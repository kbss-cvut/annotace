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
package cz.cvut.kbss.textanalysis.exception;

/**
 * Top level application-specific exception.
 */
public class AnnotaceException extends RuntimeException {

    public AnnotaceException(String message) {
        super(message);
    }

    public AnnotaceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnnotaceException(Throwable cause) {
        super(cause);
    }
}
