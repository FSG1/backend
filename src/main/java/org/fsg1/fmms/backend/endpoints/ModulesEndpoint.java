package org.fsg1.fmms.backend.endpoints;

import com.fasterxml.jackson.databind.JsonNode;
import org.fsg1.fmms.backend.exceptions.EntityNotFoundException;
import org.fsg1.fmms.backend.services.ModuleService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Endpoint for modules.
 */
@Path("modules")
public class ModulesEndpoint extends Endpoint {

    /**
     * Constructor which receives the service as dependency.
     *
     * @param modulesService Service object.
     */
    @Inject
    public ModulesEndpoint(final ModuleService modulesService) {
        super(modulesService);
    }

    /**
     * Returns information of one module.
     *
     * @param moduleId Identifier of the module.
     * @return All information of a module, plus the lifecycle activities and architectural layers.
     */
    @GET
    @Path("/{module_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getModuleInformation(@PathParam("module_id") final String moduleId) {
        try {
            final ModuleService service = (ModuleService) getService();
            final JsonNode result = service.get(service.getQueryModuleInformation(), moduleId);
            return Response.status(Response.Status.OK).entity(result.toString()).build();
        } catch (EntityNotFoundException enfe) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
}
