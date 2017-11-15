package org.fsg1.fmms.backend.database;

import org.apache.commons.dbcp2.BasicDataSource;
import org.fsg1.fmms.backend.app.Configuration;
import org.fsg1.fmms.backend.exceptions.EntityNotFoundException;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The class used for connecting with the Database. It uses the JDBC Driver.
 */
public final class Connection {
    private BasicDataSource connectionPool;

    /**
     * The constructor. It immediately connects to the database. Uses a connection pool with an
     * initial size of 2.
     *
     * @param config Active server configuration.
     * @param connectionPool The connection pool to obtain Connections from.
     * @throws SQLException if the database connection closed or the query was malformed.
     */
    @Inject
    public Connection(final Configuration config, final BasicDataSource connectionPool) throws SQLException {
        this.connectionPool = connectionPool;
        this.connectionPool.setUsername(config.getDbUser());
        this.connectionPool.setPassword(config.getDbPassword());
        this.connectionPool.setUrl(config.getDbString());
        this.connectionPool.setDriverClassName("org.postgresql.Driver");
        this.connectionPool.setInitialSize(2);
    }

    /**
     * Execute any query on the database using a <code>PreparedStatement</code>.
     *
     * @param columnName The name of the column that is returned by the query.
     * @param query      The SQL String of the query you want to perform.
     * @param parameters An optional array of Objects from which to fill the parameters.
     * @return A ResultSet of the query results.
     * @throws Exception if something goes wrong performing the query.
     */
    public String executeQuery(final String columnName, final String query, final Object... parameters) throws Exception {
        try (java.sql.Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                mapParams(preparedStatement, parameters);

                try (ResultSet result = preparedStatement.executeQuery()) {
                    if (!result.next()) throw new EntityNotFoundException();
                    return result.getString(columnName);
                }
            }
        }
    }

    /**
     * Maps parameters to a PreparedStatement.
     * Any objects given in the `args` array will be mapped sequentially to any question mark in the
     * <code>PreparedStatement</code>. For example, a <code>PreparedStatement</code>
     * with the query `SELECT * from ?` will have one open parameter to be mapped and the
     * first object given will be mapped to that position. Any excess parameters will not be mapped.
     *
     * @param ps   PreparedStatement to map parameters to.
     * @param args Array of Integers or Strings that represent the parameters.
     * @throws SQLException if a database access error occurs or
     *                      this method is called on a closed <code>PreparedStatement</code>.
     */
    private void mapParams(final PreparedStatement ps, final Object... args) throws SQLException {
        final int parameterCount = ps.getParameterMetaData().getParameterCount();
        int i = 1;
        for (Object arg : args) {
            if (i > parameterCount) return;
            if (arg instanceof Integer) {
                ps.setInt(i++, (Integer) arg);
            } else {
                ps.setString(i++, (String) arg);
            }
        }
    }
}

