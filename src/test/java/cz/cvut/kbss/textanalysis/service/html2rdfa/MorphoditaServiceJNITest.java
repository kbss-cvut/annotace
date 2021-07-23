package cz.cvut.kbss.textanalysis.service.html2rdfa;

import cz.cuni.mff.ufal.morphodita.morphodita_javaJNI;
import cz.cvut.kbss.textanalysis.configuration.AnnotaceConf;
import cz.cvut.kbss.textanalysis.service.morphodita.MorphoDitaServiceJNI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class,
    classes = {AnnotaceConf.class, MorphoDitaServiceJNI.class})
public class MorphoditaServiceJNITest {

    @Autowired
    private MorphoDitaServiceJNI sut;

    @Test void testIfNativeMorphoditaAvailable() {
        morphodita_javaJNI.Tagger_load("");
    }

    @Test void testJNIMorphodita() {
        sut.getMorphoDiteResultProcessed("ahoj  svete, jak se mas? Ja se mam dobre. A ty ?");
    }
}
