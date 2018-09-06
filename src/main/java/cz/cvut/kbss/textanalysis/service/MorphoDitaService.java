package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.model.MorphoDitaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MorphoDitaService {

    private final RestTemplate restTemplate;

    private MorphoDitaResult morphoDitaResult;

    public MorphoDitaService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public MorphoDitaResult getMorphoDitaResult() {
        morphoDitaResult = this.restTemplate.getForObject("http://lindat.mff.cuni.cz/services/morphodita/api/analyze?data=Děti pojedou k babičce. Už se těší.", MorphoDitaResult.class);
        return morphoDitaResult;
    }
}
