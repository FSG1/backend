package org.fsg1.fmms.backend.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AppBinderTest {

    @Test
    public void testBindingInSingleton() {
        AppBinder di = new AppBinder();
        di.configure();
    }
}
