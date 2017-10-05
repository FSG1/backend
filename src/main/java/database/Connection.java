package database;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Connection {
    private java.sql.Connection conn = null;

    public Connection() {
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

