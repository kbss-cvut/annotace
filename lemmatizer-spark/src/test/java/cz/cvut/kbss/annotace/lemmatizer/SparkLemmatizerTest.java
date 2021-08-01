package cz.cvut.kbss.annotace.lemmatizer;

import cz.cvut.kbss.annotace.configuration.SparkConf;
import cz.cvut.kbss.textanalysis.lemmatizer.model.LemmatizerResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class,
    classes = {SparkConf.class, SparkLemmatizer.class})
public class SparkLemmatizerTest {

    @Autowired
    private SparkLemmatizer sut;

    @Test
    void check() {
//        l.lemmatize("Kromě toho, že je králem severu, je John Snow anglickým lékařem a lídrem ve vývoji anestezie a lékařské hygieny.");
        final LemmatizerResult result = sut.process("Budovou se rozumí nadzemní stavba. Bez střechy se nejedná o budovu.", "cs");
        System.out.println(result);

//        l.lemmatize("Květinou ženu neuhodíš.");
    }
}
