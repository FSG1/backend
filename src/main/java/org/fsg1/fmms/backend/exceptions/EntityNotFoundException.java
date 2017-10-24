package org.fsg1.fmms.backend.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException() {
        super("No entity was found by the query");
    }
}
