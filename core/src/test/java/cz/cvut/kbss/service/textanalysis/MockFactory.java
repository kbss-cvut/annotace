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
package cz.cvut.kbss.service.textanalysis;

import cz.cvut.kbss.textanalysis.model.Word;
import cz.cvut.kbss.textanalysis.model.Phrase;
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
