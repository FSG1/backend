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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LayerActivityServiceTest {
    @Mock
    private Connection conn;
    @Mock
    private ResultSet mockResult;
    private LayerActivityService service;

    @Before
    public void initMocks() throws SQLException {
        service = new LayerActivityService(conn);
        when(conn.executeQuery(anyString(), any())).thenReturn(mockResult);
    }

    @Test
    public void testProcessQualificationsOverview() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.readTree(Files.readAllBytes(Paths
                .get("src/test/resources/json/qualificationsOverview.json"))).toString();

        when(mockResult.next()).thenReturn(true);
        when(mockResult.getString(anyString())).thenReturn(jsonString);

        final JsonNode node = service.get(service.getQueryQualificationsOverview(), "qualifications_overview", 1, 1, 1);
        assertThat(jsonString, SameJSONAs.sameJSONAs(node.toString()));
        verify(conn, times(1)).executeQuery(service.getQueryQualificationsOverview(), 1, 1, 1);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testProcessNoQualificationsOverview() throws Exception {
        service.get(service.getQueryQualificationsOverview(), "qualifications_overview", 1, 1, 1);
        verify(conn, times(1)).executeQuery(service.getQueryQualificationsOverview(), 1, 1, 1);
    }
}
