package cz.cvut.kbss.html2rdfa;

import cz.cvut.kbss.model.Word;

public interface ChunkProcessor {

    /**
     * Accepts a plain-text chunk and returns a stream of words
     *
     * @param textChunk
     * @return
     */
    Word[] process(final String textChunk);
}
