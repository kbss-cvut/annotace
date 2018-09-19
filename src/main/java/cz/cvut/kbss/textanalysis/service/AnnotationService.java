package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.model.Phrase;
import cz.cvut.kbss.model.Word;
import cz.cvut.kbss.textanalysis.model.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnnotationService {

    @Autowired
    private OntologyService ontologyService;

    @Autowired
    private MorphoDitaService morphoDitaService;

    //Should take two parameters, string and voc
    public List<Word> getAnnotations(final URL ontologyURL) throws AnnotationException {
        try {
            return this._getAnnotations(ontologyURL);
        } catch (Exception e) {
            throw new AnnotationException("Annotation failed.", e);
        }
    }

    private List<Word> _getAnnotations(final URL ontologyURL) throws IOException {
        List<Word> annotationsResults;

        //output of Morphodita text analysis
        //to be re-moved
        String s = Files.readAllLines(Paths.get("/home/kremep1/fel/projects/17opppr/czech-text-analysis/src/main/resources/test.txt")).stream().collect(
            Collectors.joining(""+Character.LINE_SEPARATOR));

        List<List<MorphoDitaResultJson>> morphoDitaResult;
        morphoDitaResult = morphoDitaService.getMorphoDiteResultProcessed(s);

        //output of processed ontologie lables
        //to be re-moved

        Model model = ontologyService.readOntology(ontologyURL);

        List<QueryResult> queryResultList;
        queryResultList = ontologyService.analyzeModel(model);

        annotationsResults = annotateOntologieLables(morphoDitaResult, queryResultList);

        return annotationsResults;
    }

    private List<Word> annotateOntologieLables(List<List<MorphoDitaResultJson>> morphoDitaList, List<QueryResult> queryResultList) {

        List<Word> annotationsResults = new ArrayList<>();

        for (int i=0; i<morphoDitaList.size(); i++) {
            for (int ii = 0; ii < morphoDitaList.get(i).size(); ii++) {

                List<Phrase> matchedAnnotations = new ArrayList<>();

                for (int j = 0; j < queryResultList.size(); j++) {
                    for (int k = 0; k < queryResultList.get(j).getMorphoDitaResultList().size(); k++) {
                        for (int kk = 0; kk < queryResultList.get(j).getMorphoDitaResultList().get(k).size(); kk++) {


                            if (morphoDitaList.get(i).get(ii).getLemma().contentEquals(queryResultList.get(j).getMorphoDitaResultList().get(k).get(kk).getLemma())) {

                                Phrase matchedAnnotation = new Phrase(
                                    queryResultList.get(j).getType(),
                                    false, // TODO keyword => important=true
                                    morphoDitaList.get(i).get(ii).getToken().equals(queryResultList.get(j).getLabel())
                                );
//                                matchedAnnotation.setLable(queryResultList.get(j).getLabel());
//                                matchedAnnotation.setType(queryResultList.get(j).getType());
//
//                                if (morphoDitaList.get(i).get(ii).getToken().equals(queryResultList.get(j).getLabel())) {
//                                    matchedAnnotation.setFullMatch(true);
//                                } else {
//                                    matchedAnnotation.setFullMatch(false);
//                                }

                                matchedAnnotations.add(matchedAnnotation);
                            }
                        }
                    }
                }

                final MorphoDitaResultJson res = morphoDitaList.get(i).get(ii);
                annotationsResults.add( new Word(res.getToken(), res.getSpace(), matchedAnnotations.toArray(new Phrase[]{})) );
            }
        }
        return annotationsResults;
    }
}
