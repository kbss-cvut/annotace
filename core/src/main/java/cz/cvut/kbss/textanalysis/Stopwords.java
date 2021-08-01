package cz.cvut.kbss.textanalysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class Stopwords {

    public List<String> getStopwords(String lang){
        try {
            if (lang.equals("cs"))
                return Files.readAllLines(new File(Stopwords.class.getClassLoader().getResource("stopwords-Czech.txt").getFile()).toPath());
            else
                return Files.readAllLines(new File(Stopwords.class.getClassLoader().getResource("stopwords-English.txt").getFile()).toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
