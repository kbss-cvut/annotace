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

import cz.cvut.kbss.textanalysis.exception.AnnotaceException;
import cz.cvut.kbss.textanalysis.exception.UnsupportedLanguageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * Exception handlers for REST controllers.
 * <p>
 * The general pattern should be that unless an exception can be handled in a more appropriate place it bubbles up to a
 * REST controller which originally received the request. There, it is caught by this handler, logged and a reasonable
 * error message is returned to the user.
 */
@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static void logException(AnnotaceException ex, HttpServletRequest request) {
        LOG.error("Exception caught when processing request to '{}'.", request.getRequestURI(), ex);
    }

    private static ErrorInfo errorInfo(HttpServletRequest request, Throwable e) {
        return ErrorInfo.createWithMessage(e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UnsupportedLanguageException.class)
    public ResponseEntity<ErrorInfo> unsupportedLanguageException(UnsupportedLanguageException ex,
                                                                  HttpServletRequest request) {
        logException(ex, request);
        return new ResponseEntity<>(errorInfo(request, ex), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AnnotaceException.class)
    public ResponseEntity<ErrorInfo> annotaceException(AnnotaceException ex, HttpServletRequest request) {
        logException(ex, request);
        return new ResponseEntity<>(errorInfo(request, ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
