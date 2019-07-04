package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.Stopwords;
import org.junit.jupiter.api.Test;

import java.util.List;

public class StopwordsTest {
    public Stopwords stopwords = new Stopwords();

    @Test
    public void print(){
        List<String> stopwordsList = stopwords.getStopwords("cs");
        for(String s:stopwordsList){
            System.out.println(s);
        }
    }
}
