package org.fsg1.fmms.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.fsg1.fmms.backend.database.Connection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class CurriculumServiceTest {
    final String query = "SELECT coalesce(array_to_json(array_agg(row_to_json(co))), '[]'::json) as semesters\n" +
            "FROM study.curriculum_overview co\n" +
            "WHERE study_programme = ?;";
    @Mock
    private Connection conn;
    @Mock
    private ResultSet mockResult;
    private CurriculumService service;

    @Before
    public void initMocks() throws SQLException {
        MockitoAnnotations.initMocks(this);
        service = new CurriculumService(conn);
        when(conn.executeQuery(anyString(), any())).thenReturn(mockResult);
    }

    @Test
    public void testProcessMultipleModules() throws SQLException, IOException {
        when(mockResult.getString(anyString())).thenReturn(
                "[{\"name\":\"2014_NEW\",\"study_programme\":\"SE\",\"semester\":1,\"module_code\":\"BUA1\",\"module_name\":\"Business Administration 1\",\"credits\":4},{\"name\":\"2014_NEW\",\"study_programme\":\"SE\",\"semester\":1,\"module_code\":\"JAVA1\",\"module_name\":\"Programming in Java 1\",\"credits\":5}]"
        );
        ObjectNode result = service.getCurriculumSemesters("SE");
        final JsonNode semesters = result.findValue("semesters");
        assertEquals(semesters.size(), 1);

        final ArrayNode modules = (ArrayNode) semesters.findValue("modules");
        assertEquals(modules.size(), 2);
        final JsonNode expectedBUA = modules.get(0);
        verifyModuleStructure(expectedBUA);
        assertEquals(expectedBUA.get("module_code").asText(), "BUA1");
        assertEquals(expectedBUA.get("module_name").asText(), "Business Administration 1");
        assertEquals(expectedBUA.get("credits").asInt(), 4);

        final JsonNode expectedJAVA = modules.get(1);
        verifyModuleStructure(expectedJAVA);
        assertEquals(expectedJAVA.get("module_code").asText(), "JAVA1");
        assertEquals(expectedJAVA.get("module_name").asText(), "Programming in Java 1");
        assertEquals(expectedJAVA.get("credits").asInt(), 5);

        verify(conn, times(1)).executeQuery(query, "SE");
    }

    @Test
    public void testProcessOneModule() throws IOException, SQLException {
        when(mockResult.getString(anyString())).thenReturn("[{\"name\":\"2014_NEW\",\"study_programme\":\"SE\",\"semester\":1,\"module_code\":\"BUA1\",\"module_name\":\"Business Administration 1\",\"credits\":4}]");
        ObjectNode result = service.getCurriculumSemesters("SE");
        final JsonNode semesters = result.findValue("semesters");
        assertEquals(semesters.size(), 1);

        final ArrayNode modules = (ArrayNode) semesters.findValue("modules");
        assertEquals(modules.size(), 1);
        final JsonNode expectedBUA = modules.get(0);
        verifyModuleStructure(expectedBUA);
        assertEquals(expectedBUA.get("module_code").asText(), "BUA1");
        assertEquals(expectedBUA.get("module_name").asText(), "Business Administration 1");
        assertEquals(expectedBUA.get("credits").asInt(), 4);

        verify(conn, times(1)).executeQuery(query, "SE");
    }

    @Test
    public void testProcessMultipleSemesters() throws SQLException, IOException {
        when(mockResult.getString(anyString())).thenReturn(
                "[{\"name\":\"2014_NEW\",\"study_programme\":\"SE\",\"semester\":1,\"module_code\":\"BUA1\",\"module_name\":\"Business Administration 1\",\"credits\":4}," +
                        "{\"name\":\"2014_NEW\",\"study_programme\":\"SE\",\"semester\":2,\"module_code\":\"JAVA2\",\"module_name\":\"Programming in Java 2\",\"credits\":5}," +
                        "{\"name\":\"2014_NEW\",\"study_programme\":\"SE\",\"semester\":3,\"module_code\":\"MOD2\",\"module_name\":\"Modeling 2\",\"credits\":5}," +
                        "{\"name\":\"2014_NEW\",\"study_programme\":\"SE\",\"semester\":4,\"module_code\":\"JAVA3\",\"module_name\":\"Java Concurrency\",\"credits\":5}," +
                        "{\"name\":\"2014_NEW\",\"study_programme\":\"SE\",\"semester\":5,\"module_code\":\"STG1\",\"module_name\":\"Internship\",\"credits\":30}," +
                        "{\"name\":\"2014_NEW\",\"study_programme\":\"SE\",\"semester\":6,\"module_code\":\"MINOR\",\"module_name\":\"Minor\",\"credits\":30}," +
                        "{\"name\":\"2014_NEW\",\"study_programme\":\"SE\",\"semester\":7,\"module_code\":\"COM7\",\"module_name\":\"Communication\",\"credits\":2}," +
                        "{\"name\":\"2014_NEW\",\"study_programme\":\"SE\",\"semester\":8,\"module_code\":\"STG2\",\"module_name\":\"Graduation Project\",\"credits\":30}]"

        );
        ObjectNode result = service.getCurriculumSemesters("SE");
        final ArrayNode semesters = (ArrayNode) result.findValue("semesters");
        assertEquals(semesters.size(), 8);
        for (int i = 0; i < semesters.size(); i++) {
            assertEquals(semesters.get(i).get("semester").asInt(), i + 1);
        }

        final List<JsonNode> modulesArray = semesters.findValues("modules");
        for (JsonNode modules : modulesArray) {
            for (JsonNode module : modules) {
                verifyModuleStructure(module);
            }
        }
        verify(conn, times(1)).executeQuery(query, "SE");
    }

    @Test
    public void testProcessEmptySemesters() throws SQLException, IOException {
        when(mockResult.getString(anyString())).thenReturn("[]");
        ObjectNode result = service.getCurriculumSemesters("SE");
        final JsonNode semesters = result.findValue("semesters");
        assertEquals(semesters.size(), 0);
        verify(conn, times(1)).executeQuery(query, "SE");
    }

    private void verifyModuleStructure(JsonNode module) {
        assertNotNull(module.get("module_code"));
        assertNotNull(module.get("module_name"));
        assertNotNull(module.get("credits"));
        assertNull(module.get("semester"));
        assertNull(module.get("study_programme"));
        assertNull(module.get("name"));
    }
}
