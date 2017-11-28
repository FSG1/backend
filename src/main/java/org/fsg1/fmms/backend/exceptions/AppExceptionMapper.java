package org.fsg1.fmms.backend.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

/**
 * The class that maps caught exception from the application to an appropriate response.
 */
public class AppExceptionMapper implements ExceptionMapper<Exception> {

    /**
     * Makes a Response from an Exception.
     *
     * @param ex The exception to process.
     * @return A Response to give to the client.
     */
    @Override
    public Response toResponse(final Exception ex) {
        if (ex instanceof WebApplicationException) return ((WebApplicationException) (ex)).getResponse();
        ex.printStackTrace();
        return Response.status(INTERNAL_SERVER_ERROR)
                .entity(ex.toString())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}

