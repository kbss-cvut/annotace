package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.textanalysis.model.MorphoDitaResult;
import cz.cvut.kbss.textanalysis.service.MorphoDitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MorphoDitaController {

    @Autowired
    private MorphoDitaService morphoDitaService;

    @RequestMapping("/hello")
    public String sayHi() {
        return "Hi";
    }

    @RequestMapping("/morphodita-result")
    public MorphoDitaResult getMorphoditaResult() {
        return morphoDitaService.getMorphoDitaResult();
    }
}
