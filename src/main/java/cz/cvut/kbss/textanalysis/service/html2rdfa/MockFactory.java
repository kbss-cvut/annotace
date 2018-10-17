package cz.cvut.kbss.textanalysis.service.html2rdfa;

import cz.cvut.kbss.model.Word;
import cz.cvut.kbss.model.Phrase;
import cz.cvut.kbss.textanalysis.service.ChunkAnnotationService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.mockito.Mock;
import org.mockito.Mockito;

public class MockFactory {

    @Mock
    private static ChunkAnnotationService processor;

    public static final Map<String,Word[]> chunks = new LinkedHashMap<>();

    static {
        // DUMMY CHUNK 0
        final Phrase pT = new Phrase("http://test.org/pojem/Test", false, true, "test");
        final Word[] chunk0Annotations = new Word[] {new Word("Test","Test", "", pT)};
        chunks.put("Test", chunk0Annotations);

        // DUMMY CHUNK 1
        final Phrase pMP = new Phrase("http://test.org/pojem/metropolitni-plan", false, true, "metropolitní plán");
        final Phrase pD = new Phrase(null, true, true, null);
        final Word[] chunk1Annotations = new Word[] {new Word("Metropolitní","Metropolitni", " ",pMP),
                                        new Word("Plán","plan", "  ", pMP),
                                        new Word("být","je", " "),
                                        new Word( "důležitý","dulezitym", " "),
                                        new Word( "dokument","dokumentem", " ",pD)};
        chunks.put("Metropolitni plan je\tdulezitym\ndokumentem.", chunk1Annotations);

        // DUMMY CHUNK 2
        final Phrase pZU = new Phrase("http://test.org/pojem/zastavene-uzemi", true, true, "zastavěné území");
        final Word[] chunk2Annotations = new Word[] {new Word( "Zastavěné","Zastavene", " ", pZU),
                                   new Word("území","uzemi", " ", pZU)};
        chunks.put("Zastavene uzemi", chunk2Annotations);

        // MPP CHUNK
        final String chunk = "\n"
                             + "     (2)\tMetropolitní plán je pořízen pro stanovení koncepce "
                             + "celého správního území Prahy, a to v měřítku 1:10.000 "
                             + "odpovídajícím rozsahu řešeného území.\n"
                             + "    ";
        final Phrase pMPX = new Phrase("http://test.org/pojem/metropolitni-plan", true, true, "metropolitní plán");
        final Phrase pU = new Phrase("http://test.org/pojem/uzemi", true, true, "uzemi");
        final Phrase pSUP = new Phrase("http://test.org/pojem/spravni-uzemi-prahy", true, true, "správní území Prahy");
        final Phrase pM = new Phrase(null, true, true, null);
        final Word[] chunk3Annotations = new Word[] {
            new Word( "(2)","(2)", " "),
            new Word( "Metropolitní","Metropolitní", " ", pMPX),
            new Word( "plán","plán", " ", pMPX),
            new Word("je","je", " "),
            new Word( "pořízen","pořízen", " "),
            new Word( "pro","pro", " "),
            new Word( "stanovení","stanovení", " "),
            new Word( "koncepce","koncepce", " "),
            new Word( "celé","celého", " "),
            new Word( "správní","správního", " ", pSUP),
            new Word( "území","území", " ", pU, pSUP),
            new Word( "Praha","Prahy", ". ", pSUP),
            new Word("a","a", " "),
            new Word( "to","to", " "),
            new Word( "v","v", " "),
            new Word( "měřítko","měřítku", " ", pM),
            new Word( "1:10.000","1:10.000", " "),
            new Word( "odpovídající", "odpovídajícím", " "),
            new Word( "rozsah", "rozsahu", " "),
            new Word( "řešené", "řešeného", " "),
            new Word( "území","území", " ", pU),
            };
        chunks.put(chunk, chunk3Annotations);

        processor = Mockito.mock(ChunkAnnotationService.class);
        chunks.forEach( (cText,cAnnotation) -> {
            Mockito.when(processor.process(cText)).thenReturn(cAnnotation);
        });
    }

    public static ChunkAnnotationService getProcessor() {
        return processor;
    }
}
