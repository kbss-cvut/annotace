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
package cz.cvut.kbss.annotace.keywordextractor;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.annotace.configuration.KerConf;
import cz.cvut.kbss.textanalysis.keywordextractor.KeywordExtractorAPI;
import cz.cvut.kbss.textanalysis.keywordextractor.model.KeywordExtractorResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class KerService implements KeywordExtractorAPI {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final KerConf config;

    @Inject
    public KerService(KerConf config) {
        this.config = config;
    }

    public KeywordExtractorResult process(final String chunks) {
        try {
            final String boundary = "----AnnotaceBoundary" + UUID.randomUUID();
            final byte[] body = buildMultipartBody(boundary, "file", "ker-input",
                    chunks.getBytes(StandardCharsets.UTF_8));

            final HttpRequest request = HttpRequest.newBuilder(URI.create(config.service()))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build();

            final HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            final KeywordExtractorResult result =
                    objectMapper.readValue(response.body(), KeywordExtractorResult.class);
            log.debug("Keywords: " + result.getKeywords());
            return result;
        } catch (Exception e) {
            log.error("Error computing key phrases.", e);
            return KeywordExtractorResult.createEmpty();
        }
    }

    private static byte[] buildMultipartBody(String boundary, String fieldName, String filename,
                                             byte[] content) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            final String header = "--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\""
                    + filename + "\"\r\n"
                    + "Content-Type: application/octet-stream\r\n\r\n";
            out.write(header.getBytes(StandardCharsets.UTF_8));
            out.write(content);
            out.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
            return out.toByteArray();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
}
