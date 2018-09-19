package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.model.*;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnnotationService {

    private AnnotationsResult annotationsResult;

    @Autowired
    private OntologyService ontologyService;

    @Autowired
    private MorphoDitaService morphoDitaService;

    //Should take two parameters, string and voc
    public List<AnnotationsResult> getAnnotations() throws Exception {
        List<AnnotationsResult> annotationsResults;

        //output of Morphodita text analysis
        //to be re-moved
        String s = null;

        List<List<MorphoDitaResultJson>> morphoDitaResult;
        morphoDitaResult = morphoDitaService.getMorphoDiteResultProcessed(s);

        //output of processed ontologie lables
        //to be re-moved
        Model model = ontologyService.readOntologyFromFile("C:/Projects/OPPPR/services/textanalysis/src/main/resources/glosar.ttl");

        List<QueryResult> queryResultList;
        queryResultList = ontologyService.analyzeModel(model);

        annotationsResults = annotateOntologieLables(morphoDitaResult, queryResultList);

        return annotationsResults;


    }

    public List<AnnotationsResult> annotateOntologieLables(List<List<MorphoDitaResultJson>> morphoDitaList, List<QueryResult> queryResultList) {

        List<AnnotationsResult> annotationsResults = new ArrayList<>();

        for (int i=0; i<morphoDitaList.size(); i++) {
            for (int ii = 0; ii < morphoDitaList.get(i).size(); ii++) {

                List<MatchedAnnotation> matchedAnnotations = new ArrayList<>();
                AnnotationsResult annotationsResult = new AnnotationsResult();
                annotationsResult.setResult(morphoDitaList.get(i).get(ii));

                for (int j = 0; j < queryResultList.size(); j++) {
                    for (int k = 0; k < queryResultList.get(j).getMorphoDitaResultList().size(); k++) {
                        for (int kk = 0; kk < queryResultList.get(j).getMorphoDitaResultList().get(k).size(); kk++) {


                            if (morphoDitaList.get(i).get(ii).getLemma().contentEquals(queryResultList.get(j).getMorphoDitaResultList().get(k).get(kk).getLemma())) {

                                MatchedAnnotation matchedAnnotation = new MatchedAnnotation();
                                matchedAnnotation.setLable(queryResultList.get(j).getLabel());
                                matchedAnnotation.setType(queryResultList.get(j).getType());

                                if (morphoDitaList.get(i).get(ii).getToken().equals(queryResultList.get(j).getLabel())) {
                                    matchedAnnotation.setFullMatch(true);
                                } else {
                                    matchedAnnotation.setFullMatch(false);
                                }


                                matchedAnnotations.add(matchedAnnotation);

                                annotationsResult.setAnnotations(matchedAnnotations);


                            }

                        }
                    }
                }

                annotationsResults.add(annotationsResult);
            }
        }
        return annotationsResults;

    }
}
