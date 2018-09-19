package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.model.Word;

public interface ChunkAnnotationService {

    /**
     * Accepts a plain-text chunk and returns a stream of words
     *
     * @param textChunk
     * @return
     */
    Word[] process(final String textChunk) ;
}
