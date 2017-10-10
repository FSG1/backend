package org.fsg1.fmms.backend.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.fsg1.fmms.backend.services.CurriculaService;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.mockito.Mockito.*;

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
        MockitoAnnotations.initMocks(this);
        return new ResourceConfig()
                .register(CurriculaEndpoint.class)
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(service).to(CurriculaService.class);
                    }
                });
    }

    @Test
    public void testGetCurricula() throws IOException, SQLException {
        ArrayNode resultArray = mapper.createArrayNode();
        ObjectNode seNode = mapper.createObjectNode();
        seNode.put("id", 1);
        seNode.put("code", "SE");
        seNode.put("name", "Software Engineering");

        ObjectNode biNode = mapper.createObjectNode();
        biNode.put("id", 2);
        biNode.put("code", "BI");
        biNode.put("name", "Business Informatics");

        resultArray.add(seNode);
        resultArray.add(biNode);

        when(service.getCurricula())
                .thenReturn(resultArray);
        given()
                .spec(spec)
                .get("curricula")
                .then()
                .statusCode(200)
                .body(".", iterableWithSize(2))
                .body("[0].code", equalTo("SE"))
                .body("[0].name", equalTo("Software Engineering"))
                .body("[0].id", equalTo(1))
                .body("[1].code", equalTo("BI"))
                .body("[1].name", equalTo("Business Informatics"))
                .body("[1].id", equalTo(2));
        verify(service, times(1)).getCurricula();
        reset(service);
    }

    @Test
    public void testGetEmptySemester() throws SQLException, IOException {
        when(service.getCurricula())
                .thenReturn(mapper.createArrayNode());

        given()
                .spec(spec)
                .get("curricula")
                .then()
                .body(".", iterableWithSize(0))
                .statusCode(200);
        verify(service, times(1)).getCurricula();
        reset(service);
    }

    @Test
    public void testExpectServerError() throws IOException, SQLException {
        when(service.getCurricula())
                .thenReturn(null);
        given()
                .spec(spec)
                .get("curricula")
                .then()
                .statusCode(500);
    }
}
