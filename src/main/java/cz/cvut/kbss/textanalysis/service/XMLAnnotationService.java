package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.model.MorphoDitaResultJson;
import cz.cvut.kbss.textanalysis.service.morphodita.MorphoDitaServiceAPI;
import cz.cvut.kbss.textanalysis.service.morphodita.MorphoDitaServiceJNI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XMLAnnotationService {

    @Autowired
    private MorphoDitaServiceAPI morphoDitaService = new MorphoDitaServiceJNI();

    public String stripSpans(String htmlDocument) {
        Document.OutputSettings settings = new Document.OutputSettings();
        settings.prettyPrint(false);
        final Document doc =Jsoup.parse(htmlDocument);
        for( Element element : doc.select("span") ) {
            if (!element.attr("score").equals("1.0")) {
                TextNode tn = new TextNode(" " +element.text().trim() + " ");
                element.before(tn);
                element.remove();
            }
        }

        return stripTagsWithWhitelist(doc);
    }

    public String stripTagsWithWhitelist(Document htmlDocument) {
        //final Document doc =Jsoup.parse(htmlDocument);

        Whitelist whitelist = Whitelist.none();
        whitelist.addTags("span");
        whitelist.addAttributes("span", "about", "resource", "score");
        whitelist.addProtocols("span", "score", "1.0");

        Document.OutputSettings settings = new Document.OutputSettings();
        settings.prettyPrint(false);

        htmlDocument.select("br").after(" ");
        htmlDocument.select("p").after(" ");

        String noTags = Jsoup.clean(htmlDocument.toString(),"", whitelist, settings);
        return noTags;

    }

    public String stripTags(String htmlDocument) {
        Document.OutputSettings settings = new Document.OutputSettings();
        settings.prettyPrint(false);
        Whitelist whitelist = Whitelist.none();
        Document doc = Jsoup.parse(htmlDocument);
        doc.select("br").after("\\n");
        doc.select("p").after("\\n");
        return Jsoup.clean(doc.toString(), whitelist);
    }

    public List<List<MorphoDitaResultJson>> morphoAnalyze(String s) {
        final List<List<MorphoDitaResultJson>> morphoDitaResult = morphoDitaService.getMorphoDiteResultProcessed(s);
        return morphoDitaResult;
    }

}
