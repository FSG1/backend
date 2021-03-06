package org.fsg1.fmms.backend.endpoints;

import com.fasterxml.jackson.databind.JsonNode;
import org.fsg1.fmms.backend.services.CurriculaService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The class containing the curricula endpoints.
 */
@Path("curricula")
public class CurriculaEndpoint extends Endpoint<CurriculaService> {

    /**
     * Constructor which receives the service as dependency.
     *
     * @param service Service object.
     */
    @Inject
    public CurriculaEndpoint(final CurriculaService service) {
        super(service);
    }

    /**
     * Returns all curricula.
     *
     * @return A JSON array of all the curricula.
     * @throws Exception In case the querying goes wrong.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurriculumSemesters() throws Exception {
        final CurriculaService service = getService();
        final JsonNode result = service.get(service.getQueryCurriculaString(), "curricula");
        final String jsonString = result.toString();
        return Response.status(Response.Status.OK).entity(jsonString).build();
    }
}

