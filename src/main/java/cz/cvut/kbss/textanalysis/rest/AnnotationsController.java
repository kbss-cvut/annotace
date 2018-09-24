package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.model.Word;
import cz.cvut.kbss.textanalysis.model.KerResult;
import cz.cvut.kbss.textanalysis.model.QueryResult;
import cz.cvut.kbss.textanalysis.service.AnnotationService;
import java.net.URL;

import cz.cvut.kbss.textanalysis.service.OntologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnnotationsController {

    @Autowired
    private AnnotationService annotationService;

    @Autowired
    private OntologyService ontologyService;

    // TODO LS : "file:C:/Projects/OPPPR/services/textanalysis/src/main/resources/glosar.ttl"
    //output of Morphodita text analysis
    //to be re-moved
    //    String textChunk = Files.readAllLines(Paths.get("/home/kremep1/fel/projects/17opppr/czech-text-analysis/src/main/resources/test.txt")).stream().collect(
    //        Collectors.joining(""+Character.LINE_SEPARATOR));

    @RequestMapping("/annotate-raw")
    public List<Word> getAnnotatedTokenizedText(
        @RequestParam("ontologyUrl") String ontologyUrl, @RequestParam("textChunk") String textChunk) throws Exception {
        List<QueryResult> queryResultList = ontologyService.analyzeModel(new URL(ontologyUrl));
        return annotationService.getAnnotations(textChunk, queryResultList, KerResult.createSomeList());
    }
}
