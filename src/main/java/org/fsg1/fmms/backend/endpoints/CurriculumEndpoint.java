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
@Path("curriculum/{curriculum_id}")
public class CurriculumEndpoint extends Endpoint {

    /**
     * Constructor which receives the service as dependency.
     *
     * @param service Service object.
     */
    @Inject
    public CurriculumEndpoint(final CurriculumService service) {
        super(service);
    }

    /**
     * Returns all semesters in a curriculum.
     *
     * @param curriculumId Identifier of the curriculum.
     * @return A JSON list of all semesters in this curriculum.
     * @throws Exception In case the querying goes wrong.
     */
    @GET
    @Path("/semesters")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurriculumSemesters(@PathParam("curriculum_id") final int curriculumId) throws Exception {
        final CurriculumService service = (CurriculumService) getService();
        final JsonNode result = service.get(service.getQueryCurriculumSemestersString(), "semesters", curriculumId);
        final String jsonString = result.toString();
        return Response.status(Response.Status.OK).entity(jsonString).build();
    }

    /**
     * Get the complete information of one semester in a curriculum.
     *
     * @param curriculumId Identifier of the curriculum.
     * @param semesterId   Identifier of the semester.
     * @throws Exception In case the querying goes wrong.
     * @return A JSON Object containing the complete information of this semester.
     */
    @GET
    @Path("/semesters/{semester_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompleteSemester(@PathParam("curriculum_id") final int curriculumId,
                                        @PathParam("semester_id") final int semesterId) throws Exception {
        final CurriculumService service = (CurriculumService) getService();
        final JsonNode result = service.get(service.getQueryCompleteSemester(), "complete_semester", curriculumId, semesterId);
        final String jsonString = result.toString();
        return Response.status(Response.Status.OK).entity(jsonString).build();
    }

    /**
     * Returns all semesters in a curriculum.
     *
     * @param curriculumId Identifier of the curriculum.
     * @param moduleId     Identifier of the module.
     * @return A JSON list of all semesters in this curriculum.
     * @throws Exception In case the querying goes wrong.
     */
    @GET
    @Path("/modules/{module_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getModuleInformation(@PathParam("curriculum_id") final int curriculumId,
                                         @PathParam("module_id") final String moduleId) throws Exception {
        final CurriculumService service = (CurriculumService) getService();
        final JsonNode result = service.get(service.getQueryModuleInformation(), "module", moduleId, curriculumId);
        final String jsonString = result.toString();
        return Response.status(Response.Status.OK).entity(jsonString).build();
    }
}

