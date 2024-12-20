/*
 * Annotace
 * Copyright (C) 2024 Czech Technical University in Prague
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package cz.cvut.kbss.annotace.lemmatizer;

import cz.cvut.kbss.annotace.configuration.MorphoditaConf;
import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import cz.cvut.kbss.textanalysis.lemmatizer.model.LemmatizerResult;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.List;

public class MorphoDitaServiceOnline implements LemmatizerApi {

    private final RestTemplateBuilder restTemplateBuilder;

    private final MorphoditaConf conf;

    public MorphoDitaServiceOnline(RestTemplateBuilder restTemplateBuilder, MorphoditaConf conf) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.conf = conf;
    }

    public LemmatizerResult process(String s, String lang) {
        final LemmatizerResult morphoDitaResult = restTemplateBuilder.build().getForObject(
            conf.getService() +
                "/tag?data=" + s + "&output=json",
            LemmatizerResult.class);

        morphoDitaResult.setLemmatizer(this.getClass().getName());
        return morphoDitaResult;
    }

    @Override
    public List<String> getSupportedLanguages() {
        return List.of();
    }
}
