package org.fsg1.fmms.backend.endpoints;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Authentication endpoint.
 *
 * @author Tobias Derksen
 * @see org.fsg1.fmms.backend.filters.AuthFilter
 */
@Path("auth")
public class AuthEndpoint {

    /**
     * Endpoint for simply checking auth credentials.
     *
     * @return Empty response
     */
    @POST
    public Response auth() {
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
