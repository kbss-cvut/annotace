package cz.cvut.kbss.textanalysis.model;

import java.util.List;

public class AnnotationsResult {

    private MorphoDitaResultJson result;

    private List<MatchedAnnotation> annotations;

    public AnnotationsResult() {
    }

    public AnnotationsResult(MorphoDitaResultJson morphoDitaResult) {
        this.result = morphoDitaResult;
    }

    public MorphoDitaResultJson getResult() {
        return result;
    }

    public void setResult(MorphoDitaResultJson result) {
        this.result = result;
    }

    public List<MatchedAnnotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<MatchedAnnotation> annotations) {
        this.annotations = annotations;
    }

    @Override
    public String toString() {
        return "AnnotationsResult{" +
                "result=" + result +
                ", annotations=" + annotations +
                '}';
    }
}
