package org.fsg1.fmms.backend.database;

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.mock.jdbc.MockParameterMap;
import com.mockrunner.mock.jdbc.MockPreparedStatement;
import org.apache.commons.dbcp2.BasicDataSource;
import org.fsg1.fmms.backend.app.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionTest extends BasicJDBCTestCaseAdapter {

    @Mock
    private Configuration configMock;
    @Mock
    private BasicDataSource bds;

    @Before
    public void setupJDBC() throws SQLException {
        when(bds.getConnection()).thenReturn(getJDBCMockObjectFactory().getMockConnection());
        getJDBCMockObjectFactory().registerMockDriver();

        when(configMock.getDbString()).thenReturn("jdbc:postgresql://localhost:5432/fmms");
        when(configMock.getDbUser()).thenReturn("fmms");
        when(configMock.getDbPassword()).thenReturn("test123456");
    }

    @Test
    public void testExecuteQuery() throws Exception {
        Connection conn = new Connection(configMock, bds);

        conn.executeQuery("", "param1: '?' , param2: '?'", "stringparam", 4);
        final List<MockPreparedStatement> preparedStatements = getJDBCMockObjectFactory().getMockConnection()
                .getPreparedStatementResultSetHandler().getPreparedStatements();
        assertEquals(preparedStatements.size(), 1);
        assertEquals(preparedStatements.get(0).getSQL(), "param1: '?' , param2: '?'");
        final MockParameterMap parameterMap = preparedStatements.get(0).getIndexedParameterMap();
        assertEquals(parameterMap.size(), 2);
        assertEquals(parameterMap.get(1), "stringparam");
        assertEquals(parameterMap.get(2), 4);
        verifyConnectionClosed();
    }

    @Test
    public void testSetParameters() throws Exception {
        Connection conn = new Connection(configMock, bds);
        String query = "SELECT * FROM ?";
        Object[] params = new Object[]{"tablename", 2, 4, "fourth param"};

        conn.executeQuery("", query, params);
        final List<MockPreparedStatement> preparedStatements = getJDBCMockObjectFactory().getMockConnection()
                .getPreparedStatementResultSetHandler().getPreparedStatements();
        assertEquals(preparedStatements.size(), 1);
        assertEquals(preparedStatements.get(0).getSQL(), query);

        final MockParameterMap parameterMap = preparedStatements.get(0).getIndexedParameterMap();
        assertEquals(parameterMap.size(), 1);
        assertEquals(parameterMap.get(1), "tablename");
        verifyConnectionClosed();
    }

    @Test
    public void testTransaction() throws Exception {
        Connection conn = new Connection(configMock, bds);
        final java.sql.Connection connection = conn.startTransaction();
        String query = "SELECT * FROM ?";
        Object[] params = new Object[]{"tablename", 2, 4, "fourth param"};
        conn.executeQuery(connection, "", query, params);
        final List<MockPreparedStatement> preparedStatements = getJDBCMockObjectFactory().getMockConnection()
                .getPreparedStatementResultSetHandler().getPreparedStatements();
        assertEquals(preparedStatements.size(), 1);
        assertEquals(preparedStatements.get(0).getSQL(), query);

        final MockParameterMap parameterMap = preparedStatements.get(0).getIndexedParameterMap();
        assertEquals(parameterMap.size(), 1);
        assertEquals(parameterMap.get(1), "tablename");

        assertTrue(connection.isClosed());
        verifyConnectionClosed();
    }
}
