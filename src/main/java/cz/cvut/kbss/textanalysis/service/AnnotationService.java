package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.model.Phrase;
import cz.cvut.kbss.model.Word;
import cz.cvut.kbss.textanalysis.model.*;
import cz.cvut.kbss.textanalysis.service.morphodita.MorphoDitaServiceAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnnotationService {

    @Autowired
    private MorphoDitaServiceAPI morphoDitaService;

    public List<Word> getAnnotations(final String textChunk, final List<QueryResult> queryResultList,final KerResult result) throws AnnotationException {
        try {
            return this._getAnnotations(textChunk, queryResultList, result);
        } catch (Exception e) {
            throw new AnnotationException("Annotation failed.", e);
        }
    }

    private List<Word> _getAnnotations(final String textChunk, final List<QueryResult> queryResultList,final KerResult result) {
        final List<List<MorphoDitaResultJson>> morphoDitaResult = morphoDitaService.getMorphoDiteResultProcessed(textChunk);
        return annotateOntologieLables(morphoDitaResult, queryResultList,result);
    }

    private List<Word> annotateOntologieLables(List<List<MorphoDitaResultJson>> morphoDitaList, List<QueryResult> queryResultList,final KerResult kerResult) {

        List<Word> annotationsResults = new ArrayList<>();

        for (List<MorphoDitaResultJson> results : morphoDitaList) {
            for (MorphoDitaResultJson result : results) {

                List<Phrase> matchedAnnotations = new ArrayList<>();
                boolean isKeyword = false;
                boolean isMatched = false;

                for (QueryResult queryResults : queryResultList) {
                    for (MorphoDitaResultJson ontologyResults : queryResults.getMorphoDitaResultList()) {

                        boolean singleMatch = queryResults.getMorphoDitaResultList().size() == 1;

                        if ((result.getLemma().contentEquals(ontologyResults.getLemma())) &&
                             result.getTag().charAt(10) == ontologyResults.getTag().charAt(10)) {

                            Phrase matchedAnnotation = new Phrase(
                                    queryResults.getType(),
                                    (kerResult.getKeywords().contains(result.getLemma())
                                                    &&(result.getToken().equals(queryResults.getLabel()))),
                                    singleMatch,
                                    queryResults.getLabel());

                            if(singleMatch) {
                                    isMatched = true;
                                    }
                                    matchedAnnotations.add(matchedAnnotation);
                                }
                                if (kerResult.getKeywords().contains(result.getLemma())) {
                                    isKeyword = true;
                        }
                    }
                }

                if((isKeyword) && !(isMatched) && (matchedAnnotations.isEmpty())) {
                    Phrase matchedAnnotation = new Phrase(
                                            "",
                                            true,
                                            true,
                            "");
                                    matchedAnnotations.add(matchedAnnotation);
                }

                final MorphoDitaResultJson res = result;
                annotationsResults.add( new Word(res.getLemma(), res.getToken(), res.getSpace() == null ? "":res.getSpace(), matchedAnnotations.toArray(new Phrase[]{})) );

            }
        }

        return annotationsResults;
    }

}
