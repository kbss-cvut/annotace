package cz.cvut.kbss.html2rdfa;

import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HTMLParserTest {

    private HtmlAnnotationService service;

    @BeforeEach public void init() {
        service = new HtmlAnnotationService(MockFactory.getProcessor());
    }

    @Test public void testFindChunks() throws HtmlAnnotationException {
        final String[] chunks = MockFactory.chunks.keySet().toArray(new String[] {});
        final String html =
            "<html><body><p>" + chunks[1] + "</p><p>" + chunks[2] + "</p></body></html>";
        final Document doc = Jsoup.parse(html);

        final Document doc2 = service.annotate(doc);

        System.out.println(doc2.toString());

        Assertions.assertEquals(3, doc2.select("span[typeof='ddo:vyskyt-termu']").size());
    }

    @Test public void testParseInvalidHtmlSuccessfully() throws HtmlAnnotationException {
        final String html = "<p>Test chunk 1<div></p>Test chunk 2</div>";
        final Document doc = Jsoup.parse(html);
        final Document doc2 = service.annotate(doc);
        System.out.println(doc2.toString());
    }

    @Test public void testParseInvalidInputFail() {
        Assertions.assertThrows(HtmlAnnotationException.class, () -> {
            service.annotate(null);
        });
    }

    @Test public void testAnnotateMetropolitanPlanSuccessfully()
        throws HtmlAnnotationException, IOException {
        final Document doc2 = service.annotate(Jsoup
            .parse(new File(HtmlAnnotationService.class.getResource("/mpp.html").getFile()),
                "utf-8"));
        System.out.println(doc2.toString());
    }
}
