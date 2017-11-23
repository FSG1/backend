package org.fsg1.fmms.backend.exceptions;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * Will be thrown when a user tries to access a restricted endpoint without authentication.
 *
 * @author Tobias Derksen
 */
public class UnauthorizedException extends AppException {
    /**
     * Constructor.
     */
    public UnauthorizedException() {
        super(UNAUTHORIZED.getStatusCode(), "Please provide a proper authentication.");
    }
}
