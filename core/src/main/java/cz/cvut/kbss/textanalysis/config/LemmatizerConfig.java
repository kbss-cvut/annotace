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
package cz.cvut.kbss.textanalysis.config;

import cz.cvut.kbss.annotace.configuration.MorphoditaConf;
import cz.cvut.kbss.annotace.configuration.SparkConf;
import cz.cvut.kbss.annotace.lemmatizer.MorphoDitaServiceJNI;
import cz.cvut.kbss.annotace.lemmatizer.MorphoDitaServiceOnline;
import cz.cvut.kbss.annotace.lemmatizer.SparkLemmatizer;
import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Slf4j
public class LemmatizerConfig {

    @ConfigProperty(name = "annotace.lemmatizer", defaultValue = "spark")
    String lemmatizer;

    @Inject
    MorphoditaConf morphoditaConf;

    @Inject
    SparkConf sparkConf;

    @Produces
    @ApplicationScoped
    public LemmatizerApi lemmatizer() {
        return switch (lemmatizer) {
            case "morphodita-jni" -> {
                log.info("Instantiating MorphoDiTa JNI lemmatizer.");
                yield new MorphoDitaServiceJNI(morphoditaConf);
            }
            case "morphodita-online" -> {
                log.info("Instantiating MorhoDiTa online lemmatizer.");
                yield new MorphoDitaServiceOnline(morphoditaConf);
            }
            default -> {
                log.info("Instantiating Apache Spark lemmatizer.");
                yield new SparkLemmatizer(sparkConf);
            }
        };
    }
}
