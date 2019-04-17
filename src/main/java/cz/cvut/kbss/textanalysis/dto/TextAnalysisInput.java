package cz.cvut.kbss.textanalysis.dto;

import java.net.URI;
import java.util.Set;

/**
 * Represents input passed to the text analysis service.
 * <p>
 * Mainly contains the content to analyze and identification of the vocabulary whose terms will be used in the text
 * analysis.
 */
public class TextAnalysisInput {

    /**
     * Text content to analyze.
     */
    private String content;

    /**
     * URI of the repository containing vocabulary whose terms are used in the text analysis.
     */
    private URI vocabularyRepository;

    /**
     * URI of the context containing vocabulary whose terms are used in the text analysis. Optional.
     * <p>
     * If not specified, the whole {@link #vocabularyRepository} is searched for terms.
     */
    private Set<URI> vocabularyContexts;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public URI getVocabularyRepository() {
        return vocabularyRepository;
    }

    public void setVocabularyRepository(URI vocabularyRepository) {
        this.vocabularyRepository = vocabularyRepository;
    }

    public Set<URI> getVocabularyContexts() {
        return vocabularyContexts;
    }

    public void setVocabularyContexts(Set<URI> vocabularyContexts) {
        this.vocabularyContexts = vocabularyContexts;
    }

}
