package cz.cvut.kbss.model;

import java.util.Objects;

public class Phrase {

    private final String termIri;

    private final boolean fullMatch;

    private final boolean important;

    private final String termLabel;

    public Phrase(String termIri,
                  boolean important,
                  boolean fullMatch, String termLabel) {
        this.termIri = termIri;
        this.important = important;
        this.fullMatch = fullMatch;
        this.termLabel = termLabel;
    }

    public String getTermIri() {
        return termIri;
    }

    public boolean isImportant() {
        return important;
    }

    public boolean isFullMatch() {
        return fullMatch;
    }

    public String getTermLabel() {return termLabel; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phrase phrase = (Phrase) o;
        return fullMatch == phrase.fullMatch &&
                important == phrase.important &&
                Objects.equals(termIri, phrase.termIri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(termIri, fullMatch, important);
    }

    @Override public String toString() {
        return "Phrase{" + "termIri='" + termIri + '\'' + ", fullMatch=" + fullMatch
               + ", important=" + important + '}';
    }
}
