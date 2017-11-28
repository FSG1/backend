package org.fsg1.fmms.backend.endpoints;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.fsg1.fmms.backend.services.ModulesService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;

/**
 * The class containing the 'modules' endpoints.
 */
@Path("curriculum/{curriculum_id}")
public class ModulesEndpoint extends Endpoint<ModulesService> {
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
    @Path("/module/{module_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getModuleInformation(@PathParam("curriculum_id") final int curriculumId,
                                         @PathParam("module_id") final String moduleId) throws Exception {
        final ModulesService service = getService();
        final JsonNode result = service.get(service.getQueryModuleInformation(), "module", moduleId, curriculumId);
        final String jsonString = result.toString();
        return Response.status(Response.Status.OK).entity(jsonString).build();
    }

    /**
     * Post a module to be updated.
     *
     * @param module       Module object containing the updated information. In this case an object resembling a Module, which
     *                     is shown in test/resources/json/postModule.json.
     * @param curriculumId Identifier of the curriculum.
     * @param moduleId     Identifier of the module.
     * @return A Response with status code 200 if the update went well, or status code 500
     * if an error occurred internally.
     * @throws Exception In case the update went wrong.
     */
    @POST
    @Path("/module/{module_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postModuleInformation(@PathParam("curriculum_id") final int curriculumId,
                                          @PathParam("module_id") final String moduleId,
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

        final Connection connection = service.startTransaction();

        service.post(connection, queries[0],
                code, name, credits, lecturesPerWeek, practicalPerWeek, isProject, id);

        service.post(connection, queries[1],
                id);

        for (JsonNode topic : topics) {
            service.post(connection, queries[2],
                    id, topic.asText());
        }

        service.post(connection, queries[3],
                id, id, introText, additionalInformation, credentials, id);

        service.post(connection, queries[4],
                id);

        for (JsonNode teachingMaterial : teachingMaterials) {
            service.post(connection, queries[5],
                    id, teachingMaterial.findValue("type").asText(), teachingMaterial.findValue("name").asText());
        }

        service.post(connection, queries[6],
                id);

        for (JsonNode lecturer : lecturers) {
            service.post(connection, queries[7],
                    id, lecturer.asInt());
        }

        service.commitTransaction(connection);

        return Response.status(Response.Status.OK).build();
    }
}
