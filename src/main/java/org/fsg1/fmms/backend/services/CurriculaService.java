package org.fsg1.fmms.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fsg1.fmms.backend.database.Connection;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The service class for the curricula endpoint.
 */
public class CurriculaService extends Service {

    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    @Inject
    CurriculaService(final Connection connection) {
        super(connection);
    }

    /**
     * Get the query string that retrieves every curriculum.
     * @return Query string.
     */
    public String getQueryCurriculaString() {
        return "SELECT array_to_json(array_agg(row_to_json(sp))) as curricula \" " +
                "+ \"FROM study.studyprogramme sp";
    }

    /**
     * {@inheritDoc}
     * Gets the ids, names and codes of all curricula.
     *
     * @return A JSON ObjectNode representing an array of curricula.
     */
    public JsonNode get(final String query, final Object... parameters) throws SQLException, IOException {
        final ResultSet resultSet = getConn().executeQuery(query, parameters);
        resultSet.next();
        final String jsonString = resultSet.getString("curricula");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(jsonString);
    }
}
