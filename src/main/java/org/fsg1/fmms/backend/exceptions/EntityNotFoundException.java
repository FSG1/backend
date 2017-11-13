package org.fsg1.fmms.backend.exceptions;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * Exception to throw when no result is found by a query.
 */
public class EntityNotFoundException extends AppException {
    /**
     * Constructor.
     */
    public EntityNotFoundException() {
        super(NOT_FOUND.getStatusCode(), "No entity was found by the request. Make sure the query " +
                "parameters are accurate and your requested entity exists in the database.");
    }
}
