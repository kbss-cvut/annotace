package cz.cvut.kbss.textanalysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Stopwords {

    public List<String> getStopwords(String lang) {
        try(InputStream resource = getClass().getResourceAsStream("/stopwords-" + lang + ".txt" )) {
            return
                    new BufferedReader(new InputStreamReader(resource,
                        StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Unable to load stopwords, leaving them empty. ", e);
            return Collections.emptyList();
        }
    }
}
