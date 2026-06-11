/*
 * Annotace
 * Copyright (C) 2026 Czech Technical University in Prague
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
 *along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package cz.cvut.kbss.textanalysis.rest.handler;

import cz.cvut.kbss.textanalysis.exception.AnnotaceException;
import cz.cvut.kbss.textanalysis.exception.UnsupportedLanguageException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception handlers for REST endpoints.
 * <p>
 * The general pattern should be that unless an exception can be handled in a more appropriate place it bubbles up to a
 * REST endpoint which originally received the request. There, it is caught by these mappers, logged and a reasonable
 * error message is returned to the user.
 */
public class RestExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static void logException(AnnotaceException ex, UriInfo uriInfo) {
        LOG.error("Exception caught when processing request to '{}'.", uriInfo.getPath(), ex);
    }

    private static ErrorInfo errorInfo(UriInfo uriInfo, Throwable e) {
        return ErrorInfo.createWithMessage(e.getMessage(), uriInfo.getPath());
    }

    @Provider
    public static class UnsupportedLanguageExceptionMapper
            implements ExceptionMapper<UnsupportedLanguageException> {

        @Context
        UriInfo uriInfo;

        @Override
        public Response toResponse(UnsupportedLanguageException ex) {
            logException(ex, uriInfo);
            return Response.status(Response.Status.CONFLICT).entity(errorInfo(uriInfo, ex)).build();
        }
    }

    @Provider
    public static class AnnotaceExceptionMapper implements ExceptionMapper<AnnotaceException> {

        @Context
        UriInfo uriInfo;

        @Override
        public Response toResponse(AnnotaceException ex) {
            logException(ex, uriInfo);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(errorInfo(uriInfo, ex)).build();
        }
    }
}
