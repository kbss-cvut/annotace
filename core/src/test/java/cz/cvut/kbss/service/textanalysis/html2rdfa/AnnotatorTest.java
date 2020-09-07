package cz.cvut.kbss.service.textanalysis.html2rdfa;

import cz.cvut.kbss.textanalysis.model.Word;
import cz.cvut.kbss.textanalysis.service.html2rdfa.Annotator;
import cz.cvut.kbss.service.textanalysis.TestChunkFactory;
import cz.cvut.kbss.model.Phrase;
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
        a = new Annotator("cs");
    }

    private Word[] getChunk(int i) {
        return TestChunkFactory.createTestChunks().values().toArray(new Word[][]{})[i];
    }

    @Test
    public void testSingleTermOccurrence() {
        final Word[] words = getChunk(0);
        final Stream<Node> nodes = a.annotate(words);
        Object[] aNodes= nodes.toArray();
        Assertions.assertEquals(1, aNodes.length);

        final Element element = (Element) aNodes[0];
        Assertions.assertNotNull(element.attr("about"));
        Assertions.assertEquals("ddo:je-výskytem-termu",element.attr("property"));
        Assertions.assertEquals("http://test.org/pojem/Test",element.attr("resource"));
        Assertions.assertEquals("ddo:výskyt-termu",element.attr("typeof"));
    }

    @Test
    public void testTwoTermOccurrenceCount() {
        final Stream<Node> nodes = a.annotate(getChunk(1));
        Assertions.assertEquals(2,
            nodes.filter(n -> n instanceof Element).filter(n -> ((Element) n).tagName().equals("span")).count());
    }

    @Test
    void testChoosePhraseReturnsPrefLabel() {
        Phrase[] phraseList = new Phrase[] {
                new Phrase("http://onto.fel.cvut.cz/ontologies/slovnik/ls-test/pojem/wing",false, false, "wing", "http://www.w3.org/2004/02/skos/core#prefLabel"),
                new Phrase("http://onto.fel.cvut.cz/ontologies/slovnik/ls-test/pojem/wing2", false,false, "wing", "http://www.w3.org/2004/02/skos/core#altLabel"),
                };
        phraseList = a.sortArrayOfPhrasesLabelLength(phraseList);
        Assertions.assertEquals(a.choosePhrase(phraseList).getTermIri(), "http://onto.fel.cvut.cz/ontologies/slovnik/ls-test/pojem/wing");
    }

    @Test
    void testChoosePhraseReturnsMatchWithAltLabelWhenPartialPrefLabelFound() {
        Phrase[] phraseList = new Phrase[] {
                new Phrase("http://onto.fel.cvut.cz/ontologies/slovnik/ls-test/pojem/struktura",false, false, "struktura", "http://www.w3.org/2004/02/skos/core#prefLabel"),
                new Phrase("http://onto.fel.cvut.cz/ontologies/slovnik/ls-test/pojem/typ-struktury", false,false, "struktura", "http://www.w3.org/2004/02/skos/core#altLabel"),
                new Phrase("http://onto.fel.cvut.cz/ontologies/slovnik/ls-test/pojem/typ-struktury", false,false, "typ struktury", "http://www.w3.org/2004/02/skos/core#prefLabel"),
        };
        phraseList = a.sortArrayOfPhrasesLabelLength(phraseList);
        Assertions.assertEquals(a.choosePhrase(phraseList).getTermIri(), "http://onto.fel.cvut.cz/ontologies/slovnik/ls-test/pojem/struktura");
    }

    @Test
    void testChoosePhraseReturnsHigherScoreWhenPartialMatch() {
        Phrase[] phraseList = new Phrase[] {
                new Phrase("http://onto.fel.cvut.cz/ontologies/slovnik/ls-test/pojem/wing-connector",false, false, "wing connector", "http://www.w3.org/2004/02/skos/core#prefLabel"),
                new Phrase("http://onto.fel.cvut.cz/ontologies/slovnik/ls-test/pojem/wing2", false,false, "wing that will not match", "http://www.w3.org/2004/02/skos/core#prefLabel"),
                new Phrase("http://onto.fel.cvut.cz/ontologies/slovnik/ls-test/pojem/right-wing",false, false, "Right wing", "http://www.w3.org/2004/02/skos/core#prefLabel"),
                new Phrase("http://onto.fel.cvut.cz/ontologies/slovnik/ls-test/pojem/wing2", false, false, "right hand wing and flap", "http://www.w3.org/2004/02/skos/core#altLabel"),
                new Phrase("http://onto.fel.cvut.cz/ontologies/slovnik/ls-test/pojem/right-wing", false,false, "rh wing", "http://www.w3.org/2004/02/skos/core#altLabel")
        };
        phraseList = a.sortArrayOfPhrasesLabelLength(phraseList);
        Assertions.assertEquals(a.choosePhrase(phraseList).getTermIri(), "http://onto.fel.cvut.cz/ontologies/slovnik/ls-test/pojem/right-wing");
    }
}
