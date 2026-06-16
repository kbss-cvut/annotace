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
package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Service
@Slf4j
public class RemoteOntologyService extends AbstractOntologyService {

    @Autowired
    public RemoteOntologyService(final LemmatizerApi lemmatizer) {
        super(lemmatizer);
    }

    public Model readOntology(final URI uri, final String userName, final String password) {
        final HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
        if (userName != null) {
            httpClientBuilder.authenticator(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userName, password.toCharArray());
                }
            });
        }
        final Model model = ModelFactory.createDefaultModel();
        try (final HttpClient client = httpClientBuilder.build()) {
            final HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            model.read(new StringReader(response.body()), null, FileUtils.langTurtle);
        } catch (IOException e) {
            log.error("Error getting the ontology: ", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error getting the ontology: ", e);
        }
        return model;
    }
}