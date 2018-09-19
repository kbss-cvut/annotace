package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.textanalysis.model.MorphoDitaResultJson;
import cz.cvut.kbss.textanalysis.service.morphodita.MorphoDitaServiceAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MorphoDitaController {

    @Autowired
    private MorphoDitaServiceAPI morphoDitaService;

    @RequestMapping("/morphodita-result-processed")
    public List<List<MorphoDitaResultJson>> getMorphoDitaResultProcessed(String s) {
        List<List<MorphoDitaResultJson>> morphoDitaResultList;
        morphoDitaResultList = morphoDitaService.getMorphoDiteResultProcessed(s);
        //System.out.println(morphoDitaResultList.get(0).get(0).getToken());
        return morphoDitaResultList;
    }
}
