package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.model.Word;
import cz.cvut.kbss.textanalysis.service.AnnotationService;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnnotationsController {

    @Autowired
    private AnnotationService annotationService;

    // TODO LS : "file:C:/Projects/OPPPR/services/textanalysis/src/main/resources/glosar.ttl"

    @RequestMapping("/annotate")
    public List<Word> getAnnotatedTokenizedText(
        @RequestParam("ontologyUrl") String ontologyUrl) throws Exception {
        return annotationService.getAnnotations(new URL(ontologyUrl));
    }
}
