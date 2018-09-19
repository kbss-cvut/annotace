package cz.cvut.kbss.textanalysis.service.html2rdfa;

import cz.cuni.mff.ufal.morphodita.morphodita_javaJNI;
import cz.cvut.kbss.textanalysis.service.MorphoDitaServiceJNI;
import org.junit.jupiter.api.Test;

public class MorphoditaTest {

    @Test void testIfNativeMorphoditaAvailable() {
        morphodita_javaJNI.Tagger_load("");
    }

    @Test void testJNIMorphodita() {
        final MorphoDitaServiceJNI s = new MorphoDitaServiceJNI();
        s.getMorphoDiteResultProcessed("ahoj  svete, jak se mas? Ja se mam dobre. A ty ?");
    }
}
