package cz.cvut.kbss.textanalysis.service.html2rdfa;

import cz.cvut.kbss.model.Phrase;
import cz.cvut.kbss.model.Word;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;

public class Annotator {

    public Stream<Node> annotate(final Word[] words) {
        final List<Node> list = new ArrayList<>();

        Node currentNode = null;

        Phrase [] previousPhrases = null;
        Phrase [] currentPhrases = null;

        double score;
        double numberOfTokens = 1;

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
                            previousPhrases = null;
                        }
                    }
                    numberOfTokens = 0;
                    tn = ((TextNode) currentNode);
                } else {
                    List<String> commonPhraseIRI = new ArrayList<>();
                    currentPhrases = word.getPhrases();
                    Phrase[] newPhrases;
                    if (previousPhrases != null) {
                        Arrays.stream(previousPhrases).forEach(phrase -> commonPhraseIRI.add(phrase.getTermIri()));
                        newPhrases = Arrays.stream(currentPhrases).filter(phrase -> commonPhraseIRI.contains(phrase.getTermIri())).collect(Collectors.toList()).toArray(new Phrase[]{});
                    } else
                    {
                        newPhrases = currentPhrases;
                    }

                    if (currentNode == null) {
                        currentNode = new Element(Tag.valueOf("span"), "");
                    } else if (currentNode instanceof TextNode) {
                        list.add(currentNode);
                        currentNode = new Element(Tag.valueOf("span"), "");
                     } else if  (newPhrases.length == 0) {
                        list.add(currentNode);
                        currentNode = new Element(Tag.valueOf("span"), "");
                        numberOfTokens = 1;
                    }
                    // TODO multiple
                    if (newPhrases.length == 0) {
                        newPhrases = currentPhrases;
                    }
                    previousPhrases = newPhrases;
                    double labelCount;

                    //scoring
                    if (newPhrases.length == 0 || newPhrases[0].getTermIri() == null || newPhrases[0].getTermIri().equals("")) {
                         labelCount = numberOfTokens;
                    } else
                         labelCount = getNumberOfTokens(newPhrases [0].getTermLabel());

                    score = numberOfTokens / labelCount ;
                    annotateNode((Element) currentNode, word, newPhrases [0], score);

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
                numberOfTokens += 1;
            }

            if (currentNode != null) {
                list.add(currentNode);
            }
        }

        return list.stream();
    }

    private void annotateNode(final Element node, final Word word, final Phrase phrase, final double score) {
        node.attr("about", "_:" + phrase.hashCode());

        String iri = phrase.getTermIri();
        node.attr("property", "ddo:je-vyskytem-termu");
        if (iri != null) {
            node.attr("resource", phrase.getTermIri());
        } else {
            node.attr("content", word.getLemma());
        }
        node.attr("typeof", "ddo:vyskyt-termu");
        node.attr("score", Double.toString(score));
    }

    private int getNumberOfTokens(String string) {
        String trimmed = string.trim();
        return trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
    }
}
