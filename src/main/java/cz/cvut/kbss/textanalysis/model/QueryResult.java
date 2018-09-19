package cz.cvut.kbss.textanalysis.model;

import java.util.List;

public class QueryResult {

    private String type;

    private String label;

    private List<List<MorphoDitaResultJson>> morphoDitaResultList;

    public QueryResult() {
    }


    public QueryResult(String type, String label, List<List<MorphoDitaResultJson>> morphoDitaResultList) {
        this.type = type;
        this.label = label;
        this.morphoDitaResultList = morphoDitaResultList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<List<MorphoDitaResultJson>> getMorphoDitaResultList() {
        return morphoDitaResultList;
    }

    public void setMorphoDitaResultList(List<List<MorphoDitaResultJson>> morphoDitaResultList) {
        this.morphoDitaResultList = morphoDitaResultList;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "type='" + type + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
