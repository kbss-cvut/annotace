package cz.cvut.kbss.textanalysis.service.html2rdfa;

import cz.cvut.kbss.model.Word;
import java.util.stream.Stream;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AnnotatorTest {

    private Annotator a;

    @BeforeEach
    public void init() {
        a = new Annotator();
    }

    private Word[] getChunk(int i) {
        return MockFactory.chunks.values().toArray(new Word[][]{})[i];
    }

    @Test
    public void testSingleTermOccurrence() {
        final Word[] words = getChunk(0);
        final Stream<Node> nodes = a.annotate(words);
        Object[] aNodes= nodes.toArray();
        Assertions.assertEquals(1, aNodes.length);

        final Element element = (Element) aNodes[0];
        Assertions.assertNotNull(element.attr("about"));
        Assertions.assertEquals("ddo:je-vyskytem-termu",element.attr("property"));
        Assertions.assertEquals("http://test.org/pojem/Test",element.attr("resource"));
        Assertions.assertEquals("ddo:vyskyt-termu",element.attr("typeof"));
    }

    @Test
    public void testTwoTermOccurrenceCount() {
        final Stream<Node> nodes = a.annotate(getChunk(1));
        Assertions.assertEquals(2,
            nodes.filter(n -> n instanceof Element).filter(n -> ((Element) n).tagName().equals("span")).count());
    }
}
