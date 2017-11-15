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
public class ModulesServiceTest {
    @Mock
    private Connection conn;
    private ModulesService service;

    @Before
    public void initMocks() throws Exception {
        service = new ModulesService(conn);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testProcessEmptyModule() throws Exception {
        when(conn.executeQuery("module", service.getQueryModuleInformation(), 1, "1")).thenThrow(new EntityNotFoundException());
        service.get(service.getQueryModuleInformation(), "module", 1, "1");
        verify(conn, times(1)).executeQuery("module", service.getQueryModuleInformation(), 1, "1");
    }

    @Test
    public void testProcessModule() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.readTree(Files.readAllBytes(Paths
                .get("src/test/resources/json/module.json"))).toString();

        when(conn.executeQuery("module", service.getQueryModuleInformation(), 1, "1")).thenReturn(jsonString);

        final JsonNode node = service.get(service.getQueryModuleInformation(), "module", 1, "1");
        assertThat(jsonString, SameJSONAs.sameJSONAs(node.toString()));
        verify(conn, times(1)).executeQuery("module", service.getQueryModuleInformation(), 1, "1");

    }
}
