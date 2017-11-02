package org.fsg1.fmms.backend.exceptions;

import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class AppExceptionMapperTest {
    @Test
    public void testAppException() {
        final AppException exception = new AppException(312, "Something happened", "Notify the administrator");
        AppExceptionMapper mapper = new AppExceptionMapper();
        final Response response = mapper.toResponse(exception);
        assertEquals(response.getStatus(), 312);
        final Object entity = response.getEntity();
        assertEquals(entity, exception);
    }

    @Test
    public void testException() {
        final Exception exception = new Exception("Nobody could have expected this");
        AppExceptionMapper mapper = new AppExceptionMapper();
        final Response response = mapper.toResponse(exception);
        assertEquals(response.getStatus(), 500);
        final Object entity = response.getEntity();
        assertEquals(entity, exception);
    }
}
