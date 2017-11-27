package org.fsg1.fmms.backend.filters;

import org.fsg1.fmms.backend.app.Configuration;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.inject.Singleton;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;

public class AuthFilterTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig()
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(Configuration.fromEnv()).to(Configuration.class).in(Singleton.class);
                    }
                })
                .register(AuthFilter.class);
    }

    @Test
    public void testNoAuth() {
        given()
                .get("http://localhost:9998/restricted/auth")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void testInvalidHeader() {
        given().header("Authorization", "Something really wrong")
                .get("http://localhost:9998/restricted/auth")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void testAuthFails() {
        // Given credentials are invalid
        given().header("Authorization", "Zm1tczptb2R1bGVtYW5hZ2UzMzNtZW50")
                .get("http://localhost:9998/restricted/auth")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void testNotSecured() {
        given()
                .get("http://localhost:9998/curricula")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testAuthSuccess() {
        // Credentials are base64 encoded. See Configuration for default credentials
        given().header("Authorization", "Basic Zm1tczptb2R1bGVtYW5hZ2VtZW50")
                .get("http://localhost:9998/restricted/auth")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testOptionsRequest() {
        given()
                .options("http://localhost:9998/restricted/auth")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }
}
