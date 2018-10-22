package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.model.KerResult;
import cz.cvut.kbss.textanalysis.model.QueryResult;
import cz.cvut.kbss.textanalysis.model.StatisticAnnotationsResult;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service public class StatisticalAnnotationService {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticalAnnotationService.class);

    @Autowired private KerService kerService;

    @Autowired private OntologyService ontologyService;

    public List<StatisticAnnotationsResult> getAnnotationServiceOutput() throws Exception {

        List<StatisticAnnotationsResult> annotationsResultsList;

        //Retrieve keywords from Ker Tool
        List<String> kerKeywordsList;
        KerResult kerResult = kerService.getKerResult("TODO REMOVE");
        kerKeywordsList = kerResult.getKeywords();

        //Retrieve ontologie types and labels
        List<QueryResult> queryResultList;
        Model model = ontologyService.readOntologyFromFile(
            "C:/Projects/OPPPR/services/textanalysis/src/main/resources/glosar.ttl");
        queryResultList = ontologyService.analyzeModel(model);

        String ontologieLabels = "";
        for (int i = 0; i < queryResultList.size(); i++) {
            ontologieLabels = ontologieLabels + queryResultList.get(i).getLabel() + "\n";
        }

        LOG.debug("string containing all ontologie labels" + "\n" + ontologieLabels);

        annotationsResultsList = matchKeywordsToOntologieTerms(kerKeywordsList, queryResultList);

        return annotationsResultsList;
    }


    public List<StatisticAnnotationsResult> matchKeywordsToOntologieTerms(List<String> kers,
                                                                          List<QueryResult> terms) {
        List<StatisticAnnotationsResult> annotationsResults = new ArrayList<>();

        for (int i = 0; i < kers.size(); i++) {
            for (int j = 0; j < terms.size(); j++) {
                if (terms.get(j).getLabel().contains(kers.get(i))) {
                    StatisticAnnotationsResult annotationsResult =
                        new StatisticAnnotationsResult(kers.get(i), terms.get(j).getType(),
                            terms.get(j).getLabel());
                    annotationsResults.add(annotationsResult);
                }
            }
        }
        return annotationsResults;
    }

}
