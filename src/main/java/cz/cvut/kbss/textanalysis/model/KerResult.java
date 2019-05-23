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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KerResult {

    private List<String> keywords;

    public KerResult() {
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public static KerResult createEmpty() {
        KerResult response = new KerResult();
        response.setKeywords(Collections.emptyList());
        return response;
    }

    public static KerResult createSomeList() {
        List<String> keywords = Arrays.asList("park", "město", "metropolitní", "technický");
        KerResult response = new KerResult();
        response.setKeywords(keywords);
        return response;
    }

    @Override
    public String toString() {
        return "KerResult{" +
                "keywords=" + keywords +
                '}';
    }
}
