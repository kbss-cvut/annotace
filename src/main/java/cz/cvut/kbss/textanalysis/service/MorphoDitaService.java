package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.model.MorphoDitaResult;
import cz.cvut.kbss.textanalysis.model.MorphoDitaResultJson;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class MorphoDitaService implements MorphoDitaServiceAPI {

    private final RestTemplate restTemplate;

    private MorphoDitaResult morphoDitaResult;

    String path = "C:/Projects/OPPPR/services/textanalysis/src/main/resources/test.txt";
    String s = "";

    public MorphoDitaService(RestTemplateBuilder restTemplateBuilder) throws IOException {
        this.restTemplate = restTemplateBuilder.build();
    }

    public MorphoDitaResult getMorphoDitaResult() {
        return this.restTemplate.getForObject(
            "http://lindat.mff.cuni.cz/services/morphodita/api/tag?data=Děti pojedou k babičce. Už se těší.&output=json",
            MorphoDitaResult.class
        );
    }

    public List<List<MorphoDitaResultJson>> getMorphoDiteResultProcessed(String s) {
        //s = "čtvrťové plocha dopravní a technické infrastruktury.";
        if (s == null){
            try
                {
                    s = new String(Files.readAllBytes(Paths.get(path)));
                }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        morphoDitaResult = this.restTemplate.getForObject("http://lindat.mff.cuni.cz/services/morphodita/api/tag?data=" + s +"&output=json", MorphoDitaResult.class);
        List<List<MorphoDitaResultJson>> morphoDitaResultList;
        morphoDitaResultList = morphoDitaResult.getResult();
        return morphoDitaResultList;
    }

}
