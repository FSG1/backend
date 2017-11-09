package org.fsg1.fmms.backend.endpoints;

import org.fsg1.fmms.backend.services.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("sp/qualifications")
public class QualificationsEndpoint extends Endpoint {
    /**
     * Constructor which receives the service as dependency.
     *
     * @param service Service object.
     */
    QualificationsEndpoint(QualificationsService service) {
        super(service);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQualifications() throws Exception {

    }
}
