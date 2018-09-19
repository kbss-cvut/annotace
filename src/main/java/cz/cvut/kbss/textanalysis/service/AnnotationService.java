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


    public List<Word> getAnnotations(final String textChunk, final URL ontologyURL) throws AnnotationException {
        try {
            return this._getAnnotations(textChunk, ontologyURL);
        } catch (Exception e) {
            throw new AnnotationException("Annotation failed.", e);
        }
    }

    private List<Word> _getAnnotations(final String textChunk, final URL ontologyURL) throws IOException {
        final List<QueryResult> queryResultList = ontologyService.analyzeModel(ontologyURL);
        final List<List<MorphoDitaResultJson>> morphoDitaResult = morphoDitaService.getMorphoDiteResultProcessed(textChunk);
        return annotateOntologieLables(morphoDitaResult, queryResultList);
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
                annotationsResults.add( new Word(res.getToken(), res.getSpace() == null ? "":res.getSpace(), matchedAnnotations.toArray(new Phrase[]{})) );
            }
        }
        System.out.println("Annotation results:" + annotationsResults);
        return annotationsResults;
    }
}
