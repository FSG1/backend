package org.fsg1.fmms.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fsg1.fmms.backend.database.Connection;
import org.fsg1.fmms.backend.exceptions.EntityNotFoundException;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The service class for the modules endpoint.
 */
public class ModuleService extends Service {

    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    @Inject
    ModuleService(final Connection connection) {
        super(connection);
    }

    /**
     * Get the query string that retrieves the information of a module.
     * @return Query string.
     */
    public String getQueryModuleInformation() {
        return
                "WITH " +
                        "alrow AS (SELECT Row_number() OVER () AS num, id FROM study.architecturallayer), " +
                        "acrow AS (SELECT Row_number() OVER () AS num, id FROM study.activity), " +
                        "skills AS (SELECT Array_to_json(Array_agg(Json_build_object('architectural_layer', (SELECT (num - 1) FROM alrow WHERE alrow.id = q.architecturallayer_id), 'lifecycle_activity', (SELECT (num - 1) FROM acrow WHERE acrow.id = q.activity_id), 'level', los.LEVEL))) AS json, lq.learninggoal_id FROM study.learninggoal_qualification AS lq inner join study.qualification AS q ON q.id = lq.qualification_id INNER JOIN study.levelofskill AS los ON los.id = q.levelofskill_id GROUP BY lq.learninggoal_id), " +
                        "lg AS (SELECT Array_to_json(Array_agg(Json_build_object('name', Concat('LG ', sequenceno), 'description', description, 'type', (CASE lg.groupgoal WHEN TRUE THEN 'group' ELSE 'personal' END), 'skillmatrix', Coalesce((SELECT json FROM skills WHERE skills.learninggoal_id = lg.id), '[]'::json)))) AS json, lg.module_id FROM study.learninggoal AS lg GROUP BY module_id), " +
                        "acitivies AS (SELECT Array_to_json(Array_agg(Json_build_object('lifecycle_activity_id', id, 'lifecycle_activity_name', name, 'lifecycle_activity_description', description))) AS json FROM study.activity), " +
                        "als AS (SELECT Array_to_json(Array_agg(Json_build_object('architectural_layer_id', id, 'architectural_layer_name', name, 'architectural_layer_description', description))) AS json FROM study.architecturallayer) " +
                        "SELECT Json_build_object( " +
                        "'module_code', m.code, " +
                        "'module_name', m.name, " +
                        "'credits', m.credits, " +
                        "'lifecycle_activities', (SELECT json FROM acitivies), " +
                        "'architectural_layers', (SELECT json FROM als), " +
                        "'learning_goals', (SELECT json FROM lg WHERE lg.module_id = m.id) " +
                        ") AS module FROM study.MODULE AS m " +
                        "WHERE m.code = ?";
    }

    /**
     * {@inheritDoc}
     * Gets the information of one module.
     *
     * @param parameters The first parameter should be the identifier of the module.
     * @return A JSON ObjectNode of the resulting JSON object.
     */
    @Override
    public JsonNode get(final String query, final Object... parameters) throws SQLException, IOException,
            EntityNotFoundException {
        ObjectMapper mapper = new ObjectMapper();
        final ResultSet resultSet = getConn().executeQuery(query, parameters);
        resultSet.next();
        final String jsonString = resultSet.getString("module");

        if (jsonString == null) throw new EntityNotFoundException();

        return mapper.readTree(jsonString);
    }
}
