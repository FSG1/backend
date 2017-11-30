package org.fsg1.fmms.backend.endpoints;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.fsg1.fmms.backend.services.ModulesService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The class containing the 'modules' endpoints.
 */
@Path("")
public class EditableModuleEndpoint extends Endpoint<ModulesService> {
    /**
     * Constructor which receives the service as dependency. In subclasses this dependency is automatically
     * injected by Jersey's DPI system.
     *
     * @param service Service object.
     */
    @Inject
    EditableModuleEndpoint(final ModulesService service) {
        super(service);
    }

    /**
     * Post a module to be updated.
     *
     * @param module       Module object containing the updated information. In this case an object resembling a Module, which
     *                     is shown in test/resources/json/postModule.json.
     * @param moduleId     Identifier of the module.
     * @return A Response with status code 200 if the update went well, or status code 500
     * if an error occurred internally.
     * @throws Exception In case the update went wrong.
     */
    @POST
    @Path("module/{module_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postModuleInformation(@PathParam("module_id") final int moduleId,
                                          final JsonNode module) throws Exception {
        final ModulesService service = getService();

        final int id = module.findValue("id").asInt();
        final String code = module.findValue("code").asText();
        final String name = module.findValue("name").asText();
        final int credits = module.findValue("credits").asInt();
        final int lecturesPerWeek = module.findValue("lectures_in_week").asInt();
        final int practicalPerWeek = module.findValue("practical_hours_week").asInt();
        final String introText = module.findValue("introductorytext").asText();
        final ArrayNode topics = ((ArrayNode) module.findValue("topics"));
        final ArrayNode teachingMaterials = ((ArrayNode) module.findValue("teaching_material"));
        final String additionalInformation = module.findValue("additional_information").asText();
        final ArrayNode lecturers = ((ArrayNode) module.findValue("lecturers"));
        final String credentials = module.findValue("credentials").asText();
        final boolean isProject = module.findValue("project_flag").asBoolean();

        //List all parameters in the order in which they occur in the statement
        final String[] queries = service.getUpdateModuleInformationStatements();

        service.executeTransactional(conn -> {
            //comments
            service.update(conn, queries[0],
                    code, name, credits, lecturesPerWeek, practicalPerWeek, isProject, id);

            service.update(conn, queries[1],
                    id);

            for (JsonNode topic : topics) {
                service.update(conn, queries[2],
                        id, topic.asText());
            }

            service.update(conn, queries[3],
                    introText, additionalInformation, credentials, id);

            service.update(conn, queries[4],
                    id);

            for (JsonNode teachingMaterial : teachingMaterials) {
                service.update(conn, queries[5],
                        id, teachingMaterial.findValue("type").asText(), teachingMaterial.findValue("name").asText());
            }

            service.update(conn, queries[6],
                    id);

            for (JsonNode lecturer : lecturers) {
                service.update(conn, queries[7],
                        id, lecturer.asInt());
            }
        });

        return Response.status(Response.Status.OK).build();
    }

    /**
     * Returns a module to be edited.
     *
     * @param moduleCode Code of the module.
     * @return A JSON object of a module with extra information to allow editing.
     * @throws Exception In case the querying goes wrong.
     */
    @GET
    @Path("module/{module_code}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEditableModule(@PathParam("module_code") final String moduleCode) throws Exception {
        final ModulesService service = getService();
        final JsonNode result = service.get(service.getQueryEditableModule(), "module", moduleCode);
        final String jsonString = result.toString();
        return Response.status(Response.Status.OK).entity(jsonString).build();
    }
}
