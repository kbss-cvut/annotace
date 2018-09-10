package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.model.MorphoDitaResult;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class MorphoDitaService {

    private final RestTemplate restTemplate;

    private MorphoDitaResult morphoDitaResult;

    String path = "C:/Projects/OPPPR/services/textanalysis/src/main/resources/sample.txt";
    String s = "";


    public MorphoDitaService(RestTemplateBuilder restTemplateBuilder) throws IOException {
        this.restTemplate = restTemplateBuilder.build();
    }

    public MorphoDitaResult getMorphoDitaResult() {
        try
        {
            s = new String(Files.readAllBytes(Paths.get(path)));
        }
        catch (IOException e){
            e.printStackTrace();
        }
        System.out.println(s);
        morphoDitaResult = this.restTemplate.getForObject("http://lindat.mff.cuni.cz/services/morphodita/api/analyze?data=Děti pojedou k babičce. Už se těší.", MorphoDitaResult.class);
//        morphoDitaResult = this.restTemplate.getForObject("http://lindat.mff.cuni.cz/services/morphodita/api/analyze?data=" + s, MorphoDitaResult.class);
        return morphoDitaResult;
    }
}
