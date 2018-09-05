package cz.cvut.kbss.textanalysis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MorphoDitaResult {

    private String result;

    public MorphoDitaResult() {
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "MorphoDitaResult{" +
                "result='" + result + '\'' +
                '}';
    }
}
