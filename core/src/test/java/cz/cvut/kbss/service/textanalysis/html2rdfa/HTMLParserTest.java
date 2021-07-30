package cz.cvut.kbss.service.textanalysis.html2rdfa;

import static org.mockito.Mockito.lenient;


import cz.cvut.kbss.textanalysis.keywordextractor.KeywordExtractorAPI;
import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import cz.cvut.kbss.textanalysis.service.ChunkAnnotationService;
import cz.cvut.kbss.textanalysis.service.HtmlAnnotationService;
import cz.cvut.kbss.service.textanalysis.TestChunkFactory;
import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HTMLParserTest {

    @InjectMocks
    private HtmlAnnotationService sut;

    @Mock
    private LemmatizerApi api;

    @Mock
    private KeywordExtractorAPI ker;

    @Mock
    private ChunkAnnotationService chunkAnnotationService;

    @BeforeEach
    public void init() {
        TestChunkFactory.createTestChunks().forEach(
            (cText,cAnnotation) -> {
                lenient().when(chunkAnnotationService.process(cText)).thenReturn(cAnnotation);
            }
        );
    }

    @Test public void testFindChunks() {
        final String[] chunks = TestChunkFactory.createTestChunks().keySet().toArray(new String[] {});
        final String html =
            "<html><body><p>" + chunks[1] + "</p><p>" + chunks[2] + "</p></body></html>";
        final Document doc = Jsoup.parse(html);
        final Document doc2 = sut.annotate(chunkAnnotationService,doc);
        Assertions.assertEquals(3, doc2.select("span[typeof='ddo:výskyt-termu']").size());
    }

    @Test public void testParseInvalidHtmlSuccessfully() {
        final String html = "<p>Test chunk 1<div></p>Test chunk 2</div>";
        final Document doc = Jsoup.parse(html);
        sut.annotate(chunkAnnotationService,doc);
    }

    @Test public void testParseInvalidInputFail() {
        Assertions.assertThrows(Exception.class, () -> {
            sut.annotate(chunkAnnotationService,null);
        });
    }

    @Test public void testAnnotateMetropolitanPlanSuccessfully()
        throws IOException {
        sut.annotate(chunkAnnotationService, Jsoup
            .parse(new File(HtmlAnnotationService.class.getResource("/mpp.html").getFile()),
                "utf-8"));
    }
}
