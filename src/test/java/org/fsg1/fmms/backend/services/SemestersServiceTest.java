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
import java.sql.SQLException;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SemestersServiceTest {
    @Mock
    private Connection conn;
    private SemestersService service;

    @Before
    public void initMocks() throws SQLException {
        service = new SemestersService(conn);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testProcessEmptySemester() throws Exception {
        when(conn.executeQuery("semesters", service.getQueryCurriculumSemestersString(), 1)).thenThrow(new EntityNotFoundException());
        service.get(service.getQueryCurriculumSemestersString(), "semesters", 1);
        verify(conn, times(1)).executeQuery("semesters", service.getQueryCurriculumSemestersString(), 1);
    }

    @Test
    public void testProcessMultipleSemesters() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.readTree(Files.readAllBytes(Paths
                .get("src/test/resources/json/semesterMultipleModules.json"))).toString();

        when(conn.executeQuery("semesters", service.getQueryCurriculumSemestersString(), 1)).thenReturn(jsonString);

        final JsonNode node = service.get(service.getQueryCurriculumSemestersString(), "semesters", 1);
        assertThat(jsonString, SameJSONAs.sameJSONAs(node.toString()));
        verify(conn, times(1)).executeQuery("semesters", service.getQueryCurriculumSemestersString(), 1);
    }

    @Test
    public void testProcessCompleteSemester() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.readTree(Files.readAllBytes(Paths
                .get("src/test/resources/json/completeSemester.json"))).toString();

        when(conn.executeQuery("complete_semester", service.getQueryCompleteSemester(), 1, 1)).thenReturn(jsonString);

        final JsonNode node = service.get(service.getQueryCompleteSemester(), "complete_semester", 1, 1);
        assertThat(jsonString, SameJSONAs.sameJSONAs(node.toString()));
        verify(conn, times(1)).executeQuery("complete_semester", service.getQueryCompleteSemester(), 1, 1);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testProcessEmptyCompleteSemester() throws Exception {
        when(conn.executeQuery("complete_semester", service.getQueryCompleteSemester(), 1, 1)).thenThrow(new EntityNotFoundException());
        service.get(service.getQueryCompleteSemester(), "complete_semester", 1, 1);
        verify(conn, times(1)).executeQuery("complete_semester", service.getQueryCompleteSemester(), 1, 1);
    }
}
