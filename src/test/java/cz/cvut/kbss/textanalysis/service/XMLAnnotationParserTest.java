package cz.cvut.kbss.textanalysis.service;

import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import static org.junit.Assert.*;

public class XMLAnnotationParserTest {
    XMLAnnotationParser xmlAnnotationParser = new XMLAnnotationParser();

    @Test
    public void parser() throws TransformerException, ParserConfigurationException {
        xmlAnnotationParser.paresContent();
    }

}