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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MorphoDitaResult {

    @JsonProperty
    private List<List<MorphoDitaResultJson>> result;

    public MorphoDitaResult() {
    }

    public List<List<MorphoDitaResultJson>> getResult() {
        return result;
    }

    public void setResult(List<List<MorphoDitaResultJson>> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "MorphoDitaResult{" +
                "result=" + result +
                '}';
    }
}
