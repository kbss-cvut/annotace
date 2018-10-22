package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.textanalysis.model.MorphoDitaResultJson;
import cz.cvut.kbss.textanalysis.service.morphodita.MorphoDitaServiceAPI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController public class MorphoDitaController {

    @Autowired private MorphoDitaServiceAPI morphoDitaService;

    @RequestMapping("/morphodita-result-processed")
    public List<List<MorphoDitaResultJson>> getMorphoDitaResultProcessed(
        @RequestParam("textChunk") String textChunk) {
        return morphoDitaService.getMorphoDiteResultProcessed(textChunk);
    }
}
