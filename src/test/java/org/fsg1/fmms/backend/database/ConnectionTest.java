package org.fsg1.fmms.backend.database;

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.mock.jdbc.MockParameterMap;
import com.mockrunner.mock.jdbc.MockPreparedStatement;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ConnectionTest extends BasicJDBCTestCaseAdapter {

    @Before
    public void setupJDBC(){
        getJDBCMockObjectFactory().registerMockDriver();
    }

    @Test
    public void testExecuteQuery() throws SQLException {
        Connection conn = new Connection();
        conn.executeQuery("param1: '?' , param2: '?'", "stringparam", 4);
        final List<MockPreparedStatement> preparedStatements = getJDBCMockObjectFactory().getMockConnection()
                .getPreparedStatementResultSetHandler().getPreparedStatements();
        assertEquals(preparedStatements.size(), 1);
        assertEquals(preparedStatements.get(0).getSQL(), "param1: '?' , param2: '?'");
        final MockParameterMap parameterMap = preparedStatements.get(0).getIndexedParameterMap();
        assertEquals(parameterMap.size(), 2);
        assertEquals(parameterMap.get(1), "stringparam");
        assertEquals(parameterMap.get(2), 4);
    }

    @Test
    public void testSetParameters() throws SQLException {
        Connection conn = new Connection();
        String query = "SELECT * FROM ?";
        Object[] params = new Object[]{"tablename", 2, 4, "fourth param"};
        conn.executeQuery(query, params);

        final List<MockPreparedStatement> preparedStatements = getJDBCMockObjectFactory().getMockConnection()
                .getPreparedStatementResultSetHandler().getPreparedStatements();
        assertEquals(preparedStatements.size(), 1);
        assertEquals(preparedStatements.get(0).getSQL(), query);

        final MockParameterMap parameterMap = preparedStatements.get(0).getIndexedParameterMap();
        assertEquals(parameterMap.size(), 1);
        assertEquals(parameterMap.get(1), "tablename");
    }
}
