/**
 * Annotac
 * Copyright (C) 2019 Czech Technical University in Prague
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
 * © 2019 GitHub, Inc.
 */
package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.model.KerResult;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;

@Service
public class KerService {

    private static final Logger LOG = LoggerFactory.getLogger(KerService.class);

    private final RestTemplate restTemplate;

    public KerService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public KerResult getKerResult(final String chunks) {

        final String kerUrl = "http://lindat.mff.cuni.cz/services/ker?language=cs&threshold=0.04&maximum-words=30";

        final File file;
        try {
            file = File.createTempFile("ker-input","");
            Files.write(Paths.get(file.toURI()), chunks.getBytes("utf-8"));

            Resource fileResource = new FileSystemResource(file);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            KerResult response = this.restTemplate.postForObject(kerUrl, requestEntity, KerResult.class);
            LOG.debug("Keywords: " + response.getKeywords());
            return response;
        } catch (Exception e) {
            LOG.error("Error computing key phrases.", e);
            return KerResult.createEmpty();
        }
    }
}
