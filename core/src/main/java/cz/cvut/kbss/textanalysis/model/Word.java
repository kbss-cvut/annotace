/*
 * Annotace
 * Copyright (C) 2024 Czech Technical University in Prague
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
 */
package cz.cvut.kbss.textanalysis.model;

import lombok.Data;

@Data
public class Word {

    private String token;

    private String lemma;

    private String leadingChars;

    private String stopChars;

    private Phrase[] phrases;

    public Word(String lemma, String token, String leadingChars, String stopChars, Phrase... phrases) {
        this.lemma = lemma;
        this.token = token;
        this.leadingChars = leadingChars != null ? leadingChars : "";
        this.stopChars = stopChars;
        this.phrases = phrases;
    }
}
