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

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.annotace.configuration.MorphoditaConf;
import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import cz.cvut.kbss.textanalysis.lemmatizer.model.LemmatizerResult;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MorphoDitaServiceOnline implements LemmatizerApi {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MorphoditaConf conf;

    public MorphoDitaServiceOnline(MorphoditaConf conf) {
        this.conf = conf;
    }

    public LemmatizerResult process(String s, String lang) {
        final String service = conf.service()
                                   .orElseThrow(() -> new IllegalStateException(
                                           "annotace.morphodita.service is not configured"));
        final String url = service + "/tag?data=" + URLEncoder.encode(s, StandardCharsets.UTF_8)
                + "&output=json";
        try {
            final HttpResponse<String> response = httpClient.send(
                    HttpRequest.newBuilder(URI.create(url)).GET().build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            final LemmatizerResult morphoDitaResult =
                    objectMapper.readValue(response.body(), LemmatizerResult.class);
            morphoDitaResult.setLemmatizer(this.getClass().getName());
            return morphoDitaResult;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("MorphoDiTa online tagging failed", e);
        }
    }

    @Override
    public List<String> getSupportedLanguages() {
        return List.of();
    }
}
