package cz.cvut.kbss.textanalysis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MorphoDitaResultJson {

    @JsonProperty("token")
    private String token;

    @JsonProperty("lemma")
    private String lemma;

    @JsonProperty("tag")
    private String tag;

    @JsonProperty("space")
    private String space;

    public MorphoDitaResultJson() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    @Override
    public String toString() {
        return "MorphoDitaResultJson{" +
                "token='" + token + '\'' +
                ", lemma='" + lemma + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
