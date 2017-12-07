package org.fsg1.fmms.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fsg1.fmms.backend.database.Connection;

/**
 * An abstract class representing a Service to be used by the REST API.
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

    private Connection getConn() {
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
    public JsonNode get(final String query, final String columnName, final Object... parameters) throws Exception {
        final String jsonString = getConn().executeQuery(columnName, query, parameters);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(jsonString);
    }

    /**
     * Execute an update or insert statement on the database with the given connection and parameters.
     * This connection will remain uncommitted and unclosed until the commitTransaction() method is called.
     *
     * @param connection Connection to use.
     * @param statement  Statements to perform.
     * @param parameters Array of parameters to give to the query.
     * @return The generated INSERT id if an INSERT was made, or 0 if no INSERT was made but, for instance,
     * a DELETE or UPDATE.
     * @throws Exception if a database access error occurs.
     */
    public long update(final java.sql.Connection connection, final String statement, final Object... parameters) throws Exception {
        return getConn().executeUpdate(connection, statement, parameters);
    }

    /**
     * Executes a TransactionRunner on a Connection.
     *
     * @param transaction Function to run.
     * @throws Exception If a database access error occurs or any other thing goes wrong.
     */
    public void executeTransactional(final TransactionRunner transaction) throws Exception {
        getConn().executeTransactional(transaction);
    }
}
