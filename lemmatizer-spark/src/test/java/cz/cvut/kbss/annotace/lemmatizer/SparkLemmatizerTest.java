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

import cz.cvut.kbss.annotace.configuration.SparkConf;
import cz.cvut.kbss.textanalysis.lemmatizer.model.LemmatizerResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class SparkLemmatizerTest {

    private static final SparkConf SPARK_CONF = () -> Map.of(
        "cs", "model:lemma",
        "en", "pipeline:explain_document_ml"
    );

    private static SparkLemmatizer sut;

    @BeforeAll
    static void init() {
        sut = new SparkLemmatizer(SPARK_CONF);
    }

    @Test
    void checkCzech() {
        final LemmatizerResult result =
            sut.process("Starý člověk chodí do kostela častěji než mladší lidé.",
                "cs");
        final Iterator<String> lemmas = Arrays.asList(
            new String[] {"Starý", "člověk", "chodit", "do", "kostel", "často", "než", "mladý",
                "člověk", "."}).iterator();
        test(lemmas, result);
    }

    @Test
    void checkEnglish() {
        final LemmatizerResult result = sut.process("UK is going down.", "en");
        final Iterator<String> lemmas =
            Arrays.asList(new String[] {"UK", "be", "go", "down", "."}).iterator();
        test(lemmas, result);
    }

    private void test(final Iterator<String> correctLemmas, final LemmatizerResult result) {
        Assertions.assertAll(
            result.getResult().getFirst().stream().map(r -> () ->
                Assertions.assertEquals(correctLemmas.next(), r.getLemma()))
        );
    }
}
