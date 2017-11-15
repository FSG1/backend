package org.fsg1.fmms.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fsg1.fmms.backend.database.Connection;
import org.fsg1.fmms.backend.exceptions.EntityNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CurriculaServiceTest {
    @Mock
    private Connection conn;

    private CurriculaService service;

    @Before
    public void initMocks() throws SQLException {
        service = new CurriculaService(conn);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testProcessEmptySemesters() throws Exception {
        when(conn.executeQuery(anyString(), anyString(), any())).thenThrow(new EntityNotFoundException());
        service.get(service.getQueryCurriculaString(), "curricula");
        verify(conn, times(1)).executeQuery("curricula", service.getQueryCurriculaString());
    }

    @Test
    public void testProcessCurricula() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.readTree(Files.readAllBytes(Paths
                .get("src/test/resources/json/curricula.json"))).toString();

        when(conn.executeQuery("curricula", service.getQueryCurriculaString())).thenReturn(jsonString);

        final JsonNode node = service.get(service.getQueryCurriculaString(), "curricula");
        assertThat(jsonString, SameJSONAs.sameJSONAs(node.toString()));
        verify(conn, times(1)).executeQuery("curricula", service.getQueryCurriculaString());
    }
}
