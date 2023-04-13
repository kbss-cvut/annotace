package cz.cvut.kbss.annotace;

import cz.cuni.mff.ufal.morphodita.morphodita_javaJNI;
import cz.cvut.kbss.annotace.configuration.MorphoditaConf;
import cz.cvut.kbss.annotace.lemmatizer.MorphoDitaServiceJNI;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// To enable, specify java.library.path containing libmorphodita_java.so (in build.gradle) and set
// absolute paths to taggers in src/test/resources/application.yml
@Disabled
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class,
    classes = { MorphoDitaServiceJNI.class})
@EnableSpringConfigured
@EnableConfigurationProperties({MorphoditaConf.class})
@ActiveProfiles("test")
public class MorphoditaServiceJNITest {

    @Autowired
    private MorphoDitaServiceJNI sut;

    @Test void testIfNativeMorphoditaAvailable() {
        morphodita_javaJNI.Tagger_load("");
    }

    @Test void testJNIMorphodita() {
        sut.process("ahoj  svete, jak se mas? Ja se mam dobre. A ty ?", "cs");
    }
}
