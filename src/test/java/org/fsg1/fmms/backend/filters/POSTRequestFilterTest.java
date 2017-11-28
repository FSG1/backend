package org.fsg1.fmms.backend.filters;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;

import static io.restassured.RestAssured.given;

public class POSTRequestFilterTest extends JerseyTest {
    @Override
    protected Application configure() {
        return new ResourceConfig()
                .register(POSTRequestFilter.class);
    }

    @Test
    public void testCORSResponseHeaders() {
        given()
                .post("http://localhost:9998/fmms/post")
                .then()
                .statusCode(400);

        given()
                .body("")
                .post("http://localhost:9998/fmms/post")
                .then()
                .statusCode(400);

        given()
                .body("Hello")
                .post("http://localhost:9998/fmms/post")
                .then()
                .statusCode(404);
    }
}
