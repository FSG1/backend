package org.fsg1.fmms.backend.exceptions;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * Exception to throw when a request body is empty.
 */
public class EmptyRequestBodyException extends AppException {
    /**
     * Constructor.
     */
    public EmptyRequestBodyException() {
        super(BAD_REQUEST.getStatusCode(), "Request body is empty. Please send something.");
    }
}
