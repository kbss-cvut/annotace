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
package cz.cvut.kbss.service.textanalysis;

import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import cz.cvut.kbss.textanalysis.service.AbstractOntologyService;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import java.io.InputStream;
import java.net.URI;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;

@Alternative
@Priority(1)
@ApplicationScoped
public class FileOntologyService extends AbstractOntologyService {

    @Inject
    public FileOntologyService(
        LemmatizerApi lemmatizerApi) {
        super(lemmatizerApi);
    }

    private InputStream getModelStream(URI uri) {
        final String localName = uri.toString().substring(uri.toString().lastIndexOf("/") + 1,uri.toString().lastIndexOf("?") );
        return getClass().getResourceAsStream("/" + localName + ".ttl");
    }

    public Model readOntology(URI uri, final String userName, final String password) {
        final Model model = ModelFactory.createDefaultModel();
        model.read(getModelStream(uri), null, FileUtils.langTurtle);
        return model;
    }
}
