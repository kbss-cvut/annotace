/**
 * Annotac
 * Copyright (C) 2019 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * © 2019 GitHub, Inc.
 */
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
