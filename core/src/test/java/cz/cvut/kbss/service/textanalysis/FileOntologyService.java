package cz.cvut.kbss.service.textanalysis;

import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import cz.cvut.kbss.textanalysis.service.AbstractOntologyService;
import java.io.InputStream;
import java.net.URI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Import(ServiceTestConfiguration.class)
@Primary
public class FileOntologyService extends AbstractOntologyService {

    @Autowired
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
