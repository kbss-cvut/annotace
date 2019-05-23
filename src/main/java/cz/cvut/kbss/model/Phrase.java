/**
 * Annotac
 * Copyright (C) 2019 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * © 2019 GitHub, Inc.
 */
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
