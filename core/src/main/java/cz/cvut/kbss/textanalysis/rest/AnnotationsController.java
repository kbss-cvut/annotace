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
package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.textanalysis.model.KerResult;
import cz.cvut.kbss.textanalysis.model.QueryResult;
import cz.cvut.kbss.textanalysis.model.Word;
import cz.cvut.kbss.textanalysis.service.AnnotationService;

import java.net.URI;

import cz.cvut.kbss.textanalysis.service.OntologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class AnnotationsController {

    @Autowired
    private AnnotationService annotationService;

    @Autowired
    private OntologyService ontologyService;

    // TODO LS : "file:C:/Projects/OPPPR/services/textanalysis/src/main/resources/glosar.ttl"
    //output of Morphodita text analysis
    //to be re-moved
    //    String textChunk = Files.readAllLines(Paths.get("/home/kremep1/fel/projects/17opppr/czech-text-analysis/src/main/resources/test.txt")).stream().collect(
    //        Collectors.joining(""+Character.LINE_SEPARATOR));

    @RequestMapping("/annotate-raw")
    public List<Word> getAnnotatedTokenizedText(
            @RequestParam("ontologyUrl") Set<URI> ontologyUrl, @RequestParam("textChunk") String textChunk) throws Exception {
        List<QueryResult> queryResultList = ontologyService.analyzeModel(ontologyUrl);
        return annotationService.getAnnotations(textChunk, queryResultList, KerResult.createSomeList());
    }
}
