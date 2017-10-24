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
public class CurriculumService extends Service {

    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    @Inject
    CurriculumService(final Connection connection) {
        super(connection);
    }

    /**
     * Get the query string that retrieves every semester in a curriculum.
     * @return Query string.
     */
    public String getQueryCurriculumSemestersString() {
        return
                "WITH " +
                        "modules AS (SELECT Array_to_json(Array_agg(Json_build_object('code', " +
                        "co.module_code, 'name', co.module_name, 'credits', co.credits))) AS json, " +
                        "study_programme_id AS sid, semester AS s FROM study.curriculum_overview AS co " +
                        "GROUP BY study_programme_id, semester), " +
                        "semesters AS ( " +
                        "      SELECT Json_build_object('semester', co2.semester, 'modules', " +
                        "(SELECT json FROM modules WHERE sid = co2.study_programme_id AND s = " +
                        "co2.semester)) AS json, co2.study_programme_id AS programme FROM " +
                        "study.curriculum_overview AS co2 GROUP BY co2.study_programme_id, " +
                        "co2.semester ORDER BY co2.semester) " +
                        " " +
                        "SELECT Json_build_object( " +
                        "  'semesters', (Array_to_json(Array_agg(json))) " +
                        ") AS semesters FROM semesters WHERE programme = ?;";
    }

    /**
     * {@inheritDoc}
     * Gets all semesters and their modules in a given curriculum.
     *
     * @param parameters The first parameter should be the identifier of the curriculum.
     * @return A JSON ObjectNode of the resulting JSON object.
     */
    @Override
    public JsonNode get(final String query, final Object... parameters) throws SQLException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        final ResultSet resultSet = getConn().executeQuery(getQueryCurriculumSemestersString(),
                parameters);
        resultSet.next();
        final String jsonString = resultSet.getString("semesters");
        return mapper.readTree(jsonString);
    }
}
