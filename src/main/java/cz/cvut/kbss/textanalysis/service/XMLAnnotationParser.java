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
package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.model.MorphoDitaResultJson;
import org.jsoup.Jsoup;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLAnnotationParser {

    XMLAnnotationService xmlAnnotationService = new XMLAnnotationService();

    public void paresContent() throws ParserConfigurationException {
        //Initialize xml document
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        final List<String> COMP = new ArrayList<String>(Arrays.asList("zahrnuje","zahrnovat", "tvořený", "skládající", "členěno", "členit", "rozděluje", "rozdělovat"));
        final List<String> COMPR = new ArrayList<String>(Arrays.asList("vyskytující", "tvoří", "tvořit", "součástí", "součást"));
        final List<String> CATV = new ArrayList<String>(Arrays.asList("rozlišuje", "rozlišovat", "člení", "vymezují", "vymezovat", "rozlišením", "rozlišení", "zejména"));
        final List<String> CN = new ArrayList<String>(Arrays.asList("typy", "základní typy", "typ"));
        final List<String> SYN = new ArrayList<String>(Arrays.asList("synonym", "syn", "ekvivalent", "ekvivalentem"));
        final List<String> PROP = new ArrayList<String>(Arrays.asList("přiřazen", "přiřazena", "přiřadit"));
        final List<String> BE = new ArrayList<String>(Arrays.asList("být", "bude", "budou", "byl", "byt", "je", "jsou"));
        final List<String> CNR = new ArrayList<String>(Arrays.asList("takový", "takové", "taková"));
        final List<String> PUNC = new ArrayList<String>(Arrays.asList("."));

        int labelCount=0;

        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("MPP-allgraphs-dev-annotated-noStopwords.html").toURI())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String processedContent = xmlAnnotationService.stripSpans(content);

        processedContent = processedContent.replaceAll("\\(", "\\(").replaceAll("</u>", " ").trim();

        String cleanDoc = processedContent.replaceAll("<span.*?\">", " ").replaceAll("</span>", " ");

        List<List<MorphoDitaResultJson>> analyzedDocList = xmlAnnotationService.morphoAnalyze(cleanDoc);

        Element tokenCollection = doc.createElement("TokenCollection");
        doc.appendChild(tokenCollection);

        Attr type = doc.createAttribute("Type");
        type.setValue("Content");
        tokenCollection.setAttributeNode(type);

        for (List<MorphoDitaResultJson> list1 : analyzedDocList) {

            for (int i=0; i<list1.size(); i++) {
                if(labelCount != 0) {
                    i = i+labelCount - 1;
                    labelCount = 0;
                }

                String token = list1.get(i).getToken();
                String tag = transformTag(list1.get(i).getTag().substring(0, 2));
                String lemma = stripLemma(list1.get(i).getLemma());
                if (processedContent.startsWith(token)) {

                    Element xmltoken;
                    //xmltoken = createToken(doc, tokenCollection, lemma);
                    xmltoken = createToken(doc, tokenCollection, token);
                    createFeature(doc, xmltoken, "SynCategory", tag);

                    if (COMP.contains(token))
                        createFeature(doc, xmltoken, "Concept", "http://www.hermes.com/knowledgebase.owl#COMP");
                    else if (COMPR.contains(token))
                        createFeature(doc, xmltoken, "Concept", "http://www.hermes.com/knowledgebase.owl#COMPR");
                    else if (CATV.contains(token))
                        createFeature(doc, xmltoken, "Concept", "http://www.hermes.com/knowledgebase.owl#CATV");
                    else if (CN.contains(token))
                        createFeature(doc, xmltoken, "Concept", "http://www.hermes.com/knowledgebase.owl#CN");
                    else if (SYN.contains(token))
                        createFeature(doc, xmltoken, "Concept", "http://www.hermes.com/knowledgebase.owl#SYN");
                    else if (PROP.contains(token))
                        createFeature(doc, xmltoken, "Concept", "http://www.hermes.com/knowledgebase.owl#PROP");
                    else if (BE.contains(token))
                        createFeature(doc, xmltoken, "Concept", "http://www.hermes.com/knowledgebase.owl#BE");
                    else if (CNR.contains(token))
                        createFeature(doc, xmltoken, "Concept", "http://www.hermes.com/knowledgebase.owl#CNR");
                    else if (PUNC.contains(token))
                        createFeature(doc, xmltoken, "Concept", "http://www.hermes.com/knowledgebase.owl#PUNC");



                    processedContent = processedContent.replaceFirst(Pattern.quote(token), Matcher.quoteReplacement("")).trim();
                }
                else {
                    if (processedContent.startsWith("<span")) {
                        final org.jsoup.nodes.Document spanDoc =Jsoup.parse(processedContent);
                        org.jsoup.nodes.Element element = spanDoc.selectFirst("span");
                        String spanValue = element.attr("resource");

                        Element xmlToken = createToken(doc, tokenCollection, element.text());

                        if (spanValue.contains("ufo")) {
                            createFeature(doc, xmlToken, "UFOConcept", spanValue);
                            createFeature(doc, xmlToken, "UFOConcept", "http://www.hermes.com/knowledgebase.owl#Concept");
                        }
                        else {
                            createFeature(doc, xmlToken, "Concept", spanValue);
                            createFeature(doc, xmlToken, "Concept", "http://www.hermes.com/knowledgebase.owl#Concept");
                        }

                        //termTag = termTag + list1.get(i).getTag().substring(0, 2);
                        String[] words = element.text().split("\\s+");
                        labelCount = words.length;
                        //int counter = labelCount;
                        String termTag;
                        if (labelCount > 1)
                            termTag = "NNP";
                        else
                            termTag = tag;

                        createFeature(doc, xmlToken, "SynCategory", termTag);
                        processedContent = processedContent.replaceFirst("<span.*?>", "").replaceFirst(".*?</span>", " ").trim();
                    }
                }
            }
        }

        transformXml(doc);
        System.out.println(doc.toString());
    }

    public Element createToken(Document doc, Element root, String token) {
        Element xmltoken = doc.createElement("Token");
        root.appendChild(xmltoken);
        Attr value = doc.createAttribute("Value");
        Attr endOffset = doc.createAttribute("EndOffset");
        endOffset.setValue("0");
        Attr startOffset = doc.createAttribute("StartOffset");
        startOffset.setValue("0");
        value.setValue(token);
        xmltoken.setAttributeNode(value);
        xmltoken.setAttributeNode(endOffset);
        xmltoken.setAttributeNode(startOffset);

        return xmltoken;
    }

    public Element createFeature(Document doc, Element parentToken, String type, String value) {
        Element feature = doc.createElement("Feature");
        parentToken.appendChild(feature);

        Attr featureType = doc.createAttribute("Type");
        Attr featureValue = doc.createAttribute("Value");
        feature.setAttributeNode(featureType);
        feature.setAttributeNode(featureValue);
        featureType.setValue(type);
        featureValue.setValue(value);

        return feature;
    }

    public void transformXml(Document doc) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new File(XMLAnnotationParser.class.getResource("xmlAnnotations.xml").getFile()));

        try {
            transformer.transform(domSource, streamResult);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        System.out.println("xml file is created");
    }

    public String transformTag(String tag) {
        String newTag = tag;
        // PP: personal pronoun, NN: noun, VB: verb,
        if (tag.contains("C")) newTag = "CD";
            else if (tag.contains("R") | (tag.contains(","))) newTag = "IN"; //preposition or subordering conjunction do, z, aby, kdyby, etc.
            else if (tag.equals("AA")) newTag = "JJ"; //Adjective
            else if (tag.equals("Dg")) newTag = "RBR"; //RBR here represents comparative or superlative adverb, ex. druhotně, vzájemně, územně, stejně, průběžně, samostatně, bezprostředně, obecně, etc.
            else if (tag.equals("Db")) newTag = "RB"; //Adverb without a possibility to form negation and degrees of comparison, ex. tak, zejména, Zároveň, nejen, pouze, především, také, dále, etc.
            else if (tag.equals("TT")) newTag = "RP"; // li, jen, nikoli[v], až, ovšem, tedy, etc.
            else if (tag.equals("II")) newTag = "UH"; //interjection
            else if (tag.equals("Vs") || tag.equals("Vp")) newTag = "VBN"; //Verb, past participle ex. je kladen, je pořízen, etc.
            else if (tag.equals("Vf")) newTag = "VB"; //Verb, infinitive
            else if (tag.contains("V")) newTag = "VB";
            else if (tag.equals("PS")) newTag = "WP$"; //Pronoun possessive
            else if (tag.contains("^")) newTag = "CC"; //Conjunction (connecting main clauses, not subordinate) ex. nebo
            else if (tag.equals("Z:")) newTag = "DT"; //Punctuation
            else if (tag.equals("P7")) newTag = "PRP"; //reflexive se

        return newTag;
    }

    public String stripLemma(String lemma) {
        if (lemma.contains("_")) {
            return lemma.substring(0, lemma.indexOf("_"));
        } else
        if (lemma.contains("-")) {
            return lemma.substring(0, lemma.indexOf("-"));
        }
        else
            return lemma;

    }
}
