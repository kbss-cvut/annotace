package cz.cvut.kbss.html2rdfa;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public class HtmlAnnotationService {

    private ChunkProcessor p;

    public HtmlAnnotationService(ChunkProcessor p) {
        this.p = p;
    }

    public Document annotate(final Document doc) throws HtmlAnnotationException {
        try {
            return _annotate(doc);
        } catch(Exception e) {
            throw new HtmlAnnotationException("Document annotation failed.",e);
        }
    }

    private Document _annotate(final Document doc) {
        final Document output = doc.clone();
        final Element eHtml = output.selectFirst("html");
        eHtml
              .attr("prefix","ddo: http://onto.fel.cvut.cz/ontologies/slovnik/agendovy/popis-dat/pojem/");

        final Map<TextNode,Stream<Node>> replaceMap = new HashMap<>();

        final NodeVisitor visitor = new NodeVisitor() {
            public void head(Node node, int depth) {
                // nothing
            }
            public void tail(Node node, int depth) {
                if ( node instanceof TextNode ) {
                    final TextNode textNode = (TextNode) node;
                    if ( !textNode.getWholeText().trim().isEmpty()) {
                        final Stream<Node> newNode = new Annotator().annotate(
                            p.process(textNode.getWholeText())
                        );
                        replaceMap.put(textNode, newNode);
                    }
                }
            }
        };
        NodeTraversor.traverse(visitor, output);

        for(final Map.Entry<TextNode,Stream<Node>> e : replaceMap.entrySet()) {
            Node node = e.getKey().text("");
            e.getValue().forEach(node::before);
        }

        return output;
    }
}
