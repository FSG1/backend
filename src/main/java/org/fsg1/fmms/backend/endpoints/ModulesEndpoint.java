package org.fsg1.fmms.backend.endpoints;

import com.fasterxml.jackson.databind.JsonNode;
import org.fsg1.fmms.backend.services.ModulesService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The class containing the 'modules' endpoints.
 */
@Path("curriculum/{curriculum_id}")
public class ModulesEndpoint extends Endpoint {
    /**
     * Constructor which receives the service as dependency. In subclasses this dependency is automatically
     * injected by Jersey's DPI system.
     *
     * @param service Service object.
     */
    @Inject
    ModulesEndpoint(final ModulesService service) {
        super(service);
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
        final ModulesService service = (ModulesService) getService();
        final JsonNode result = service.get(service.getQueryModuleInformation(), "module", moduleId, curriculumId);
        final String jsonString = result.toString();
        return Response.status(Response.Status.OK).entity(jsonString).build();
    }
}
