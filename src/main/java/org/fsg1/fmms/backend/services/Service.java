package org.fsg1.fmms.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.fsg1.fmms.backend.database.Connection;
import org.fsg1.fmms.backend.exceptions.EntityNotFoundException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * An abstract class representing a Service to be used by the REST API.
 * A service contains
 */
public abstract class Service {
    private final Connection conn;

    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    Service(final Connection connection) {
        conn = connection;
    }

    final Connection getConn() {
        return conn;
    }

    /**
     * Execute a retrieval query on the database with any parameters.
     *
     * @param query Query string to perform.
     * @param parameters Optional array of parameters to give to the query
     * @return The result of the query in JSON format.
     * @throws SQLException if the query was malformed.
     * @throws IOException if the database connection was broken.*
     * @throws EntityNotFoundException if no entity was found by the query.
     */
    public abstract JsonNode get(String query, Object... parameters) throws SQLException, IOException,
            EntityNotFoundException;
}
