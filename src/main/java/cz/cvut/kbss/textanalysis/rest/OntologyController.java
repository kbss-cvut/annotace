package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.textanalysis.model.QueryResult;
import cz.cvut.kbss.textanalysis.service.OntologyService;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
public class OntologyController {

    @Autowired
    private OntologyService ontologyService;

    @RequestMapping("/ontology")
    public List<QueryResult> getAnnotationResult(@RequestParam("ontologyUrl") Set<URI> ontologyUrl) {
        List<QueryResult> queryResultList;
        //Model model = ontologyService.readOntologyFromFile("C:/Projects/OPPPR/services/textanalysis/src/main/resources/glosar.ttl");
        queryResultList = ontologyService.analyzeModel(ontologyUrl);

        return queryResultList;
    }


}
