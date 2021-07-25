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

public class MatchedAnnotation {

    private String type;

    private String lable;

    private boolean fullMatch;

    public MatchedAnnotation() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public boolean isFullMatch() {
        return fullMatch;
    }

    public void setFullMatch(boolean fullMatch) {
        this.fullMatch = fullMatch;
    }

    @Override
    public String toString() {
        return "MatchedAnnotation{" +
                "type='" + type + '\'' +
                ", lable='" + lable + '\'' +
                ", fullMatch=" + fullMatch +
                '}';
    }
}
