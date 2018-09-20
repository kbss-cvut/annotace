package cz.cvut.kbss.textanalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Stopwords {

    public List<String> getStopwords(){
        List<String> stopwordsList = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(Stopwords.class.getResource("/stopwords-Czech.txt").getFile()));
            while (scanner.hasNextLine()){
                stopwordsList.add(scanner.next());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return stopwordsList;
    }
}
