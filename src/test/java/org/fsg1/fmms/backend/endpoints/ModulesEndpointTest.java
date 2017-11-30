package org.fsg1.fmms.backend.endpoints;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.fsg1.fmms.backend.exceptions.AppExceptionMapper;
import org.fsg1.fmms.backend.exceptions.EntityNotFoundException;
import org.fsg1.fmms.backend.filters.POSTRequestFilter;
import org.fsg1.fmms.backend.services.ModulesService;
import org.fsg1.fmms.backend.services.Service;
import org.fsg1.fmms.backend.services.TransactionRunner;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModulesEndpointTest extends JerseyTest {

    private static RequestSpecification spec;
    private ObjectMapper mapper = new ObjectMapper();
    @Mock
    private ModulesService service;
    @Mock
    private Connection connection;

    @BeforeClass
    public static void initSpec() {
        spec = new RequestSpecBuilder()
                .setBaseUri("http://localhost:9998/")
                .addFilter(new ResponseLoggingFilter())//log request and response for better debugging. You can also only log if a requests fails.
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Override
    public ResourceConfig configure() {
        return new ResourceConfig()
                .register(ModulesEndpoint.class)
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(service).to(Service.class);
                    }
                })
                .register(AppExceptionMapper.class)
                .register(POSTRequestFilter.class)
                .register(JacksonFeature.class);
    }

    @Test
    public void testExpectServerError() throws Exception {
        given()
                .spec(spec)
                .get("curriculum/1/module/BUA1")
                .then()
                .statusCode(500);

        given()
                .spec(spec)
                .get("/module/BUA1")
                .then()
                .statusCode(500);
    }

    @Test
    public void testGetNoModule() throws Exception {
        when(service.get(eq(service.getQueryModuleInformation()), eq("module"), eq("BUA1"), eq(1)))
                .thenThrow(new EntityNotFoundException());

        given()
                .spec(spec)
                .get("curriculum/1/module/BUA1")
                .then()
                .statusCode(404);
        verify(service, times(2)).get(eq(service.getQueryModuleInformation()), eq("module"), eq(1), eq("BUA1"));
    }

    @Test
    public void testGetModule() throws Exception {
        JsonNode node = mapper.readTree(Files.readAllBytes(Paths.get("src/test/resources/json/module.json")));

        when(service.get(eq(service.getQueryModuleInformation()), eq("module"), eq("BUA1"), eq(1)))
                .thenReturn(node);
        given()
                .spec(spec)
                .get("curriculum/1/module/BUA1")
                .then()
                .statusCode(200)
                .header("Content-Type", MediaType.APPLICATION_JSON);
        verify(service, times(2)).get(eq(service.getQueryModuleInformation()), eq("module"), eq(1), eq("BUA1"));
    }

    @Test
    public void testGetEditableModule() throws Exception {
        when(service.get(eq(service.getQueryEditableModule()), eq("module"), eq("BUA1")))
                .thenThrow(new EntityNotFoundException());

        given()
                .spec(spec)
                .get("module/BUA1")
                .then()
                .statusCode(404);
        verify(service, times(2)).get(eq(service.getQueryEditableModule()), eq("module"), eq("BUA1"));
    }

    @Test
    public void testGetNoEditableModule() throws Exception {
        JsonNode node = mapper.readTree(Files.readAllBytes(Paths.get("src/test/resources/json/editableModuleOutput.json")));

        when(service.get(eq(service.getQueryEditableModule()), eq("module"), eq("BUA1")))
                .thenReturn(node);
        given()
                .spec(spec)
                .get("module/BUA1")
                .then()
                .statusCode(200)
                .header("Content-Type", MediaType.APPLICATION_JSON);
        verify(service, times(2)).get(eq(service.getQueryEditableModule()), eq("module"), eq("BUA1"));
    }

    @Test
    public void testPostEmpty() throws Exception {
        given()
                .spec(spec)
                .contentType(ContentType.JSON)
                .post("module/BUA1")
                .then()
                .statusCode(400);
        verify(service, times(0)).update(any(), anyString(), any(), any(), any());
    }

    @Test
    public void testPostServerError() throws Exception {
        given()
                .spec(spec)
                .contentType(ContentType.JSON)
                .body("{}")
                .post("module/2")
                .then()
                .statusCode(500);
        verify(service, times(0)).update(any(), any(), any());
    }

    @Test
    public void testPostModule() throws Exception {
        when(service.getUpdateModuleInformationStatements()).thenCallRealMethod();
        Mockito.doAnswer(invocation -> {
            final TransactionRunner argument = invocation.getArgument(0);
            argument.run(connection);
            return 0;
        }).when(service).executeTransactional(any());
        JsonNode node = mapper.readTree(Files.readAllBytes(Paths.get("src/test/resources/json/editableModuleInput.json")));
        given()
                .spec(spec)
                .contentType(ContentType.JSON)
                .body(node)
                .post("module/9")
                .then()
                .statusCode(200);

        final String[] statements = service.getUpdateModuleInformationStatements();

        verify(service, times(1)).executeTransactional(any(TransactionRunner.class));
        verify(service, times(11)).update(any(Connection.class), any(), any());
        verify(service, times(1)).update(any(Connection.class), eq(statements[0]), eq("JOS"), eq("Java on steroids"), eq(4), eq(1), eq(4), eq(true), eq(9));
        verify(service, times(1)).update(any(Connection.class), eq(statements[1]), eq(9));
        verify(service, times(1)).update(any(Connection.class), eq(statements[2]), eq(9), eq("Do some stuff"));
        verify(service, times(1)).update(any(Connection.class), eq(statements[2]), eq(9), eq("And other stuff too"));
        verify(service, times(1)).update(any(Connection.class), eq(statements[3]), eq("Hello"), eq("This course is impossible"), eq(""), eq(9));
        verify(service, times(1)).update(any(Connection.class), eq(statements[4]), eq(9));
        verify(service, times(1)).update(any(Connection.class), eq(statements[5]), eq(9), eq("BOOK"), eq("This book"));
        verify(service, times(1)).update(any(Connection.class), eq(statements[6]), eq(9));
        verify(service, times(1)).update(any(Connection.class), eq(statements[7]), eq(9), eq(1));
        verify(service, times(1)).update(any(Connection.class), eq(statements[7]), eq(9), eq(2));
    }
}
