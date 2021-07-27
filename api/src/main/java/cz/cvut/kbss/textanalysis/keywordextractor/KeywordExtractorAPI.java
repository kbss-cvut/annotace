package cz.cvut.kbss.textanalysis.keywordextractor;

import cz.cvut.kbss.textanalysis.keywordextractor.model.KeywordExtractorResult;

public interface KeywordExtractorAPI {

    KeywordExtractorResult process(final String input);
}
