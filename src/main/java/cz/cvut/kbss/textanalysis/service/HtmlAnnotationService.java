package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.model.Word;
import cz.cvut.kbss.textanalysis.model.KerResult;
import cz.cvut.kbss.textanalysis.model.QueryResult;
import cz.cvut.kbss.textanalysis.service.html2rdfa.Annotator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HtmlAnnotationService {

    @Autowired
    private OntologyService ontologyService;

    @Autowired
    private KerService kerService;

    private static final Logger logger = LoggerFactory.getLogger(HtmlAnnotationService.class);

    public String annotate(@Autowired AnnotationService annotationService, String ontologyUrl, String htmlDocument) throws HtmlAnnotationException, IOException {
        try {
            final URL url = new URL(ontologyUrl);
            final List<QueryResult> queryResultList = ontologyService.analyzeModel(url);

            final Document doc =Jsoup.parse(htmlDocument);
            final List<String> chunks = new ArrayList<>();
            final NodeVisitor chunkCollector = new ChunkIterator(chunk -> chunks.add(chunk.getWholeText()));
            NodeTraversor.traverse(chunkCollector, doc);

            String documentChunksString = chunks.stream().collect(Collectors.joining(" "));
            final KerResult kerResult = kerService.getKerResult(documentChunksString.replace("\n"," "));

            return this.annotate(textChunk -> {
                try {
                    return annotationService.getAnnotations(textChunk, queryResultList, kerResult).toArray(new Word[]{});
                } catch (Exception ex) {
                    logger.error("Annotation failed.", ex);
                    return new Word[]{new Word("",textChunk,"")};
                }
            }, doc).toString();
        } catch (MalformedURLException e) {
            throw new HtmlAnnotationException("Annotation failed.", e);
        }
    }

    public Document annotate(final ChunkAnnotationService p, final Document doc)
        throws HtmlAnnotationException {
        try {
            return _annotate(p, doc);
        } catch (Exception e) {
            throw new HtmlAnnotationException("Document annotation failed.", e);
        }
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


    private Document _annotate(final ChunkAnnotationService p, final Document doc) {
        final Document output = doc.clone();
        final Element eHtml = output.selectFirst("html");
        eHtml.attr("prefix",
            "ddo: http://onto.fel.cvut.cz/ontologies/slovnik/agendovy/popis-dat/pojem/");

        final Map<TextNode, List<Node>> replaceMap = new HashMap<>();

        final Annotator a = new Annotator();
        final NodeVisitor visitor = new ChunkIterator(chunk -> {
            final List<Node> newNode = a.annotate(
                p.process(chunk.getWholeText())).collect(Collectors.toList());
            replaceMap.put(chunk, newNode);
        });

        NodeTraversor.traverse(visitor, output);

        for (final Map.Entry<TextNode, List<Node>> e : replaceMap.entrySet()) {
            if (!(e.getKey().parentNode().nodeName().equals("span")) || ((e.getKey().parentNode().nodeName().equals("span")) && !(e.getKey().parentNode().attr("typeof").equals("ddo:vyskyt-termu")))) {
                Node node = e.getKey().text("");
                e.getValue().forEach(node::before);
            }
        }

        return output;
    }
}
