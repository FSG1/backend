package org.fsg1.fmms.backend.exceptions;


import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Class to map application related exceptions.
 */
public class AppException extends WebApplicationException {

    /**
     * @param status       status code.
     * @param errorMessage error message.
     */
    public AppException(final int status, final String errorMessage) {
        super(Response
                .status(status)
                .entity(errorMessage)
                .build());
    }

    /**
     * Default constructor so object mapping from Jackson does not break.
     */
    AppException() {
        super();
    }

}
