package org.fsg1.fmms.backend.app;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigurationTest {

    @Test
    public void testDefaults() {
        final Configuration configuration = Configuration.fromEnv();
        assertEquals("", configuration.getDbPassword());
        assertEquals("jdbc:postgresql://172.17.0.1:5432/fmms", configuration.getDbString());
        assertEquals("fmms", configuration.getDbUser());
        assertEquals("http://0.0.0.0:8080/fmms", configuration.getServerString());
    }

    @Test
    public void testBuilder() {
        final Configuration.Builder builder = new Configuration.Builder();

        builder.setHost("9.8.7.6")
                .setPort("8080")
                .setBase("/database")
                .setDbUser("user")
                .setDbPassword("pass")
                .setDb("123.45.6.7:8900/database");

        final Configuration configuration = builder.build();
        assertEquals("pass", configuration.getDbPassword());
        assertEquals("jdbc:postgresql://123.45.6.7:8900/database", configuration.getDbString());
        assertEquals("user", configuration.getDbUser());
        assertEquals("http://9.8.7.6:8080/database", configuration.getServerString());
    }
}
