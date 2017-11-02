package org.fsg1.fmms.backend.exceptions;

import org.junit.Assert;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class AppExceptionTest {
    @Test
    public void testException() {
        AppException exception = new AppException(350, "Error", "Fix it");
        assertEquals(350, exception.getStatus());
        assertEquals("Error", exception.getErrorMessage());
        assertEquals("Fix it", exception.getDeveloperMessage());

        exception = new AppException();
        assertNull(exception.getErrorMessage());
        assertNull(exception.getDeveloperMessage());
        try {
            exception.getStatus();
            Assert.fail();
        } catch (NullPointerException ignored) {
        }
    }
}
