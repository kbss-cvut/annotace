package cz.cvut.kbss.annotace.lemmatizer;

import cz.cvut.kbss.annotace.configuration.MorphoditaConf;
import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import cz.cvut.kbss.textanalysis.lemmatizer.model.LemmatizerResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class,
    classes = {MorphoditaConf.class, MorphoDitaServiceJNI.class, SparkLemmatizer.class})
public class LemmatizationTests {

    @Autowired
    private SparkLemmatizer sut1;

    @Autowired
    private MorphoDitaServiceJNI sut2;

    private LemmatizerApi[] lemmatizers;

    @BeforeEach
    public void init() {
        this.lemmatizers = new LemmatizerApi[] {sut1, sut2};
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Budovou se rozumí nadzemní stavba. Bez střechy se nejedná o budovu.",
        "Kromě toho, že je králem severu, je John Snow anglickým lékařem a lídrem ve vývoji "
            + "anestezie a lékařské hygieny.",
        "Květinou ženu neuhodíš."
    })
    void check(final String val) {
        final List<LemmatizerResult> results = new ArrayList<>();
        Arrays.stream(lemmatizers).forEach(l -> {
            results.add(l.process(val));
        });
        results.forEach(r -> {
            System.out.println(r.getLemmatizer());
            r.getResult().forEach(l -> {
                l.forEach(x -> {
                    System.out.print(String.format("%1$" + 30 + "s", x.getLemma()));
                });
                System.out.println();
            });
        });
    }
}