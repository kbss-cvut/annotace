package cz.cvut.kbss.textanalysis.config;

import cz.cvut.kbss.annotace.configuration.MorphoditaConf;
import cz.cvut.kbss.annotace.configuration.SparkConf;
import cz.cvut.kbss.annotace.lemmatizer.MorphoDitaServiceJNI;
import cz.cvut.kbss.annotace.lemmatizer.MorphoDitaServiceOnline;
import cz.cvut.kbss.annotace.lemmatizer.SparkLemmatizer;
import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LemmatizerConfig {

    @Value("${annotace.lemmatizer:spark}")
    private String lemmatizer;

    @Bean
    public LemmatizerApi lemmatizer(ApplicationContext context) {
        switch (lemmatizer) {
            case "morphodita-jni":
                log.info("Instantiating MorphoDiTa JNI lemmatizer.");
                return new MorphoDitaServiceJNI(context.getBean(MorphoditaConf.class));
            case "morphodita-online":
                log.info("Instantiating MorhoDiTa online lemmatizer.");
                return new MorphoDitaServiceOnline(context.getBean(RestTemplateBuilder.class), context.getBean(
                        MorphoditaConf.class));
            default:
                log.info("Instantiating Apache Spark lemmatizer.");
                return new SparkLemmatizer(context.getBean(SparkConf.class));
        }
    }
}
