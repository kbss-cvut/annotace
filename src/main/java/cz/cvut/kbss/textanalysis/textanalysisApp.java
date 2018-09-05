package cz.cvut.kbss.textanalysis;

import cz.cvut.kbss.textanalysis.model.MorphoDitaResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
public class textanalysisApp {

    private static final Logger log = LoggerFactory.getLogger(textanalysisApp.class);

    public static void main(String[] args){
        SpringApplication.run(textanalysisApp.class, args);
    }

//    @Bean
//    public RestTemplate restTemplate(RestTemplateBuilder builder) {
//        return builder.build();
//    }
//
//    @Bean
//    public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
//        return args -> {
//            MorphoDitaResult morphoDitaResult = restTemplate.getForObject(
//                    "http://lindat.mff.cuni.cz/services/morphodita/api/analyze?data=Děti pojedou k babičce. Už se těší.", MorphoDitaResult.class);
//            log.info(morphoDitaResult.toString());
//        };
//    }

}
