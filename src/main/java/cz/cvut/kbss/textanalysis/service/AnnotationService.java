package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.model.Phrase;
import cz.cvut.kbss.model.Word;
import cz.cvut.kbss.textanalysis.Stopwords;
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

    public Stopwords stopwords = new Stopwords();

    List<String> stopwordsList;

    public List<Word> getAnnotations(final String textChunk, final List<QueryResult> queryResultList,final KerResult result, String lang) throws AnnotationException {
        try {
            return this._getAnnotations(textChunk, queryResultList, result, lang);
        } catch (Exception e) {
            throw new AnnotationException("Annotation failed.", e);
        }
    }

    private List<Word> _getAnnotations(final String textChunk, final List<QueryResult> queryResultList,final KerResult result, String lang) {
        final List<List<MorphoDitaResultJson>> morphoDitaResult = morphoDitaService.getMorphoDiteResultProcessed(textChunk, lang);
        return annotateOntologieLables(morphoDitaResult, queryResultList,result, lang);
    }

    private List<Word> annotateOntologieLables(List<List<MorphoDitaResultJson>> morphoDitaList, List<QueryResult> queryResultList,final KerResult kerResult, String lang) {

        List<Word> annotationsResults = new ArrayList<>();

        stopwordsList = stopwords.getStopwords(lang);

        for (List<MorphoDitaResultJson> results : morphoDitaList) {
            for (MorphoDitaResultJson result : results) {

                List<Phrase> matchedAnnotations = new ArrayList<>();
                boolean isKeyword = kerResult.getKeywords().contains(result.getLemma());
                boolean isStopword = stopwordsList.contains(result.getLemma());
                boolean isMatched = false;
                boolean isNotNegation;

                for (QueryResult queryResults : queryResultList) {
                    for (MorphoDitaResultJson ontologyResults : queryResults.getMorphoDitaResultList()) {

                        isMatched = queryResults.getMorphoDitaResultList().size() == 1;

                        isNotNegation = checkForNegation(result.getTag(), ontologyResults.getTag());

                        if ((result.getLemma().contentEquals(ontologyResults.getLemma())
                                || result.getToken().toLowerCase().contentEquals(ontologyResults.getToken().toLowerCase())
                        || result.getLemma().toLowerCase().contentEquals(ontologyResults.getToken().toLowerCase())) &&
                             isNotNegation) {

                            Phrase matchedAnnotation = new Phrase(
                                    queryResults.getType(),
                                    (kerResult.getKeywords().contains(result.getLemma())
                                                    &&(result.getToken().equals(queryResults.getLabel()))),
                                    isMatched,
                                    queryResults.getLabel(), queryResults.getPropertyName());

                                    matchedAnnotations.add(matchedAnnotation);
                                }
                    }
                }

                if((matchedAnnotations.isEmpty()) && (isKeyword) && !(isStopword)) {
                    Phrase matchedAnnotation = new Phrase(
                                            "",
                                            true,
                                            true,
                            "", "");
                                    matchedAnnotations.add(matchedAnnotation);
                }

                final MorphoDitaResultJson res = result;
                annotationsResults.add( new Word(res.getLemma(), res.getToken(), res.getSpace() == null ? "":res.getSpace(), matchedAnnotations.toArray(new Phrase[]{})) );

            }
        }

        return annotationsResults;
    }

    private boolean checkForNegation(String s, String r) {
        if (s.length() > 10)
            return (s.charAt(10) == r.charAt(10));
        else
            return true;
    }

}
