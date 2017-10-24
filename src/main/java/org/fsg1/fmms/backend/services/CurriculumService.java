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
import java.util.HashMap;
import java.util.Map;

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

    /**
     * Builds a complex JSON Object from a JSON array.
     * First the algorithm loops through every module and for every unique semester, creates an
     * entry for it in the resulting object.
     * Then a second loop through every module matches the module to a semester.
     * @param jsonString The JSON array in String form.
     * @return A JSON Object which contains every semester and its modules in hierarchical format.
     * @throws IOException If the JSON String is malformed.
     */
    private ObjectNode buildCurriculumSemesters(final String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultObject = mapper.createObjectNode();
        ArrayNode semestersArray = mapper.createArrayNode();
        resultObject.set("semesters", semestersArray);

        ArrayNode arrayOfModules = (ArrayNode) mapper.readTree(jsonString);
        if (arrayOfModules.size() == 0) return resultObject;

        //The capacity is 12 to prevent the HashMap from growing. There are only 8 semesters so a slightly
        //larger number is chosen.
        Map<Integer, ArrayNode> seenSemesters = new HashMap<>(12);

        for (JsonNode module : arrayOfModules) {
            int semester = module.get("semester").asInt();
            if (seenSemesters.containsKey(semester)) continue;

            ObjectNode currentSemester = mapper.createObjectNode();
            currentSemester.put("semester", semester);

            ArrayNode currentSemesterModules = mapper.createArrayNode();
            currentSemester.set("modules", currentSemesterModules);

            semestersArray.add(currentSemester);

            seenSemesters.put(semester, currentSemesterModules);
        }

        for (JsonNode module : arrayOfModules) {
            int moduleSemester = module.get("semester").asInt();
            cleanModuleNode((ObjectNode) module);

            final ArrayNode modules = seenSemesters.get(moduleSemester);
            modules.add(module);
        }

        return resultObject;
    }

    private void cleanModuleNode(final ObjectNode currentModule) {
        currentModule.remove("semester");
        currentModule.remove("name");
        currentModule.remove("study_programme");
    }
}
