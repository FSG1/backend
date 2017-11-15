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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LayerActivityServiceTest {
    @Mock
    private Connection conn;
    private LayerActivityService service;

    @Before
    public void initMocks() throws Exception {
        service = new LayerActivityService(conn);
    }

    @Test
    public void testProcessQualificationsOverview() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.readTree(Files.readAllBytes(Paths
                .get("src/test/resources/json/qualificationsOverview.json"))).toString();

        when(conn.executeQuery("qualifications_overview", service.getQueryQualificationsOverview(), 1, 1, 1)).thenReturn(jsonString);

        final JsonNode node = service.get(service.getQueryQualificationsOverview(), "qualifications_overview", 1, 1, 1);
        assertThat(jsonString, SameJSONAs.sameJSONAs(node.toString()));
        verify(conn, times(1)).executeQuery("qualifications_overview", service.getQueryQualificationsOverview(), 1, 1, 1);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testProcessNoQualificationsOverview() throws Exception {
        when(conn.executeQuery("qualifications_overview", service.getQueryQualificationsOverview(), 1, 1, 1)).thenThrow(new EntityNotFoundException());
        service.get(service.getQueryQualificationsOverview(), "qualifications_overview", 1, 1, 1);
        verify(conn, times(1)).executeQuery("qualifications_overview", service.getQueryQualificationsOverview(), 1, 1, 1);
    }
}
