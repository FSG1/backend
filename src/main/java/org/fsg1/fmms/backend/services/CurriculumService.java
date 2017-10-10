package org.fsg1.fmms.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.fsg1.fmms.backend.database.Connection;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * The service class for the curriculum endpoint.
 */
public class CurriculumService {

    private final Connection conn;

    private final String queryCurriculumSemesters =
            "SELECT coalesce(array_to_json(array_agg(row_to_json(co))), '[]' :: JSON) AS semesters "
                    + "FROM study.curriculum_overview co "
                    + "WHERE co.study_programme_id = ?";

    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    @Inject
    public CurriculumService(final Connection connection) {
        conn = connection;
    }

    final String getQueryCurriculumSemesters() {
        return queryCurriculumSemesters;
    }

    /**
     * Gets all semesters and their modules in a given curriculum.
     *
     * @param curriculumId The identifier of the curriculum.
     * @return A JSON ObjectNode of the resulting JSON object.
     * @throws SQLException If something goes wrong.
     * @throws IOException  If something goes wrong.
     */
    public ObjectNode getCurriculumSemesters(final int curriculumId) throws SQLException, IOException {
        final ResultSet resultSet = conn.executeQuery(queryCurriculumSemesters, curriculumId);
        resultSet.next();
        final String jsonString = resultSet.getString("semesters");

        return buildCurriculumSemesters(jsonString);
    }

    private ObjectNode buildCurriculumSemesters(final String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultObject = mapper.createObjectNode();
        ArrayNode semestersArray = mapper.createArrayNode();
        resultObject.set("semesters", semestersArray);

        ArrayNode arrayOfModules = (ArrayNode) mapper.readTree(jsonString);
        if (arrayOfModules.size() == 0) return resultObject;

        Set<Integer> seenSemesters = new HashSet<>();

        for (JsonNode module : arrayOfModules) {
            int semester = module.get("semester").asInt();
            if (seenSemesters.contains(semester)) continue;

            seenSemesters.add(semester);

            ObjectNode currentSemester = mapper.createObjectNode();
            currentSemester.put("semester", semester);

            ArrayNode currentSemesterModules = mapper.createArrayNode();
            currentSemester.set("modules", currentSemesterModules);

            semestersArray.add(currentSemester);
        }

        for (JsonNode module : arrayOfModules) {
            int moduleSemester = module.get("semester").asInt();
            cleanModuleNode((ObjectNode) module);

            for (JsonNode semester : semestersArray) {
                final JsonNode modules = semester.get("modules");
                final int selectedSemester = semester.get("semester").asInt();
                if (selectedSemester == moduleSemester) {
                    ((ArrayNode) modules).add(module);
                    break;
                }
            }
        }

        return resultObject;
    }

    private void cleanModuleNode(final ObjectNode currentModule) {
        currentModule.remove("semester");
        currentModule.remove("name");
        currentModule.remove("study_programme");
    }
}
