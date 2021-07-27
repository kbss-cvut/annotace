package cz.cvut.kbss.service.textanalysis.html2rdfa;

import cz.cvut.kbss.textanalysis.service.ChunkAnnotationService;
import cz.cvut.kbss.textanalysis.service.HtmlAnnotationService;
import cz.cvut.kbss.service.textanalysis.ServiceTestConfiguration;
import cz.cvut.kbss.service.textanalysis.MockFactory;
import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {ServiceTestConfiguration.class},
    initializers = {ConfigDataApplicationContextInitializer.class}
)
public class HTMLParserTest {

    private HtmlAnnotationService service;

    private ChunkAnnotationService chunkAnnotationService;

    @Autowired
    public HTMLParserTest(HtmlAnnotationService service) {
        this.service = service;
    }

    @BeforeEach
    public void init() {
        chunkAnnotationService = MockFactory.getProcessor();
    }

    @Test public void testFindChunks() {
        final String[] chunks = MockFactory.chunks.keySet().toArray(new String[] {});
        final String html =
            "<html><body><p>" + chunks[1] + "</p><p>" + chunks[2] + "</p></body></html>";
        final Document doc = Jsoup.parse(html);
        final Document doc2 = service.annotate(chunkAnnotationService,doc);
        Assertions.assertEquals(3, doc2.select("span[typeof='ddo:výskyt-termu']").size());
    }

    @Test public void testParseInvalidHtmlSuccessfully() {
        final String html = "<p>Test chunk 1<div></p>Test chunk 2</div>";
        final Document doc = Jsoup.parse(html);
        service.annotate(chunkAnnotationService,doc);
    }

    @Test public void testParseInvalidInputFail() {
        Assertions.assertThrows(Exception.class, () -> {
            service.annotate(chunkAnnotationService,null);
        });
    }

    @Test public void testAnnotateMetropolitanPlanSuccessfully()
        throws IOException {
        service.annotate(chunkAnnotationService, Jsoup
            .parse(new File(HtmlAnnotationService.class.getResource("/mpp.html").getFile()),
                "utf-8"));
    }
}
