package cz.cvut.kbss.annotace.configuration;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("annotace.morphodita")
public class MorphoditaConf {

    /**
     * Map language to tagger file.
     */
    private Map<String,String> taggers;

    private String service;
}
