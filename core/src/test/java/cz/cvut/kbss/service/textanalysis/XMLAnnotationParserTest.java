package cz.cvut.kbss.service.textanalysis;

import cz.cvut.kbss.textanalysis.service.XMLAnnotationParser;
import cz.cvut.kbss.textanalysis.service.XMLAnnotationService;
import cz.cvut.kbss.textanalysis.service.morphodita.MorphoDitaServiceJNI;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class,
    classes = {XMLAnnotationParser.class, XMLAnnotationService.class, MorphoDitaServiceJNI.class})
public class XMLAnnotationParserTest {

    @Autowired
    private XMLAnnotationParser sut;

    //    @Test
    public void parser() throws TransformerException, ParserConfigurationException {
        sut.paresContent();
    }
}