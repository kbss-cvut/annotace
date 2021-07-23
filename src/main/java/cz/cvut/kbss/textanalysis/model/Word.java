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
package cz.cvut.kbss.textanalysis.model;

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

    public void setPhrases(Phrase[] phrases) {
        this.phrases = phrases;
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
