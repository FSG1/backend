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

    @Test(expected = EntityNotFoundException.class)
    public void testProcessEmptySemester() throws Exception {
        service.get(service.getQueryCurriculumSemestersString(), "semesters", 1);
    }

    @Test
    public void testProcessMultipleSemesters() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.readTree(Files.readAllBytes(Paths
                .get("src/test/resources/json/semesterMultipleModules.json"))).toString();

        when(mockResult.next()).thenReturn(true);
        when(mockResult.getString(anyString())).thenReturn(jsonString);

        final JsonNode node = service.get(service.getQueryCurriculumSemestersString(), "semesters", 1);
        assertThat(jsonString, SameJSONAs.sameJSONAs(node.toString()));
        verify(conn, times(1)).executeQuery(service.getQueryCurriculumSemestersString(), 1);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testProcessEmptyModule() throws Exception {
        service.get(service.getQueryModuleInformation(), "module", 1, "1");
    }

    @Test
    public void testProcessModule() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.readTree(Files.readAllBytes(Paths
                .get("src/test/resources/json/module.json"))).toString();

        when(mockResult.next()).thenReturn(true);
        when(mockResult.getString(anyString())).thenReturn(jsonString);

        final JsonNode node = service.get(service.getQueryModuleInformation(), "module", 1);
        assertThat(jsonString, SameJSONAs.sameJSONAs(node.toString()));
    }

    @Test
    public void testProcessCompleteSemester() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.readTree(Files.readAllBytes(Paths
                .get("src/test/resources/json/completeSemester.json"))).toString();

        when(mockResult.next()).thenReturn(true);
        when(mockResult.getString(anyString())).thenReturn(jsonString);

        final JsonNode node = service.get(service.getQueryCompleteSemester(), "complete_semester", 1, 1);
        assertThat(jsonString, SameJSONAs.sameJSONAs(node.toString()));

    }

    @Test(expected = EntityNotFoundException.class)
    public void testProcessEmptyCompleteSemester() throws Exception {
        service.get(service.getQueryCompleteSemester(), "complete_semester", 1, 1);

    }
}
