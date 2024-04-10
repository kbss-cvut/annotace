/**
 * Annotace Copyright (C) 2019 Czech Technical University in Prague
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with this program.  If
 * not, see <https://www.gnu.org/licenses/>. © 2019 GitHub, Inc.
 */

package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.Constants;
import cz.cvut.kbss.textanalysis.dto.TextAnalysisInput;
import cz.cvut.kbss.textanalysis.keywordextractor.KeywordExtractorAPI;
import cz.cvut.kbss.textanalysis.keywordextractor.model.KeywordExtractorResult;
import cz.cvut.kbss.textanalysis.model.QueryResult;
import cz.cvut.kbss.textanalysis.model.Word;
import cz.cvut.kbss.textanalysis.service.html2rdfa.Annotator;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.vocabulary.SKOS;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HtmlAnnotationService {

    private final AnnotationService annotationService;

    private final OntologyService ontologyService;

    private final KeywordExtractorAPI keywordExtractionService;

    @Autowired
    public HtmlAnnotationService(AnnotationService annotationService,
                                 OntologyService ontologyService,
                                 KeywordExtractorAPI kerService) {
        this.annotationService = annotationService;
        this.ontologyService = ontologyService;
        this.keywordExtractionService = kerService;
    }


    private Set<URI> getQueryUrlsForVocabularyContext(final TextAnalysisInput input) {
        final Set<URI> uriSet = new HashSet<>();
        final String iTerm = SKOS.Concept.toString();
        if (input.getVocabularyContexts() != null) {
            for (final URI graphUri : input.getVocabularyContexts()) {
                final String uri = input.getVocabularyRepository() + "?query=" + encode(
                    "CONSTRUCT {?s ?p ?o} WHERE { GRAPH <" + graphUri + "> {?s a <" + iTerm +
                        "> .?s ?p ?o}}");
                uriSet.add(URI.create(uri));
            }
        } else {
            final String uri = input.getVocabularyRepository() + "?query=" + encode(
                "CONSTRUCT {?s ?p ?o} WHERE {?s a <" + iTerm + "> . ?s ?p ?o}");
            uriSet.add(URI.create(uri));
        }
        return uriSet;
    }

    private String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    public String annotate(Boolean enableKeywordExtraction, final TextAnalysisInput input) {
        final Set<URI> vocabularies = getQueryUrlsForVocabularyContext(input);
        final String lang = input.getLanguage();
        final List<QueryResult> queryResultList = ontologyService
            .analyzeModel(vocabularies, input.getVocabularyRepositoryUserName(), input.getVocabularyRepositoryPassword(), lang);

        final Document doc = Jsoup.parse(unwrapSpan(Jsoup.parse(input.getContent())).toString());
        final List<String> chunks = new ArrayList<>();
        final NodeVisitor chunkCollector = new ChunkIterator(chunk -> chunks.add(chunk.getWholeText()));
        NodeTraversor.traverse(chunkCollector, doc);

        final KeywordExtractorResult kerResult;
        if (enableKeywordExtraction) {
            final String documentChunksString = String.join("\r\n", chunks);
            kerResult = keywordExtractionService.process(documentChunksString);
        } else {
            kerResult = KeywordExtractorResult.createEmpty();
        }

        return this.annotate(textChunk -> {
            try {
                return annotationService.getAnnotations(textChunk, queryResultList, kerResult, lang)
                    .toArray(new Word[] {});
            } catch (Exception ex) {
                log.error("Document annotation failed.", ex);
                return new Word[] {new Word("", "", textChunk, "")};
            }
        }, doc, lang).toString();
    }

    static class ChunkIterator implements NodeVisitor {

        final Consumer<TextNode> consumer;

        ChunkIterator(Consumer<TextNode> consumer) {
            this.consumer = consumer;
        }

        public void head(Node node, int depth) {
            // nothing
        }

        public void tail(Node node, int depth) {
            if (node instanceof TextNode) {

                final TextNode textNode = (TextNode) node;
                if (!textNode.getWholeText().trim().isEmpty()) {
                    consumer.accept(textNode);
                }
            }
        }
    }

    ;

    public Document annotate(final ChunkAnnotationService p, final Document doc,
                             final String lang) {
        log.debug("Annotating document has started");
        final Document output = doc.clone();
        output.outputSettings().prettyPrint(false);
        final Element eHtml = output.selectFirst("html");
        eHtml.attr("prefix", "ddo: " + Constants.NS_TERMIT);

        final Map<TextNode, List<Node>> replaceMap = new HashMap<>();

        final Annotator a = new Annotator(lang);
        final NodeVisitor visitor = new ChunkIterator(chunk -> {
            final List<Node> newNode = a.annotate(
                p.process(chunk.getWholeText())).collect(Collectors.toList());
//            if (!(newNode.size() == 1 && chunk.getWholeText().trim().equals(newNode.iterator().next().toString()))) {
                replaceMap.put(chunk, newNode);
//            }
        });

        NodeTraversor.traverse(visitor, output);

        for (final Map.Entry<TextNode, List<Node>> e : replaceMap.entrySet()) {
            if (!(e.getKey().parentNode().nodeName().equals("span")
                && e.getKey().parentNode().attr("typeof").equals("ddo:výskyt-termu"))) {
                final Node node = e.getKey().text("");
                e.getValue().forEach(node::before);
            }
        }

        log.debug("Annotating document has finished");

        return output;
    }

    private Document unwrapSpan(Document doc) {
        Elements elements = doc.getElementsByTag("span");
        for (Element element : elements) {
            if (isTermOccurrence(element) && element.hasText()) {
                // Unwrap any suggested occurrence with score attribute to keep the annotations up-to-date with the vocabulary terms. Keep the assigned occurrences untouched.
                element.select("span[score]").unwrap();
            }
        }
        return doc;
    }

    private Boolean isTermOccurrence(Node node) {
        return (node.attr("typeof").equals(Constants.NS_TERMIT + "výskyt-termu") ||
            node.attr("typeof").equals("ddo:výskyt-termu"));
    }
}
