package org.fsg1.fmms.backend.exceptions;

import org.junit.Test;

import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class AppExceptionTest {

    @Test
    public void testException() {
        AppException exception = new AppException(350, "Error. Fix it");
        Response response = exception.getResponse();
        assertEquals(350, response.getStatus());
        assertEquals("Error. Fix it", response.getEntity());

        exception = new AppException();
        response = exception.getResponse();
        assertNull(response.getEntity());
        assertEquals(500, response.getStatus());
    }
}
