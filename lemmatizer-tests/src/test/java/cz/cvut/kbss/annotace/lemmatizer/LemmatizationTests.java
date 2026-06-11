/*
 * Annotace
 * Copyright (C) 2026 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 *along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package cz.cvut.kbss.annotace.lemmatizer;

import cz.cvut.kbss.annotace.configuration.MorphoditaConf;
import cz.cvut.kbss.annotace.configuration.SparkConf;
import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import cz.cvut.kbss.textanalysis.lemmatizer.model.LemmatizerResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// To enable, specify java.library.path containing libmorphodita_java.so (in build.gradle) and set
// absolute paths to taggers in src/test/resources/application.properties
@Disabled
public class LemmatizationTests {

    private static final SparkConf SPARK_CONF = () -> Map.of(
        "cs", "model:lemma",
        "en", "pipeline:explain_document_ml"
    );

    private static final MorphoditaConf MORPHODITA_CONF = new MorphoditaConf() {
        @Override
        public Map<String, String> taggers() {
            return Map.of(
                "cs", "czech-morfflex-pdt-161115.tagger",
                "en", "english-morphium-wsj-140407.tagger"
            );
        }

        @Override
        public Optional<String> service() {
            return Optional.empty();
        }
    };

    private SparkLemmatizer sut1;

    private MorphoDitaServiceJNI sut2;

    private LemmatizerApi[] lemmatizers;

    @BeforeEach
    public void init() {
        this.sut1 = new SparkLemmatizer(SPARK_CONF);
        this.sut2 = new MorphoDitaServiceJNI(MORPHODITA_CONF);
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
        Arrays.stream(lemmatizers).forEach(l -> results.add(l.process(val, "cs")));
        results.forEach(r -> {
            System.out.println(r.getLemmatizer());
            r.getResult().forEach(l -> {
                l.forEach(x -> System.out.printf("%1$" + 30 + "s", x.getLemma()));
                System.out.println();
            });
        });
    }
}