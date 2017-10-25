package org.fsg1.fmms.backend.exceptions;

/**
 * Exception to throw when no result is found by a query.
 */
public class EntityNotFoundException extends Exception {
    /**
     * Constructor.
     */
    public EntityNotFoundException() {
        super("No entity was found by the query");
    }
}
