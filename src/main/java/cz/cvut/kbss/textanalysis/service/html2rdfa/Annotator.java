package cz.cvut.kbss.textanalysis.service.html2rdfa;

import cz.cvut.kbss.model.Phrase;
import cz.cvut.kbss.model.Word;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;

public class Annotator {

    public Stream<Node> annotate(final Word[] words) {
        final List<Node> list = new ArrayList<>();

        Node currentNode = null;

        Phrase previousPhrase = null;

        if (words != null) {
            for (Word word : words) {
                // TODO overlap

                final TextNode tn;

                if ((word.getPhrases() == null || word.getPhrases().length == 0)) {
                    if (currentNode == null) {
                        currentNode = new TextNode("");
                    } else {
                        if (!(currentNode instanceof TextNode)) {
                            list.add(currentNode);
                            currentNode = new TextNode("");
                        }
                    }
                    tn = ((TextNode) currentNode);
                } else {
                    if (currentNode == null) {
                        currentNode = new Element(Tag.valueOf("span"), "");
                    } else if (currentNode instanceof TextNode) {
                        list.add(currentNode);
                        currentNode = new Element(Tag.valueOf("span"), "");
                     } else if  (!(Arrays.asList(word.getPhrases()).contains(previousPhrase))) {
                        list.add(currentNode);
                        currentNode = new Element(Tag.valueOf("span"), "");
                    }
                    // TODO multiple
                    previousPhrase = word.getPhrases()[0];
                    annotateNode((Element) currentNode, word, word.getPhrases()[0]);

                    final List<TextNode> textNodes = ((Element) currentNode).textNodes();
                    if (textNodes.isEmpty()) {
                        tn = new TextNode("");
                        ((Element) currentNode).appendChild(tn);
                    } else if (textNodes.size() == 1) {
                        tn = textNodes.get(0);
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
                tn.text(tn.text() + word.getToken() + word.getStopChars());
            }

            if (currentNode != null) {
                list.add(currentNode);
            }
        }

        return list.stream();
    }

    private void annotateNode(final Element node, final Word word, final Phrase phrase) {
        node.attr("about", "_:" + phrase.hashCode());

        String iri = phrase.getTermIri();
        node.attr("property", "ddo:je-vyskytem-termu");
        if (iri != null) {
            node.attr("resource", phrase.getTermIri());
        } else {
            node.attr("content", word.getLemma());
        }
        node.attr("typeof", "ddo:vyskyt-termu");
    }
}
