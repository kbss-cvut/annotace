package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.textanalysis.dto.TextAnalysisInput;
import cz.cvut.kbss.textanalysis.service.AnnotationService;
import cz.cvut.kbss.textanalysis.service.HtmlAnnotationService;

import java.net.URI;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnnotateController {

    @Autowired
    private HtmlAnnotationService service;

    @Autowired
    private AnnotationService annotationService;

    @RequestMapping(value = "/annotate", method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    public String annotate(@RequestBody TextAnalysisInput input)
        throws Exception {
        Set<URI> uriSet = new HashSet<>();
        String uri;
        String iTerm = "http://onto.fel.cvut.cz/ontologies/slovnik/agendovy/popis-dat/pojem/term";
        if (input.getVocabularyContexts() != null) {
            Set<URI> allGraphs = input.getVocabularyContexts();

            for(URI graphUri : allGraphs) {
                 uri = input.getVocabularyRepository() + "?query=" + URLEncoder.encode(
                        "CONSTRUCT {?s ?p ?o} WHERE { GRAPH <" + graphUri + "> {?s a <" +iTerm+ "> .?s ?p ?o}}");
                uriSet.add(URI.create(uri));
            }

        } else {
            uri = input.getVocabularyRepository() + "?query=" + URLEncoder.encode(
                    "CONSTRUCT {?s ?p ?o} WHERE {?s a <"+iTerm+"> . ?s ?p ?o}");
            uriSet.add(URI.create(uri));
        }


        final String htmlDocument = input.getContent();
        return service.annotate(annotationService, uriSet, htmlDocument);
    }

    @RequestMapping(value = "/annotate-html", method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    consumes = MediaType.TEXT_HTML_VALUE)

    public String annotateHtml(
        @RequestParam(value = "ontologyUrl", required = false) Set<URI> ontologyUrl,
        @RequestBody String htmlDocument)
        throws Exception {
        return service.annotate(annotationService, ontologyUrl, htmlDocument);
    }
}
