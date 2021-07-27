/**
 * Annotace Copyright (C) 2019 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If
 * not, see <https://www.gnu.org/licenses/>. © 2019 GitHub, Inc.
 */

package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.Constants;
import cz.cvut.kbss.textanalysis.keywordextractor.model.KeywordExtractorResult;
import cz.cvut.kbss.textanalysis.model.QueryResult;
import cz.cvut.kbss.textanalysis.model.Word;
import cz.cvut.kbss.textanalysis.service.html2rdfa.Annotator;
import cz.cvut.kbss.textanalysis.keywordextractor.KeywordExtractorAPI;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HtmlAnnotationService {

    private AnnotationService annotationService;

    private OntologyService ontologyService;

    private KeywordExtractorAPI keywordExtractionService;

    @Autowired
    public HtmlAnnotationService(AnnotationService annotationService,
                                 OntologyService ontologyService,
                                 KeywordExtractorAPI kerService) {
        this.annotationService = annotationService;
        this.ontologyService = ontologyService;
        this.keywordExtractionService = kerService;
    }

    public String annotate(Set<URI> vocabularies, String htmlDocument)
        throws HtmlAnnotationException {
        final List<QueryResult> queryResultList = ontologyService.analyzeModel(vocabularies);

        final Document doc = Jsoup.parse(htmlDocument);
        final List<String> chunks = new ArrayList<>();
        final NodeVisitor chunkCollector =
            new ChunkIterator(chunk -> chunks.add(chunk.getWholeText()));
        NodeTraversor.traverse(chunkCollector, doc);

        final String documentChunksString = chunks.stream().collect(Collectors.joining("\r\n"));
        final KeywordExtractorResult kerResult = keywordExtractionService.process(documentChunksString);

        return this.annotate(textChunk -> {
            try {
                return annotationService.getAnnotations(textChunk, queryResultList, kerResult)
                    .toArray(new Word[] {});
            } catch (Exception ex) {
                log.error("Document annotation failed.", ex);
                return new Word[] {new Word("", textChunk, "")};
            }
        }, doc).toString();
    }

    class ChunkIterator implements NodeVisitor {

        final Consumer<TextNode> consumer;

        ChunkIterator(Consumer<TextNode> consumer)  {
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
    };

    public Document annotate(final ChunkAnnotationService p, final Document doc) {
        log.debug("Annotating document has started");
        final Document output = doc.clone();
        final Element eHtml = output.selectFirst("html");
        eHtml.attr("prefix",
            "ddo: "+ Constants.NS_TERMIT);

        final Map<TextNode, List<Node>> replaceMap = new HashMap<>();

        final Annotator a = new Annotator();
        final NodeVisitor visitor = new ChunkIterator(chunk -> {
            final List<Node> newNode = a.annotate(
                p.process(chunk.getWholeText())).collect(Collectors.toList());
            replaceMap.put(chunk, newNode);
        });

        NodeTraversor.traverse(visitor, output);

        for (final Map.Entry<TextNode, List<Node>> e : replaceMap.entrySet()) {
            if (!(e.getKey().parentNode().nodeName().equals("span")) || (
                (e.getKey().parentNode().nodeName().equals("span")) && !(e.getKey().parentNode()
                    .attr("typeof").equals("ddo:výskyt-termu")))) {
                Node node = e.getKey().text("");
                e.getValue().forEach(node::before);
            }
        }

        log.debug("Annotating document has finished");

        return output;
    }
}
