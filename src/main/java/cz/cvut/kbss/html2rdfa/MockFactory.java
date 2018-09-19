package cz.cvut.kbss.html2rdfa;

import cz.cvut.kbss.model.Word;
import cz.cvut.kbss.model.Phrase;
import java.util.LinkedHashMap;
import java.util.Map;
import org.mockito.Mock;
import org.mockito.Mockito;

public class MockFactory {

    @Mock
    private static ChunkProcessor processor;

    public static final Map<String,Word[]> chunks = new LinkedHashMap<>();

    static {
        // DUMMY CHUNK 0
        final Phrase pT = new Phrase("http://test.org/pojem/Test", false, true);
        final Word[] chunk0Annotations = new Word[] {new Word("Test", "", pT)};
        chunks.put("Test", chunk0Annotations);

        // DUMMY CHUNK 1
        final Phrase pMP = new Phrase("http://test.org/pojem/metropolitni-plan", false, true);
        final Phrase pD = new Phrase(null, true, true);
        final Word[] chunk1Annotations = new Word[] {new Word("Metropolitni", " ",pMP),
                                        new Word("plan", "  ", pMP),
                                        new Word("je", " "),
                                        new Word( "dulezitym", " "),
                                        new Word( "dokumentem", " ",pD)};
        chunks.put("Metropolitni plan je\tdulezitym\ndokumentem.", chunk1Annotations);

        // DUMMY CHUNK 2
        final Phrase pZU = new Phrase("http://test.org/pojem/zastavene-uzemi", true, true);
        final Word[] chunk2Annotations = new Word[] {new Word( "Zastavene", " ", pZU),
                                   new Word("uzemi", " ", pZU)};
        chunks.put("Zastavene uzemi", chunk2Annotations);

        // MPP CHUNK
        final String chunk = "\n"
                             + "     (2)\tMetropolitní plán je pořízen pro stanovení koncepce "
                             + "celého správního území Prahy, a to v měřítku 1:10.000 "
                             + "odpovídajícím rozsahu řešeného území.\n"
                             + "    ";
        final Phrase pMPX = new Phrase("http://test.org/pojem/metropolitni-plan", true, true);
        final Phrase pU = new Phrase("http://test.org/pojem/uzemi", true, true);
        final Phrase pSUP = new Phrase("http://test.org/pojem/spravni-uzemi-prahy", true, true);
        final Phrase pM = new Phrase(null, true, true);
        final Word[] chunk3Annotations = new Word[] {
            new Word( "(2)", " "),
            new Word( "Metropolitní", " ", pMPX),
            new Word( "plán", " ", pMPX),
            new Word("je", " "),
            new Word( "pořízen", " "),
            new Word( "pro", " "),
            new Word( "stanovení", " "),
            new Word( "koncepce", " "),
            new Word( "celého", " "),
            new Word( "správního", " ", pSUP),
            new Word( "území", " ", pU, pSUP),
            new Word( "Prahy", ". ", pSUP),
            new Word("a", " "),
            new Word( "to", " "),
            new Word( "v", " "),
            new Word( "měřítku", " ", pM),
            new Word( "1:10.000", " "),
            new Word( "odpovídajícím", " "),
            new Word( "rozsahu", " "),
            new Word( "řešeného", " "),
            new Word( "území", " ", pU),
            };
        chunks.put(chunk, chunk3Annotations);

        processor = Mockito.mock(ChunkProcessor.class);
        chunks.forEach( (cText,cAnnotation) -> {
            Mockito.when(processor.process(cText)).thenReturn(cAnnotation);
        });
    }

    public static ChunkProcessor getProcessor() {
        return processor;
    }
}
