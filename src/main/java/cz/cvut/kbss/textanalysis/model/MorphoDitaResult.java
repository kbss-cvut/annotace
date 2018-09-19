package cz.cvut.kbss.textanalysis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MorphoDitaResult {

    @JsonProperty
    private List<List<MorphoDitaResultJson>> result;

    public MorphoDitaResult() {
    }

    public List<List<MorphoDitaResultJson>> getResult() {
        return result;
    }

    public void setResult(List<List<MorphoDitaResultJson>> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "MorphoDitaResult{" +
                "result=" + result +
                '}';
    }
}
