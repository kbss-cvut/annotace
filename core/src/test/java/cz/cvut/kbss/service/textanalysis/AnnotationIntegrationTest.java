package cz.cvut.kbss.service.textanalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;


import cz.cvut.kbss.textanalysis.service.HtmlAnnotationException;
import cz.cvut.kbss.textanalysis.service.HtmlAnnotationService;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = {HtmlAnnotationService.class, FileOntologyService.class})
@Import(ServiceTestConfiguration.class)
public class AnnotationIntegrationTest {

    @Autowired
    private HtmlAnnotationService sut;

    @Test
    public void testAnnotateQuotedStrings()
        throws IOException, HtmlAnnotationException {
        final Set<URI> vocabularies =
            Collections.singleton(URI.create("https://slovník.gov.cz/generický/testovaci-slovnik"));

        final String s = sut.annotate(vocabularies, Jsoup
            .parse(new File(HtmlAnnotationService.class.getResource("/test-1574.html").getFile()),
                "utf-8").toString(), "cs", true);

        final int count = StringUtils.countMatches(s,
            "https://slovník.gov.cz/generický/testovaci-slovnik-0-0/pojem/parcela");

        assertEquals(4, count);
    }
}
