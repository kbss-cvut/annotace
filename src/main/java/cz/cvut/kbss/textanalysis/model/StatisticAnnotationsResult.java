package cz.cvut.kbss.textanalysis.model;

public class StatisticAnnotationsResult {

    private String keyword;

    private String type;

    private String label;

    public StatisticAnnotationsResult() {
    }

    public StatisticAnnotationsResult(String keyword, String type, String label) {
        this.keyword = keyword;
        this.type =  type;
        this.label = label;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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

    @Override
    public String toString() {
        return "StatisticAnnotationsResult{" +
                "keyword='" + keyword + '\'' +
                ", type='" + type + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
