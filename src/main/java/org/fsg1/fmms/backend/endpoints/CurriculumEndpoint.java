package org.fsg1.fmms.backend.endpoints;

import com.fasterxml.jackson.databind.JsonNode;
import org.fsg1.fmms.backend.services.CurriculumService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The class containing the curriculum endpoints.
 */
@Path("curriculum")
public class CurriculumEndpoint {

    private final CurriculumService service;

    /**
     * Constructor which receives the service as dependency.
     *
     * @param curriculumService CurriculumService object.
     */
    @Inject
    public CurriculumEndpoint(final CurriculumService curriculumService) {
        service = curriculumService;
    }

    /**
     * Returns all semesters in a curriculum.
     *
     * @param curriculumId Identifier of the curriculum.
     * @return A JSON list of all semesters in this curriculum.
     */
    @GET
    @Path("/{curriculum_id}/semesters")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurriculumSemesters(@PathParam("curriculum_id") final int curriculumId) {
        try {
            final JsonNode result = service.execute(curriculumId);
            final String jsonString = result.toString();
            return Response.status(Response.Status.OK).entity(jsonString).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
}

