/*
 * Annotace
 * Copyright (C) 2021 Czech Technical University in Prague
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

import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class RemoteOntologyService extends AbstractOntologyService {

    private static final String REPO_AUTH = "Basic dGVybWl0OnZsOGRjZXRlcm1pdGkzdDI=";

    @Autowired
    public RemoteOntologyService(final LemmatizerApi morphoDitaService) {
        super(morphoDitaService);
    }

    public Model readOntology(URI uri) {
        HttpClient client = HttpClients.custom().build();
        HttpUriRequest request = RequestBuilder.get()
            .setUri(uri)
            .setHeader("Authorization", REPO_AUTH)
            .build();
        HttpEntity entity = null;
        try {
            entity = client.execute(request).getEntity();
        } catch (IOException e) {
            log.error("Error executing Get request to the repository");
        }

        final File file;
        Model model = ModelFactory.createDefaultModel();
        model.read(uri.toString(), "text/turtle");
        try {
            file = File.createTempFile("model", "");
            if (entity != null) {
                Files.write(Paths.get(file.toURI()),
                    Collections.singleton(EntityUtils.toString(entity)));
                model.read(file.getAbsolutePath(), FileUtils.langTurtle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return model;
    }
}