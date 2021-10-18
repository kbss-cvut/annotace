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
import cz.cvut.kbss.textanalysis.lemmatizer.model.LemmatizerResult;
import cz.cvut.kbss.textanalysis.model.QueryResult;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.SKOS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public abstract class AbstractOntologyService implements OntologyService {

    private LemmatizerApi lemmatizerServiceApi;

    @Autowired
    public AbstractOntologyService(final LemmatizerApi morphoDitaService) {
        this.lemmatizerServiceApi = morphoDitaService;
    }

    public abstract Model readOntology(URI uri);

    public List<QueryResult> analyzeModel(Set<URI> uriSet, String lang) {
        List<QueryResult> allGraphs = new ArrayList<>();
        List<QueryResult> singleGraph;

        for(URI uri : uriSet) {
            singleGraph = analyzeModel(readOntology(uri), lang);
            allGraphs.addAll(singleGraph);
        }
        return allGraphs;
    }

    public List<QueryResult> analyzeModel(Model model, String lang) {
        List<QueryResult> queryResultList = new ArrayList<>();
        log.debug("Analyzing ontology model to get all labels");
        RDFNode s;
        RDFNode p;
        RDFNode o;
        ResultSet resultSet;
        String query =
            "SELECT ?s ?p ?o WHERE {"
                + "?s ?p ?o ."
                + "?s a <" + SKOS.Concept.toString() + "> . "
                + "FILTER (lang(?o) = '" + lang + "') ."
                + "FILTER (?p IN (<" + SKOS.prefLabel.toString()+ ">, <" + SKOS.hiddenLabel.toString() + ">, <" + SKOS.altLabel + ">))"
                + "}";

        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        resultSet = queryExecution.execSelect();

        for (; resultSet.hasNext(); ) {
            QuerySolution querySolution = resultSet.nextSolution();
            s = querySolution.get("s");
            p = querySolution.get("p");
            o = querySolution.get("o");
            if (!o.asLiteral().getString().isEmpty()) {
                QueryResult queryResultobject =
                    new QueryResult(s.asNode().toString(), o.asLiteral().getString(), p.asNode().toString());
                queryResultList.add(queryResultobject);
            } else {
                QueryResult queryResultobject =
                    new QueryResult(s.asNode().toString(), "null", "null");
                queryResultList.add(queryResultobject);
            }
        }
        //printList(queryResultList);
        log.debug("number of retrieved labels is: " + queryResultList.size());
        //Store all labels in one string and call lemmatizer only once then map the queryResult
        // objects to corresponding sub-array
        final StringBuilder sb = new StringBuilder();
        for (QueryResult qr : queryResultList) {
            sb.append(qr.getLabel().trim()).append("\n\n\n\n");
        }
        final String ontologieLabels = sb.toString();

        log.debug("Morphological analysis for ontology labels has started:");
        LemmatizerResult lemmatizerResult =
            lemmatizerServiceApi.process(ontologieLabels, lang);

        int i = 0;
        for (QueryResult queryResult : queryResultList) {
            queryResult.setSingleLemmaResults(lemmatizerResult.getResult().get(i));
            i++;
        }

        log.debug("Morphological analysis for ontology labels has finished");
        return queryResultList;

    }
}
