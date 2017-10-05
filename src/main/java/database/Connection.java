package database;

import resources.Configuration;

import javax.inject.Inject;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Connection {
    private java.sql.Connection conn = null;

    private final Configuration config;

    @Inject
    public Connection(Configuration config) {
        this.config = config;

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

    public ResultSet executeQuery(String query, Object... parameters) throws SQLException {
        final PreparedStatement preparedStatement = conn.prepareStatement(query);
        mapParams(preparedStatement, parameters);
        return preparedStatement.executeQuery();
    }

    private void mapParams(PreparedStatement ps, Object... args) throws SQLException {
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

