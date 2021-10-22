package cz.cvut.kbss.service.textanalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;


import cz.cvut.kbss.textanalysis.dto.TextAnalysisInput;
import cz.cvut.kbss.textanalysis.service.HtmlAnnotationException;
import cz.cvut.kbss.textanalysis.service.HtmlAnnotationService;
import java.io.File;
import java.io.IOException;
import java.net.URI;
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

        final TextAnalysisInput input = new TextAnalysisInput()
            .setVocabularyRepository(URI.create("https://slovník.gov.cz/generický/testovaci-slovnik"))
            .setLanguage("cs")
            .setContent(Jsoup
                .parse(new File(HtmlAnnotationService.class.getResource("/test-1574.html").getFile()),"utf-8").toString());

        final String s = sut.annotate(true, input);

        final int count = StringUtils.countMatches(s,
            "https://slovník.gov.cz/generický/testovaci-slovnik-0-0/pojem/parcela");

        assertEquals(4, count);
    }
}
