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
package cz.cvut.kbss.textanalysis;

public class Constants {

    /**
     * Namespace of terms relevant for annotation.
     */
    public static final String NS_TERMIT = "http://onto.fel.cvut.cz/ontologies/application/termit/pojem/";

    /**
     * Prefix used to shorten {@link #NS_TERMIT}.
     */
    public static final String NS_TERMIT_PREFIX = "termit";

    /**
     * IRI of the annotation type, written in full.
     */
    public static final String TERM_OCCURRENCE = NS_TERMIT + "výskyt-termu";

    /**
     * IRI of the annotation type, written using prefix.
     */
    public static final String TERM_OCCURRENCE_PREFIXED = NS_TERMIT_PREFIX + ":výskyt-termu";

    /**
     * IRI of the annotation property, written using prefix.
     */
    public static final String TERM_OCCURRENCE_PROPERTY_PREFIXED = NS_TERMIT_PREFIX + ":je-přiřazením-termu";

    /**
     * HTML element used to wrap annotated content.
     */
    public static final String ANNOTATION_ELEMENT = "span";

    /**
     * RDF blank node prefix.
     */
    public static final String BNODE_PREFIX = "_:";
}
