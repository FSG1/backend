package jaxrs;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EndpointsTest {
    @Test
    public void testEndpoints(){
        assertEquals(Endpoints.CURRICULUM, "curriculum/{1}");
        assertEquals(Endpoints.SEMESTERS, "curriculum/{1}/semesters");
    }
}
