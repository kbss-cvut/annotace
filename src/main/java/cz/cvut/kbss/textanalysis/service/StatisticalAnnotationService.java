package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.model.StatisticAnnotationsResult;
import cz.cvut.kbss.textanalysis.model.KerResult;
import cz.cvut.kbss.textanalysis.model.QueryResult;
import cz.cvut.kbss.textanalysis.service.morphodita.MorphoDitaServiceAPI;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticalAnnotationService {

//    @Autowired
//    private MorphoDitaService morphoDitaService;

    @Autowired
    private KerService kerService;

    @Autowired
    private OntologyService ontologyService;

    private MorphoDitaServiceAPI morphoDitaService;

    //private StatisticAnnotationsResult annotationsResult;

    public List<StatisticAnnotationsResult> getAnnotationServiceOutput() throws Exception {

        List<StatisticAnnotationsResult> annotationsResultsList;
        //        MorphoDitaResult morphoDitaResult = morphoDitaService.getResult();

        //Retrieve keywords from Ker Tool
        List<String> kerKeywordsList;
        KerResult kerResult = kerService.getKerResult("TODO REMOVE");
        kerKeywordsList = kerResult.getKeywords();

        //Retrieve ontologie types and labels
        List<QueryResult> queryResultList;
        Model model = ontologyService.readOntologyFromFile("C:/Projects/OPPPR/services/textanalysis/src/main/resources/glosar.ttl");
        queryResultList = ontologyService.analyzeModel(model);




        String ontologieLables = "";
        for(int i=0; i<queryResultList.size(); i++) {
            ontologieLables = ontologieLables + queryResultList.get(i).getLabel() + "\n";
        }

        System.out.println("string containing all ontologie lables" + "\n" + ontologieLables);



        annotationsResultsList = matchKeywordsToOntologieTerms(kerKeywordsList, queryResultList);

        return annotationsResultsList;
    }


    public List<StatisticAnnotationsResult> matchKeywordsToOntologieTerms(List<String> kers, List<QueryResult> terms) {
        List<StatisticAnnotationsResult> annotationsResults = new ArrayList<>();

        for (int i=0; i<kers.size(); i++) {
            for (int j=0; j<terms.size(); j++){
                if (terms.get(j).getLabel().contains(kers.get(i))) {
                    StatisticAnnotationsResult annotationsResult = new StatisticAnnotationsResult(kers.get(i), terms.get(j).getType(), terms.get(j).getLabel());
                    annotationsResults.add(annotationsResult);
                }
            }
        }
        return annotationsResults;
    }

}
