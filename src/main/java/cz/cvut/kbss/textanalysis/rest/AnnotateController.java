package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.textanalysis.dto.TextAnalysisInput;
import cz.cvut.kbss.textanalysis.service.AnnotationService;
import cz.cvut.kbss.textanalysis.service.HtmlAnnotationService;
import java.net.URLEncoder;
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
        String ontologyUrl;
        if (input.getVocabularyContext() != null) {
          ontologyUrl = input.getVocabularyRepository() + "?query=" + URLEncoder.encode(
                "CONSTRUCT {?s ?p ?o} WHERE { GRAPH <" + input.getVocabularyContext().toString()+ "> {?s ?p ?o}}");
        } else
          ontologyUrl = input.getVocabularyRepository() + "?query=" + URLEncoder.encode(
                "CONSTRUCT {?s ?p ?o} WHERE {?s ?p ?o}");
        final String htmlDocument = input.getContent();
        return service.annotate(annotationService, ontologyUrl, htmlDocument);
    }

    @RequestMapping(value = "/annotate-html", method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    consumes = MediaType.TEXT_HTML_VALUE)
    public String annotateHtml(
        @RequestParam(value = "ontologyUrl", required = false) String ontologyUrl,
        @RequestBody String htmlDocument)
        throws Exception {
        return service.annotate(annotationService, ontologyUrl, htmlDocument);
    }
}
