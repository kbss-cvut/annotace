package cz.cvut.kbss.model;

public class Phrase {

    private final String termIri;

    private final boolean fullMatch;

    private final boolean important;

    public Phrase(String termIri,
                  boolean important,
                  boolean fullMatch) {
        this.termIri = termIri;
        this.important = important;
        this.fullMatch = fullMatch;
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

    @Override public String toString() {
        return "Phrase{" + "termIri='" + termIri + '\'' + ", fullMatch=" + fullMatch
               + ", important=" + important + '}';
    }
}
