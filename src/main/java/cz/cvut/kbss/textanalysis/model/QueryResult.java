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

import java.util.List;

public class QueryResult {

    private String type;

    private String label;

    private List<MorphoDitaResultJson> morphoDitaResultList;

    public QueryResult() {
    }

    public QueryResult(String type, String label) {
        this.type = type;
        this.label = label;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public List<MorphoDitaResultJson> getMorphoDitaResultList() {
        return morphoDitaResultList;
    }

    public void setMorphoDitaResultList(List<MorphoDitaResultJson> morphoDitaResultList) {
        this.morphoDitaResultList = morphoDitaResultList;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "type='" + type + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
