package database;

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
     */
    Connection() {
        Properties props = new Properties();
        props.setProperty("user", "fmms");
        props.setProperty("password", "test123456");
        try {
            String url = "jdbc:postgresql://localhost:5432/fmms";
            conn = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute any query on the database using prepared statements.
     * @param query The SQL String of the query you want to perform.
     * @param parameters An optional array of Objects from which to fill the parameters.
     * @return A ResultSet of the query results.
     * @throws SQLException if something goes wrong performing the query.
     */
    public ResultSet executeQuery(final String query, final Object... parameters) throws SQLException {
        final PreparedStatement preparedStatement = conn.prepareStatement(query);
        mapParams(preparedStatement, parameters);
        return preparedStatement.executeQuery();
    }

    private void mapParams(final PreparedStatement ps, final Object... args) throws SQLException {
        int i = 1;
        for (Object arg : args) {
            if (arg instanceof Integer) {
                ps.setInt(i++, (Integer) arg);
            } else {
                ps.setString(i++, (String) arg);
            }
        }
    }
}

