/**
 * Annotac
 * Copyright (C) 2019 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * © 2019 GitHub, Inc.
 */
package cz.cvut.kbss.textanalysis.service.html2rdfa;

import cz.cvut.kbss.textanalysis.model.Phrase;
import cz.cvut.kbss.textanalysis.model.Word;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import cz.cvut.kbss.model.Phrase;
import cz.cvut.kbss.model.Word;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cz.cvut.kbss.textanalysis.Stopwords;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;

public class Annotator {

    private int i = 0;

    public Stopwords stopwords = new Stopwords();

    private String uniqueId = generateID();

    List<String> stopwordsList;



    public Annotator(String lang) {
        stopwordsList = stopwords.getStopwords(lang);
    }

    public Stream<Node> annotate(final Word[] words) {
        final List<Node> list = new ArrayList<>();

        Node currentNode = null;

        Phrase [] previousPhrases = null;
        Phrase [] currentPhrases;
        StringBuilder content = new StringBuilder();

        double score;
        double numberOfTokens = 1;
        boolean previousWordisStopword = false;

        if (words != null) {
            for (Word word : words) {
                // TODO overlap

                final TextNode tn;

                if ((word.getPhrases() == null || word.getPhrases().length == 0) || ((currentNode instanceof TextNode || currentNode == null) && (isStopword(word.getToken())))) {
                    if (currentNode == null) {
                        currentNode = new TextNode("");

                    } else {
                        if (!(currentNode instanceof TextNode)) {
                            if (!previousWordisStopword)
                                list.add(currentNode);
                            else {
                                list.add(currentNode.childNode(0));
                            }
                            currentNode = new TextNode("");
                            previousPhrases = null;
                            content = new StringBuilder();
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
                        currentNode = createEmptySpanNode();
                    } else if (currentNode instanceof TextNode) {
                        list.add(currentNode);
                        currentNode = createEmptySpanNode();
                     } else if  (newPhrases.length == 0) {
                        if (!previousWordisStopword) {
                        list.add(currentNode);
                        content = new StringBuilder();
                        }
                        else {
                            list.add(currentNode.childNode(0));
                            content = new StringBuilder();
                        }
                        currentNode = createEmptySpanNode();
                        numberOfTokens = 1;
                    }
                    // TODO multiple
                    if (newPhrases.length == 0) {
                        newPhrases = currentPhrases;
                    }
                    previousPhrases = newPhrases;
                    double labelCount;

                    if (newPhrases.length > 1)
                    newPhrases = sortArrayOfPhrasesLabelLength(newPhrases);
                    Phrase matchedPhrase = newPhrases[0];
                    if (matchedPhrase.getTermIri() == null || matchedPhrase.getTermIri().equals("")) {
                         labelCount = numberOfTokens;
                    } else
                         labelCount = getNumberOfTokens(matchedPhrase.getTermLabel());

                    score = numberOfTokens / labelCount ;
                    if (score > 1) score = 1.0;
                    content.append(parseLemma(word.getLemma())).append(" ");
                    annotateNode((Element) currentNode, content.trim(), matchedPhrase, Math.round(100 * score ) / (double) 100,i++);

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
                previousWordisStopword = isStopword(word.getToken());
                numberOfTokens += 1;
            }

            if (currentNode != null) {
                if (previousWordisStopword && !(currentNode instanceof TextNode))
                    list.add(currentNode.childNode(0));
                else
                    list.add(currentNode);
            }
        }

        return list.stream();
    }

    private void annotateNode(final Element node, final String content, final Phrase phrase, final double score, int i) {
        node.attr("about", "_:" + uniqueId + i);
        String iri = phrase.getTermIri();
        node.attr("property", "ddo:je-výskytem-termu");
        if ((iri != null) && (!iri.equals(""))) {
            node.attr("resource", phrase.getTermIri());
        } else {
            node.attr("content", content);
        }
        node.attr("typeof", "ddo:výskyt-termu");
        node.attr("score", Double.toString(score));
    }

    private int getNumberOfTokens(String string) {
        String trimmed = string.trim().replace("-", " - ");
        return trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
    }

    private Phrase [] sortArrayOfPhrasesLabelLength (Phrase [] phraseList) {
        Phrase [] sortedArray = Arrays.stream(phraseList).sorted(Comparator.comparingInt(x -> x.getTermLabel().length())).collect(Collectors.toList()).toArray(new Phrase[]{});
        return sortedArray;
    }

    private boolean isStopword(String s){
        return stopwordsList.contains(s);
    }

    private String parseLemma(String s) {
        String [] parts = s.split("[-_]");
        if (!(parts.length < 1))
            return parts[0];
        else return s;

    }

    private Element createEmptySpanNode() {
        return new Element(Tag.valueOf("span"), "");
    }

    public String generateID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0, 4).concat("-");
    }

}
