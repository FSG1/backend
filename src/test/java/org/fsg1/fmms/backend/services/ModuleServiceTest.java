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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModuleServiceTest {
    @Mock
    private Connection conn;
    @Mock
    private ResultSet mockResult;

    private ModuleService service;

    @Before
    public void init() throws SQLException {
        service = new ModuleService(conn);
        when(conn.executeQuery(anyString(), any())).thenReturn(mockResult);
    }

    @Test (expected = EntityNotFoundException.class)
    public void testProcessEmptyModule() throws SQLException, IOException {
        when(mockResult.getString(anyString())).thenReturn(null);
        service.execute(1);
    }

    @Test
    public void testProcessModule() throws IOException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.readTree(Files.readAllBytes(Paths
                .get("src/test/resources/json/module.json"))).toString();

        when(mockResult.getString(anyString())).thenReturn(jsonString);

        final JsonNode node = service.execute(1);
        assertThat(jsonString, SameJSONAs.sameJSONAs(node.toString()));
    }
}
