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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QualificationsServiceTest {
    @Mock
    private Connection conn;
    @Mock
    private ResultSet mockResult;
    private QualificationsService service;

    @Before
    public void initMocks() throws SQLException {
        service = new QualificationsService(conn);
        when(conn.executeQuery(anyString(), any())).thenReturn(mockResult);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testProcessNoQualifications() throws Exception {
        service.get(service.getQualificationsQuery(), "qualifications");
    }

    @Test
    public void testProcessQualifications() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.readTree(Files.readAllBytes(Paths
                .get("src/test/resources/json/qualifications.json"))).toString();

        when(mockResult.next()).thenReturn(true);
        when(mockResult.getString(anyString())).thenReturn(jsonString);

        final JsonNode node = service.get(service.getQualificationsQuery(), "qualifications");
        assertThat(jsonString, SameJSONAs.sameJSONAs(node.toString()));
        verify(conn, times(1)).executeQuery(service.getQualificationsQuery());
    }
}
