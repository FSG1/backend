package org.fsg1.fmms.backend.endpoints;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.fsg1.fmms.backend.services.ModulesService;
import org.fsg1.fmms.backend.util.ArchitecturalLayerMapper;
import org.fsg1.fmms.backend.util.LifecycleActivityMapper;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import static org.fsg1.fmms.backend.util.StringEscaper.escapeString;

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

    /**
     * Returns a latex string of the selected module.
     *
     * @param curriculumId Identifier of the curriculum.
     * @param moduleId     Identifier of the module.
     * @return A filled in latex template as plain text.
     * @throws Exception In case the querying goes wrong.
     */
    @GET
    @Path("/pdf")
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public Response getModulePDF(@PathParam("curriculum_id") final int curriculumId,
                                 @PathParam("module_id") final String moduleId) throws Exception {
        final ModulesService service = getService();
        final JsonNode module = service.get(service.getQueryModuleInformation(), "module", moduleId, curriculumId);

        final String code = module.findValue("code").asText();
        final String name = module.findValue("name").asText();
        final int semester = module.findValue("semester").asInt();
        final int credits = module.findValue("credits").asInt();
        final int lecturesPerWeek = module.findValue("lectures_in_week").asInt();
        final int practicalPerWeek = module.findValue("practical_hours_week").asInt();
        final String introText = module.findValue("introductorytext").asText();
        final ArrayNode topics = ((ArrayNode) module.findValue("topics"));
        final ArrayNode teachingMaterials = ((ArrayNode) module.findValue("teaching_material"));
        final String additionalInformation = module.findValue("additional_information").asText();
        final Iterator<JsonNode> lecturers = module.findValue("lecturers").iterator();
        final String credentials = module.findValue("credentials").asText();
        final ArrayNode learningGoals = (ArrayNode) module.findValue("learning_goals");
        final ArrayNode assessmentParts = (ArrayNode) module.findValue("assesment_parts");
        final ArrayNode moduleLinks = (ArrayNode) module.findValue("prior_knowledge_references");
        final ArrayNode qualifications = (ArrayNode) module.findValue("qualifications");

        final StringBuilder latexBuilder = new StringBuilder();
        final String latexFunctions = new String(Files.readAllBytes(Paths.get("src/test/resources/latex/functions.tex")));
        latexBuilder.append(latexFunctions);

        StringBuilder arrayLinker = new StringBuilder();
        lecturers.forEachRemaining(lecturer -> arrayLinker.append(lecturer.asText()).append(", "));
        if (arrayLinker.length() > 2) {
            arrayLinker.deleteCharAt(arrayLinker.length() - 1);
            arrayLinker.deleteCharAt(arrayLinker.length() - 1);
        }
        final String lecturersLinked = arrayLinker.toString();
        arrayLinker.setLength(0);

        latexBuilder.append("\\begin{document}\n");

        latexBuilder.append(service.latexHeader(
                code + " - " + name,
                semester,
                credits,
                lecturesPerWeek,
                practicalPerWeek,
                credits * 28,
                lecturersLinked,
                credentials));

        if (!introText.isEmpty()) {
            latexBuilder.append(service.latexIntroduction(escapeString(introText)));
        }

        latexBuilder.append("\\begin{learninggoals}");
        learningGoals.forEach(goal -> {
            if (goal.findValue("type").asText().equals("personal")) {
                latexBuilder.append(service.latexLearningGoal(
                        goal.findValue("name").asText(),
                        escapeString(goal.findValue("description").asText())
                ));
            }
        });

        final List<JsonNode> goalTypes = learningGoals.findValues("type");
        if (goalTypes.stream().filter(type -> type.asText().equals("group")).count() > 0) {
            latexBuilder.append("\\GroupGoals\n");
            learningGoals.forEach(goal -> {
                if (goal.findValue("type").asText().equals("group")) {
                    latexBuilder.append(service.latexLearningGoal(
                            goal.findValue("name").asText(),
                            escapeString(goal.findValue("description").asText())
                    ));
                }
            });
        }

        latexBuilder.append("\\end{learninggoals}\n");

        if (topics.size() != 0) {
            latexBuilder.append("\\begin{topics}\n");
            topics.forEach(topic -> {
                latexBuilder.append(service.latexTopic(escapeString(topic.asText())));
            });
            latexBuilder.append("\\end{topics}\n");
        }

        latexBuilder.append("\\begin{skills}\n");
        qualifications.forEach(qualification -> {
            final int layer = qualification.findValue("architectural_layer").asInt();
            final int activity = qualification.findValue("lifecycle_activity").asInt();
            final int level = qualification.findValue("level").asInt();

            if (activity == 5) {
                latexBuilder.append("\\ProBehaviour{").append(level).append("}\n");
            } else if (activity == 6) {
                latexBuilder.append("\\Research{").append(level).append("}\n");
            } else {
                latexBuilder.append("\\").append(ArchitecturalLayerMapper.mapInt(layer)).append("{\n");
                latexBuilder.append("\\").append(LifecycleActivityMapper.mapInt(activity)).append("{").append(level).append("}\n");
                latexBuilder.append("}\n");
            }
        });
        latexBuilder.append("\\end{skills}\n");

        latexBuilder.append("\\begin{exams}\n");
        assessmentParts.forEach(exam -> {
            latexBuilder.append(service.latexExam(
                    exam.findValue("subcode").asText(),
                    escapeString(exam.findValue("description").asText()),
                    exam.findValue("percentage").asDouble() * 100,
                    exam.findValue("minimal_grade").asDouble()
            ));
        });
        latexBuilder.append("\\end{exams}\n");

        if (teachingMaterials.size() != 0) {
            latexBuilder.append("\\begin{teachingmaterial}\n");
            teachingMaterials.forEach(material -> {
                latexBuilder.append(service.latexTeachingMaterial(escapeString(material.asText())));
            });
            latexBuilder.append("\\end{teachingmaterial}\n");
        }

        if (moduleLinks.size() != 0) {
            latexBuilder.append("\\begin{priorknowledge}\n");
            moduleLinks.forEach(link -> {
                latexBuilder.append(service.latexModuleLink(
                        link.findValue("code").asText(),
                        link.findValue("name").asText(),
                        link.findValue("type").asText()
                ));
            });
            latexBuilder.append("\\end{priorknowledge}\n");
        }

        if(!additionalInformation.isEmpty()){
            latexBuilder.append(service.latexAdditionalInformation(escapeString(additionalInformation)));
        }

        latexBuilder.append("\\end{document}\n");

        return Response.status(Response.Status.OK).entity(latexBuilder.toString())
                .build();
    }
}
