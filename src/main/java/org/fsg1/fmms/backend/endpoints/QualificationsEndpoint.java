package org.fsg1.fmms.backend.endpoints;

import com.fasterxml.jackson.databind.JsonNode;
import org.fsg1.fmms.backend.services.QualificationsService;
import org.fsg1.fmms.backend.services.Service;

import javax.inject.Inject;
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
    @Inject
    QualificationsEndpoint(QualificationsService service) {
        super(service);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQualifications() throws Exception {
        final QualificationsService service = (QualificationsService) getService();
        final JsonNode result = service.get(service.getQualificationsQuery(), "qualifications");
        final String jsonString = result.toString();
        return Response.status(Response.Status.OK).entity(jsonString).build();
    }
}
