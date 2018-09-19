package cz.cvut.kbss.textanalysis.model;

public class MatchedAnnotation {

    private String type;

    private String lable;

    private boolean matched;

    public MatchedAnnotation() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    @Override
    public String toString() {
        return "MatchedAnnotation{" +
                "type='" + type + '\'' +
                ", lable='" + lable + '\'' +
                ", matched=" + matched +
                '}';
    }
}
