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
public class CurriculaEndpoint {

    private final CurriculaService service;

    /**
     * Constructor which receives the service as dependency.
     *
     * @param curriculaService CurriculaService object.
     */
    @Inject
    public CurriculaEndpoint(final CurriculaService curriculaService) {
        service = curriculaService;
    }

    /**
     * Returns all curricula.
     *
     * @return A JSON array of all the curricula.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurriculumSemesters() {
        try {
            final JsonNode result = service.getCurricula();
            final String jsonString = result.toString();
            return Response.status(Response.Status.OK).entity(jsonString).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
}

