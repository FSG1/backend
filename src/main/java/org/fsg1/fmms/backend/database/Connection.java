package org.fsg1.fmms.backend.database;

import org.fsg1.fmms.backend.app.Configuration;

import javax.inject.Inject;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The class used for connecting with the Database. It uses the JDBC Driver.
 */
public final class Connection {
    private java.sql.Connection conn = null;

    /**
     * The constructor. It immediately connects to the database.
     *
     * @param config Active server configuration
     */
    @Inject
    public Connection(final Configuration config) {
        Properties props = new Properties();
        props.setProperty("user", config.getDbUser());
        props.setProperty("password", config.getDbPassword());

        try {
            String url = config.getDbString();
            conn = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute any query on the database using a <code>PreparedStatement</code>.
     *
     * @param query      The SQL String of the query you want to perform.
     * @param parameters An optional array of Objects from which to fill the parameters.
     * @return A ResultSet of the query results.
     * @throws SQLException if something goes wrong performing the query.
     */
    public ResultSet executeQuery(final String query, final Object... parameters) throws SQLException {
        final PreparedStatement preparedStatement = conn.prepareStatement(query);
        mapParams(preparedStatement, parameters);
        return preparedStatement.executeQuery();
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

