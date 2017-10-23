package org.fsg1.fmms.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.fsg1.fmms.backend.database.Connection;

import java.io.IOException;
import java.sql.SQLException;

/**
 * An abstract class representing a Service to be used by the REST API.
 * A service contains
 */
public abstract class Service {
    private final Connection conn;

    final Connection getConn() {
        return conn;
    }

    private String queryString;

    final void setQueryString(final String query) {
        queryString = query;
    }

    final String getQueryString() {
        return queryString;
    }

    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    Service(final Connection connection) {
        conn = connection;
    }

    /**
     * Execute the query on the database with any parameters.
     * @param parameters Optional array of parameters to give to the query
     * @return The result of the query in JSON format.
     * @throws SQLException if the query was malformed.
     * @throws IOException if the database connection was broken.
     */
    public abstract JsonNode execute(Object... parameters) throws SQLException, IOException;
}
