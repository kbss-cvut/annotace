package cz.cvut.kbss.model;

import java.util.Arrays;

public class Word {

    private String token;

    private Phrase[] phrase;

    private String stopChars;

    public Word(String token, String stopChars, Phrase... phrase) {
        this.token = token;
        this.phrase = phrase;
        this.stopChars = stopChars;
    }

    public String getToken() {
        return token;
    }

    public Phrase[] getPhrases() {
        return phrase;
    }

    public String getStopChars() {
        return stopChars;
    }

    @Override public String toString() {
        return "Word{" + "token='" + token + '\'' + ", phrase=" + (phrase == null ? null :
                                                                   Arrays.asList(phrase)
        ) + ", stopChars='" + stopChars + '\'' + '}';
    }
}
