package org.fsg1.fmms.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.fsg1.fmms.backend.database.Connection;

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
     * @param query      Query string to perform.
     * @param parameters Optional array of parameters to give to the query
     * @param columnName Name of the column of the result.
     * @return The result of the query in JSON format.
     * @throws Exception if the query was malformed, the connection broken or no entity was found.
     */
    public abstract JsonNode get(String query, String columnName, Object... parameters) throws Exception;
}
