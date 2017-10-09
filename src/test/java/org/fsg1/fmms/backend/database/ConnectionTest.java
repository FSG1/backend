package org.fsg1.fmms.backend.database;

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.mock.jdbc.MockParameterMap;
import com.mockrunner.mock.jdbc.MockPreparedStatement;
import org.fsg1.fmms.backend.app.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionTest extends BasicJDBCTestCaseAdapter {

    @Mock
    private Configuration configMock;

    @Before
    public void setupJDBC(){
        getJDBCMockObjectFactory().registerMockDriver();

        Mockito.when(configMock.getDbString()).thenReturn("jdbc:postgresql://localhost:5432/fmms");
        Mockito.when(configMock.getDbUser()).thenReturn("fmms");
        Mockito.when(configMock.getDbPassword()).thenReturn("test123456");
    }

    @Test
    public void testExecuteQuery() throws SQLException {
        Connection conn = new Connection(configMock);

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
        Connection conn = new Connection(configMock);
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
