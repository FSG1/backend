package org.fsg1.fmms.backend.endpoints;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.fsg1.fmms.backend.app.Configuration;
import org.fsg1.fmms.backend.exceptions.AppExceptionMapper;
import org.fsg1.fmms.backend.filters.AuthFilter;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;

@RunWith(MockitoJUnitRunner.class)
public class AuthEndpointTest extends JerseyTest {

    private static RequestSpecification spec;

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
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(Configuration.fromEnv()).to(Configuration.class).in(Singleton.class);
                    }
                })
                .register(AuthEndpoint.class)
                .register(AuthFilter.class)
                .register(AppExceptionMapper.class);
    }

    @Test
    public void testAuth() throws Exception {
        given().header("Authorization", "Basic Zm1tczptb2R1bGVtYW5hZ2VtZW50")
                .spec(spec)
                .post("auth")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        given().header("Authorization", "Basic lkuahdsfliköas")
                .spec(spec)
                .post("auth")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }
}
