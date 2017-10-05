package resources;

import com.fasterxml.jackson.databind.node.ObjectNode;
import services.CurriculumService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("curriculum")
public class CurriculumResource {

    private final CurriculumService service;

    @Inject
    public CurriculumResource(CurriculumService service) {
        this.service = service;
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
    public Response getCurriculumSemesters(@PathParam("curriculum_id") String curriculumId) {
        try {
            final ObjectNode result = service.getCurriculumSemesters(curriculumId);
            final String JsonString = result.toString();
            return Response.status(200).entity(JsonString).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
}

