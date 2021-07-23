package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.configuration.AnnotaceConf;
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
    classes = {XMLAnnotationParser.class, XMLAnnotationService.class, MorphoDitaServiceJNI.class, AnnotaceConf.class})
public class XMLAnnotationParserTest {

    @Autowired
    private XMLAnnotationParser sut;

    //    @Test
    public void parser() throws TransformerException, ParserConfigurationException {
        sut.paresContent();
    }
}