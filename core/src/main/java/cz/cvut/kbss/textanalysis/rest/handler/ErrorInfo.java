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
package cz.cvut.kbss.textanalysis.rest.handler;

import lombok.Getter;
import lombok.Setter;

/**
 * Contains information about an error and can be sent to client as JSON to let him know what is wrong.
 */
@Setter
@Getter
public class ErrorInfo {

    /**
     * Readable message describing the error
     */
    private String message;

    private String requestUri;


    public ErrorInfo() {
    }

    public ErrorInfo(String requestUri) {
        this.requestUri = requestUri;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" + requestUri + ", message='" + message + "'}";
    }

    /**
     * Creates a new instance with the specified message and request URI.
     *
     * @param message    Error message
     * @param requestUri URI of the request which caused the error
     * @return New {@code ErrorInfo} instance
     */
    public static ErrorInfo createWithMessage(String message, String requestUri) {
        final ErrorInfo errorInfo = new ErrorInfo(requestUri);
        errorInfo.setMessage(message);
        return errorInfo;
    }
}
