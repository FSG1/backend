package org.fsg1.fmms.backend.exceptions;

/**
 * Exception to throw when no result is found by a query.
 */
public class EntityNotFoundException extends AppException {
    /**
     * Constructor.
     */
    public EntityNotFoundException() {
        super(404, "No entity was found by the request.", "Make sure the query " +
                "parameters are accurate and your requested entity exists in the database.");
    }
}
