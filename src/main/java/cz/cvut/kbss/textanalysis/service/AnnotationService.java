package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.model.Phrase;
import cz.cvut.kbss.model.Word;
import cz.cvut.kbss.textanalysis.Stopwords;
import cz.cvut.kbss.textanalysis.model.*;
import cz.cvut.kbss.textanalysis.service.morphodita.MorphoDitaServiceAPI;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnotationService {

    @Autowired
    private MorphoDitaServiceAPI morphoDitaService;

    public Stopwords stopwords = new Stopwords();

    List<String> stopwordsList = stopwords.getStopwords();

    public List<Word> getAnnotations(final String textChunk, final List<QueryResult> queryResultList,final KerResult result) throws AnnotationException {
        try {
            return this._getAnnotations(textChunk, queryResultList, result);
        } catch (Exception e) {
            throw new AnnotationException("Annotation failed.", e);
        }
    }

    private List<Word> _getAnnotations(final String textChunk, final List<QueryResult> queryResultList,final KerResult result) throws IOException {
        final List<List<MorphoDitaResultJson>> morphoDitaResult = morphoDitaService.getMorphoDiteResultProcessed(textChunk);
        return annotateOntologieLables(morphoDitaResult, queryResultList,result);
    }

    private List<Word> annotateOntologieLables(List<List<MorphoDitaResultJson>> morphoDitaList, List<QueryResult> queryResultList,final KerResult result) {

        List<Word> annotationsResults = new ArrayList<>();

        for (int i=0; i<morphoDitaList.size(); i++) {
            for (int ii = 0; ii < morphoDitaList.get(i).size(); ii++) {

                List<Phrase> matchedAnnotations = new ArrayList<>();
                boolean isKeyword = false;
                boolean isMatched = false;

                for (int j = 0; j < queryResultList.size(); j++) {
                    for (int k = 0; k < queryResultList.get(j).getMorphoDitaResultList().size(); k++) {

                boolean singleMatch = (queryResultList.get(j).getMorphoDitaResultList().size() == 1);
                            if (!stopwordsList.contains(morphoDitaList.get(i).get(ii).getToken())) {
                                if(morphoDitaList.get(i).get(ii).getLemma().contentEquals(queryResultList.get(j).getMorphoDitaResultList().get(k).getLemma())) {
                                    Phrase matchedAnnotation = new Phrase(
                                            queryResultList.get(j).getType(),
                                            (result.getKeywords().contains(morphoDitaList.get(i).get(ii).getLemma()))
                                                    &&(morphoDitaList.get(i).get(ii).getToken().equals(queryResultList.get(j).getLabel())),
                                            singleMatch,
                                            queryResultList.get(j).getLabel());
                                    if(singleMatch) {
                                    isMatched = true;
                                    }
                                    matchedAnnotations.add(matchedAnnotation);
                                }
                                if (result.getKeywords().contains(morphoDitaList.get(i).get(ii).getLemma())) {
                                    isKeyword = true;
                                }
                            }
                    }
                }
                if((isKeyword) && !(isMatched)) {
                    Phrase matchedAnnotation = new Phrase(
                                            "",
                                            true,
                                            true,
                            "");
                                    matchedAnnotations.add(matchedAnnotation);
                }

                final MorphoDitaResultJson res = morphoDitaList.get(i).get(ii);
                annotationsResults.add( new Word(res.getLemma(), res.getToken(), res.getSpace() == null ? "":res.getSpace(), matchedAnnotations.toArray(new Phrase[]{})) );
            }
        }
        return annotationsResults;
    }

}
