package org.fsg1.fmms.backend.endpoints;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.fsg1.fmms.backend.exceptions.EntityNotFoundException;
import org.fsg1.fmms.backend.services.CurriculaService;
import org.fsg1.fmms.backend.services.Service;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CurriculaEndpointTest extends JerseyTest {

    private static RequestSpecification spec;
    private ObjectMapper mapper = new ObjectMapper();

    @Mock
    private CurriculaService service;

    @BeforeClass
    public static void initSpec() {
        spec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("http://localhost:9998/")
                .addFilter(new ResponseLoggingFilter())//log request and response for better debugging. You can also only log if a requests fails.
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Override
    public ResourceConfig configure() {
        return new ResourceConfig()
                .register(CurriculaEndpoint.class)
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(service).to(Service.class);
                    }
                });
    }

    @Test
    public void testGetCurricula() throws IOException, SQLException, EntityNotFoundException {
        JsonNode node = mapper.readTree(Files.readAllBytes(Paths.get("src/test/resources/json/curricula.json")));

        when(service.get(eq(service.getQueryCurriculaString()), eq("curricula")))
                .thenReturn(node);
        given()
                .spec(spec)
                .get("curricula")
                .then()
                .statusCode(200)
                .header("Content-Type", MediaType.APPLICATION_JSON);
        verify(service, times(2)).get(service.getQueryCurriculaString(), "curricula");
    }

    @Test
    public void testGetEmptySemester() throws SQLException, IOException, EntityNotFoundException {
        when(service.get(service.getQueryCurriculaString(), "curricula"))
                .thenThrow(EntityNotFoundException.class);

        given()
                .spec(spec)
                .get("curricula")
                .then()
                .statusCode(404);
        verify(service, times(2)).getQueryCurriculaString();
    }

    @Test
    public void testExpectServerError() throws IOException, SQLException, EntityNotFoundException {
        given()
                .spec(spec)
                .get("curricula")
                .then()
                .statusCode(500);
    }
}
