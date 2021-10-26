package cz.cvut.kbss.service.textanalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;


import cz.cvut.kbss.textanalysis.dto.TextAnalysisInput;
import cz.cvut.kbss.textanalysis.service.HtmlAnnotationException;
import cz.cvut.kbss.textanalysis.service.HtmlAnnotationService;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

    @ParameterizedTest
    @MethodSource("getIdempotentTexts")
    public void testAnnotateIsIdempotent(final String file)
        throws IOException, HtmlAnnotationException {

        final String content = Jsoup
            .parse(new File(HtmlAnnotationService.class.getResource("/" + file).getFile()),"utf-8").toString();

        final TextAnalysisInput input = new TextAnalysisInput()
            .setVocabularyRepository(URI.create("https://slovník.gov.cz/generický/testovaci-slovnik"))
            .setLanguage("cs")
            .setContent(content);

        final String annotatedContent1 = sut.annotate(true, input);

        input.setContent(annotatedContent1);
        final String annotatedContent2 = sut.annotate(true, input);

        // hard to compare directly due to blank nodes
        assertEquals(annotatedContent1.length(), annotatedContent2.length());
    }

    private static Stream<Arguments> getIdempotentTexts() {
        return Stream.of(
            Arguments.of("test-simple.html"),
            Arguments.of("test-simple-2.html")
        );
    }
}
