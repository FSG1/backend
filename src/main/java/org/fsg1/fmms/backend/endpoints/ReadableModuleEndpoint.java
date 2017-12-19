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
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The class containing the 'modules' endpoints that are used to only display a module.
 */
@Path("curriculum/{curriculum_id}/module/{module_id}")
public class ReadableModuleEndpoint extends Endpoint<ModulesService> {

    /**
     * Constructor which receives the service as dependency. In subclasses this dependency is automatically
     * injected by Jersey's DPI system.
     *
     * @param service Service object.
     */
    @Inject
    ReadableModuleEndpoint(final ModulesService service) {
        super(service);
    }

    /**
     * Returns a module.
     *
     * @param curriculumId Identifier of the curriculum.
     * @param moduleId     Identifier of the module.
     * @return A JSON list of all semesters in this curriculum.
     * @throws Exception In case the querying goes wrong.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getModuleInformation(@PathParam("curriculum_id") final int curriculumId,
                                         @PathParam("module_id") final String moduleId) throws Exception {
        final ModulesService service = getService();
        final JsonNode result = service.get(service.getQueryModuleInformation(), "module", moduleId, curriculumId);
        final String jsonString = result.toString();
        return Response.status(Response.Status.OK).entity(jsonString).build();
    }

    @GET
    @Path("/pdf")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getModulePDF(@PathParam("curriculum_id") final int curriculumId,
                                 @PathParam("module_id") final String moduleId) throws Exception {
        final ModulesService service = getService();
        //final JsonNode result = service.get(service.getQueryModuleInformation(), "module", moduleId, curriculumId);
        File file = new File("src/test/resources/pdf.pdf");
        return Response.status(Response.Status.OK).entity(file)
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" )
                .build();
    }
}
