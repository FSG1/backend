package org.fsg1.fmms.backend.filters;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;

import static io.restassured.RestAssured.given;

public class CORSResponseFilterTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig()
                .register(CORSResponseFilter.class);
    }

    @Test
    public void testCORSResponseHeaders() {
        given()
                .get("http://localhost:9998/fmms/cors")
                .then()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS")
                .header("Access-Control-Allow-Headers", "X-Requested-With, Content-Type");
    }
}
