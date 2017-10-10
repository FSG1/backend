package org.fsg1.fmms.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.fsg1.fmms.backend.database.Connection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class CurriculaServiceTest {
    @Mock
    private Connection conn;
    @Mock
    private ResultSet mockResult;

    private CurriculaService service;

    @Before
    public void initMocks() throws SQLException {
        MockitoAnnotations.initMocks(this);
        service = new CurriculaService(conn);
        when(conn.executeQuery(anyString(), any())).thenReturn(mockResult);
    }

    @Test
    public void testProcessCurricula() throws SQLException, IOException {
        when(mockResult.getString("curricula")).thenReturn(
                "[{\"id\":1,\"code\":\"SE\",\"name\":\"Software Engineering\"}," +
                        "{\"id\":2,\"code\":\"BI\",\"name\":\"Business Informatics\"}]"
        );
        JsonNode result = service.getCurricula();
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).get("id").asInt(), 1);
        assertEquals(result.get(0).get("code").asText(), "SE");
        assertEquals(result.get(0).get("name").asText(), "Software Engineering");
        assertEquals(result.get(1).get("id").asInt(), 2);
        assertEquals(result.get(1).get("code").asText(), "BI");
        assertEquals(result.get(1).get("name").asText(), "Business Informatics");

        verify(conn, times(1)).executeQuery(service.getQueryCurricula());
    }

    @Test
    public void testProcessEmptySemesters() throws SQLException, IOException {
        when(mockResult.getString(anyString())).thenReturn("[]");
        JsonNode result = service.getCurricula();
        assertEquals(result.size(), 0);
        verify(conn, times(1)).executeQuery(service.getQueryCurricula());
    }
}
