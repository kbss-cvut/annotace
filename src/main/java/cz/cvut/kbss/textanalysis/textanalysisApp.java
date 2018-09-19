package cz.cvut.kbss.textanalysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class textanalysisApp {

    private static final Logger log = LoggerFactory.getLogger(textanalysisApp.class);

    public static void main(String[] args){
        SpringApplication.run(textanalysisApp.class);
    }

}
