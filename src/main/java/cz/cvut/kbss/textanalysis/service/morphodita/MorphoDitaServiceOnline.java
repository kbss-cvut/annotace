package cz.cvut.kbss.textanalysis.service.morphodita;

import cz.cvut.kbss.textanalysis.model.MorphoDitaResult;
import cz.cvut.kbss.textanalysis.model.MorphoDitaResultJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MorphoDitaServiceOnline implements MorphoDitaServiceAPI {
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    public List<List<MorphoDitaResultJson>> getMorphoDiteResultProcessed(String s) {
        final MorphoDitaResult morphoDitaResult = restTemplateBuilder.build().getForObject(
            "http://lindat.mff.cuni.cz/services/morphodita/api/tag?data=" + s +"&output=json",
            MorphoDitaResult.class);
        return morphoDitaResult.getResult();
    }
}
