package org.fsg1.fmms.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fsg1.fmms.backend.database.Connection;
import org.fsg1.fmms.backend.exceptions.EntityNotFoundException;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModuleService extends Service {

    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    @Inject
    ModuleService(final Connection connection) {
        super(connection);
        setQueryString(
                "WITH " +
                        "    skills AS (SELECT Array_to_json(Array_agg(Json_build_object('al', q.architecturallayer_id, 'ac', q.activity_id, 'level', los.LEVEL))) AS json, lq.learninggoal_id FROM study.learninggoal_qualification AS lq inner join study.qualification AS q ON q.id = lq.qualification_id inner join study.levelofskill AS los ON los.id = q.levelofskill_id GROUP BY lq.learninggoal_id), " +
                        "    lg AS (SELECT Array_to_json(Array_agg(Json_build_object('name', Concat('LG ', sequenceno), 'description', description, 'groupgoal', lg.groupgoal, 'skills', (SELECT json FROM skills WHERE skills.learninggoal_id = lg.id)))) AS json, lg.module_id FROM study.learninggoal AS lg GROUP BY module_id), " +
                        "    activities AS (SELECT Array_to_json(Array_agg(Json_build_object('id', id, 'name', name, 'description', description))) AS json FROM study.activity), " +
                        "    als AS (SELECT Array_to_json(Array_agg(Json_build_object('id', id, 'name', name, 'description', description))) AS json FROM study.architecturallayer) " +
                        "SELECT Json_build_object( " +
                        "  'module_code', m.code, " +
                        "  'module_name', m.name, " +
                        "  'credits', m.credits, " +
                        "  'lifecycle_activities', (SELECT json FROM activities), " +
                        "  'architectural_layers', (SELECT json FROM als), " +
                        "  'learning_goals', (SELECT json FROM lg WHERE lg.module_id = m.id) " +
                        ") as module FROM study.MODULE AS m " +
                        "WHERE m.code = ?;"
        );
    }

    /**
     * {@inheritDoc}
     * Gets the information of one module.
     *
     * @param parameters The first parameter should be the identifier of the module.
     * @return A JSON ObjectNode of the resulting JSON object.
     */
    @Override
    public JsonNode execute(Object... parameters) throws SQLException, IOException, EntityNotFoundException {
        ObjectMapper mapper = new ObjectMapper();
        final ResultSet resultSet = getConn().executeQuery(getQueryString(), parameters[0]);
        resultSet.next();
        final String jsonString = resultSet.getString("module");

        if(jsonString == null) throw new EntityNotFoundException();

        return mapper.readTree(jsonString);
    }
}
