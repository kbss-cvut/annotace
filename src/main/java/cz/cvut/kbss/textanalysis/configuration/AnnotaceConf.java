package cz.cvut.kbss.textanalysis.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("annotace")
public class AnnotaceConf {

    private String serverPort;

    private String morphoditaTagger;

    private String morphoditaServer;
}
