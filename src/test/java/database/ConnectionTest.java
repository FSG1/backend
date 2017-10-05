package database;

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.mock.jdbc.MockParameterMap;
import com.mockrunner.mock.jdbc.MockPreparedStatement;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ConnectionTest extends BasicJDBCTestCaseAdapter {
    @Test
    public void testExecuteQuery() throws SQLException {
        getJDBCMockObjectFactory().registerMockDriver();
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
}
