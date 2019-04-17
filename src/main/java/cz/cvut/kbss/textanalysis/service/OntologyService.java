package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.model.MorphoDitaResultJson;
import cz.cvut.kbss.textanalysis.model.QueryResult;
import cz.cvut.kbss.textanalysis.service.morphodita.MorphoDitaServiceJNI;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service public class OntologyService {

    @Autowired private MorphoDitaServiceJNI morphoDitaService;

    private static final Logger LOG = LoggerFactory.getLogger(OntologyService.class);

    public OntologyService() {
    }

    public Model readOntologyFromFile(String filename) throws Exception {
        Model model = ModelFactory.createDefaultModel();
        File file = new File(filename);
        FileReader reader = new FileReader(file);
        model.read(reader, null, FileUtils.langTurtle);

        return model;
    }

    public Model readOntology(URI uri) {
        Model model = ModelFactory.createDefaultModel();
        model.read(uri.toString(), "text/turtle");
        //model.write(System.out, "RDF/JSON");
        return model;
    }

    public List<QueryResult> analyzeModel(Set<URI> uriSet) {
        List<QueryResult> allGraphs = new ArrayList<>();
        List<QueryResult> singleGraph;

        for(URI uri : uriSet) {
            singleGraph = analyzeModel(readOntology(uri));
            allGraphs.addAll(singleGraph);
        }
        return allGraphs;
    }

    public List<QueryResult> analyzeModel(Model model) {
        List<QueryResult> queryResultList = new ArrayList<>();
        LOG.debug("Analyzing ontology model to get all labels");
        RDFNode s;
        RDFNode o;
        ResultSet resultSet;
        String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                        + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                        + "SELECT ?s ?o WHERE {"
                        + "?s rdfs:label ?o ."
                        + "?s a <http://onto.fel.cvut.cz/ontologies/slovnik/agendovy/popis-dat/pojem/term> . "
                        + "}";

        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        resultSet = queryExecution.execSelect();

        for (; resultSet.hasNext(); ) {
            QuerySolution querySolution = resultSet.nextSolution();
            s = querySolution.get("s");
            o = querySolution.get("o");
            if (!o.asLiteral().getString().equals("")) {
                QueryResult queryResultobject =
                        new QueryResult(s.asNode().toString(), o.asLiteral().getString());
                queryResultList.add(queryResultobject);
            } else {
                QueryResult queryResultobject =
                        new QueryResult(s.asNode().toString(), "null");
                queryResultList.add(queryResultobject);
            }
        }
        //printList(queryResultList);
        LOG.debug("number of retrieved lables is: " + queryResultList.size());
        //Store all lables in one string and call morphoDita only once then map the queryResult
        // objects to corresponding sub-array
        String ontologieLabels = "";
        for (int i = 0; i < queryResultList.size(); i++) {
            ontologieLabels = ontologieLabels + queryResultList.get(i).getLabel().trim() + "\n" + "\n";
        }

        LOG.debug("Morphological anlysis for ontology labels has started:");
        List<List<MorphoDitaResultJson>> morphoDitaResultList =
            morphoDitaService.getMorphoDiteResultProcessed(ontologieLabels);

        int i = 0;
        for (QueryResult queryResult : queryResultList) {
            queryResult.setMorphoDitaResultList(morphoDitaResultList.get(i));
            i++;
        }

        LOG.debug("Morphological analysis for ontology labels has finished");
        return queryResultList;

    }

    public void printList(List<QueryResult> queryResultList) {
        queryResultList.forEach(qr -> LOG.debug(
            "type from the arraylist is: " + qr.getType() + "   and label is: " + qr.getLabel()));
    }
}
