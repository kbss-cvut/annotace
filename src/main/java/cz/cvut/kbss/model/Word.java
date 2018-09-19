package cz.cvut.kbss.model;

import java.util.Arrays;

public class Word {

    private String token;

    private String lemma;

    private Phrase[] phrases;

    private String stopChars;

    public Word(String lemma, String token, String stopChars, Phrase... phrases) {
        this.lemma = lemma;
        this.token = token;
        this.phrases = phrases;
        this.stopChars = stopChars;
    }

    public String getLemma() {
        return lemma;
    }

    public Phrase[] getPhrases() {
        return phrases;
    }

    public String getToken() {
        return token;
    }

    public String getStopChars() {
        return stopChars;
    }

    @Override public String toString() {
        return "Word{" + "token='" + token + '\'' + ", phrase=" + (phrases == null ? null :
                                                                   Arrays.asList(phrases)
        ) + ", stopChars='" + stopChars + '\'' + '}';
    }
}
