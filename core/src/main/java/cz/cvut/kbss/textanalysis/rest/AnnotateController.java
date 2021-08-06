/**
 * Annotace
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
package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.textanalysis.Constants;
import cz.cvut.kbss.textanalysis.dto.TextAnalysisInput;
import cz.cvut.kbss.textanalysis.service.HtmlAnnotationService;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.vocabulary.SKOS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnnotateController {

    private final HtmlAnnotationService service;

    @Autowired
    public AnnotateController(HtmlAnnotationService service) {
        this.service = service;
    }

    @RequestMapping(value = "/annotate", method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    public String annotate(@RequestParam(value = "enableKeywordExtraction", defaultValue = "false") Boolean enableKeywordExtraction, @RequestBody TextAnalysisInput input)
        throws Exception {
        Set<URI> uriSet = new HashSet<>();
        String uri;
        String iTerm = SKOS.Concept.toString();
        if (input.getVocabularyContexts() != null) {
            Set<URI> allGraphs = input.getVocabularyContexts();

            for(URI graphUri : allGraphs) {
                 uri = input.getVocabularyRepository() + "?query=" + encode(
                        "CONSTRUCT {?s ?p ?o} WHERE { GRAPH <" + graphUri + "> {?s a <" +iTerm+ "> .?s ?p ?o}}");
                uriSet.add(URI.create(uri));
            }

        } else {
            uri = input.getVocabularyRepository() + "?query=" + encode(
                    "CONSTRUCT {?s ?p ?o} WHERE {?s a <"+iTerm+"> . ?s ?p ?o}");
            uriSet.add(URI.create(uri));
        }


        final String htmlDocument = input.getContent();
        return service.annotate(uriSet, htmlDocument, input.getLanguage(), enableKeywordExtraction);
    }

    private String encode(String s) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8.toString());
    }
}
