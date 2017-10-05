package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.Connection;
import org.junit.Before;
import org.junit.BeforeClass;
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

public class CurriculumServiceTest {
    @Mock
    private Connection conn;

    @Mock
    private ResultSet mockResult;

    private CurriculumService service;
    final String query = "SELECT coalesce(array_to_json(array_agg(row_to_json(co))), '[]'::json) as semesters\n" +
            "FROM study.curriculum_overview co\n" +
            "WHERE study_programme = ?;";

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
        assertEquals(semesters.size(),1);
        verify(conn, times(1)).executeQuery(query, "SE");
    }

    @Test
    public void testProcessOneModule() throws IOException, SQLException {
        when(mockResult.getString(anyString())).thenReturn("[{\"name\":\"2014_NEW\",\"study_programme\":\"SE\",\"semester\":1,\"module_code\":\"BUA1\",\"module_name\":\"Business Administration 1\",\"credits\":4}]");
        ObjectNode result = service.getCurriculumSemesters("SE");
        final JsonNode semesters = result.findValue("semesters");
        assertEquals(semesters.size(),1);
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
        final JsonNode semesters = result.findValue("semesters");
        assertEquals(semesters.size(),8);
        final JsonNode codes = semesters.path("module_code");
        verify(conn, times(1)).executeQuery(query, "SE");
    }

    @Test
    public void testProcessEmptySemesters() throws SQLException, IOException {
        when(mockResult.getString(anyString())).thenReturn("[]");
        ObjectNode result = service.getCurriculumSemesters("SE");
        final JsonNode semesters = result.findValue("semesters");
        assertEquals(semesters.size(),0);
        verify(conn, times(1)).executeQuery(query, "SE");
    }

    @Test
    public void testProcessNonexistantSemester() throws SQLException, IOException {
        when(mockResult.getString(anyString())).thenReturn("[]");
        ObjectNode result = service.getCurriculumSemesters("SE");
        final JsonNode semesters = result.findValue("semesters");
        assertEquals(semesters.size(),0);
        verify(conn, times(1)).executeQuery(query, "SE");
    }
}
