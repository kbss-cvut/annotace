package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.textanalysis.service.AnnotationService;
import cz.cvut.kbss.textanalysis.service.HtmlAnnotationService;
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
                    produces = MediaType.TEXT_HTML_VALUE,
                    consumes = {MediaType.TEXT_HTML_VALUE,MediaType.TEXT_PLAIN_VALUE})
    public String annotate(
        @RequestParam("ontologyUrl") String ontologyUrl,
        @RequestBody String htmlDocument)
        throws Exception {
        return service.annotate(annotationService, ontologyUrl, htmlDocument);
    }
}
