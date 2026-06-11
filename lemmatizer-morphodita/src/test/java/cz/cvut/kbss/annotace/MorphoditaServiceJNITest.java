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
package cz.cvut.kbss.annotace;

import cz.cuni.mff.ufal.morphodita.morphodita_javaJNI;
import cz.cvut.kbss.annotace.configuration.MorphoditaConf;
import cz.cvut.kbss.annotace.lemmatizer.MorphoDitaServiceJNI;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

// To enable, set java.library.path environment variable to a directory containing libmorphodita_java.so (in build.gradle)
// and set absolute paths to taggers in src/test/resources/application.properties
@Disabled
public class MorphoditaServiceJNITest {

    private static final MorphoditaConf CONF = new MorphoditaConf() {
        @Override
        public Map<String, String> taggers() {
            return Map.of("cs", "czech-morfflex-pdt-161115.tagger");
        }

        @Override
        public Optional<String> service() {
            return Optional.empty();
        }
    };

    private static MorphoDitaServiceJNI sut;

    @BeforeAll
    static void init() {
        sut = new MorphoDitaServiceJNI(CONF);
    }

    @Test
    void testIfNativeMorphoditaAvailable() {
        morphodita_javaJNI.Tagger_load("");
    }

    @Test
    void testJNIMorphodita() {
        sut.process("ahoj  svete, jak se mas? Ja se mam dobre. A ty ?", "cs");
    }
}
