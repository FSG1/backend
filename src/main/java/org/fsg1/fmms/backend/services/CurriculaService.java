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
public class CurriculaService {

    private final Connection conn;

    private final String queryCurricula =
            "SELECT array_to_json(array_agg(row_to_json(sp))) as curricula "
                    + "FROM  study.studyprogramme sp";

    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    @Inject
    public CurriculaService(final Connection connection) {
        conn = connection;
    }

    final String getQueryCurricula() {
        return queryCurricula;
    }

    /**
     * Gets the ids, names and codes of all curricula.
     *
     * @return A JSON ObjectNode representing an array of curricula.
     * @throws SQLException If something goes wrong.
     * @throws IOException  If something goes wrong.
     */
    public JsonNode getCurricula() throws SQLException, IOException {
        final ResultSet resultSet = conn.executeQuery(queryCurricula);
        resultSet.next();
        final String jsonString = resultSet.getString("curricula");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(jsonString);
    }
}
