package org.fsg1.fmms.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fsg1.fmms.backend.database.Connection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CurriculumServiceTest {
    @Mock
    private Connection conn;
    @Mock
    private ResultSet mockResult;
    private CurriculumService service;

    @Before
    public void initMocks() throws SQLException {
        service = new CurriculumService(conn);
        when(conn.executeQuery(anyString(), any())).thenReturn(mockResult);
    }

    @Test
    public void testProcessMultipleSemesters() throws SQLException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.readTree(Files.readAllBytes(Paths
                .get("src/test/resources/json/semesterMultipleModules.json"))).toString();
        when(mockResult.getString(anyString())).thenReturn(jsonString);
        JsonNode node = service.get(service.getQueryCurriculumSemestersString(), 1);
        assertThat(jsonString, SameJSONAs.sameJSONAs(node.toString()));
        verify(conn, times(1)).executeQuery(service.getQueryCurriculumSemestersString(), 1);
    }

    @Test
    public void testProcessOneModule() throws IOException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.readTree(Files.readAllBytes(Paths
                .get("src/test/resources/json/semesterOneModule.json"))).toString();
        when(mockResult.getString(anyString())).thenReturn(jsonString);
        JsonNode node = service.get(service.getQueryCurriculumSemestersString(), 1);
        assertThat(jsonString, SameJSONAs.sameJSONAs(node.toString()));
        verify(conn, times(1)).executeQuery(service.getQueryCurriculumSemestersString(), 1);
    }

    @Test
    public void testProcessEmptySemesters() throws SQLException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.readTree(Files.readAllBytes(Paths
                .get("src/test/resources/json/semesterEmpty.json"))).toString();
        when(mockResult.getString(anyString())).thenReturn(jsonString);
        JsonNode node = service.get(service.getQueryCurriculumSemestersString(), 1);
        assertThat(jsonString, SameJSONAs.sameJSONAs(node.toString()));
        verify(conn, times(1)).executeQuery(service.getQueryCurriculumSemestersString(), 1);
    }
}
