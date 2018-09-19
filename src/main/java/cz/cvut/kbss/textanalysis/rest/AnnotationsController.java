package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.textanalysis.model.AnnotationsResult;
import cz.cvut.kbss.textanalysis.service.AnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnnotationsController {

    @Autowired
    private AnnotationService annotationService;

    @RequestMapping("/annotate")
    public List<AnnotationsResult> getAnnotatedTokenizedText() throws Exception {
        return annotationService.getAnnotations();
    }
}
