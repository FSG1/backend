package org.fsg1.fmms.backend.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Authentication endpoint
 *
 * @author Tobias Derksen
 * @see org.fsg1.fmms.backend.filters.AuthFilter
 */
@Path("restricted/auth")
public class AuthEndpoint {

    /**
     * Endpoint for simply checking auth credentials
     *
     * @return Empty response
     */
    @GET
    public Response auth() {
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
