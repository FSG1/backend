package resources;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Properties;

public class Configuration {

    private String host;
    private String port;
    private String base;

    private String db_user;
    private String db_passwd;
    private String db_host;
    private String db_name;
    private String db_port;

    public Configuration() {
        this.loadConfigFromFile("config/server.properties");
        this.loadConfigFromFile("config/database.properties");
        this.loadConfigFromEnv();
    }

    private void setEachField(GetValueInterface obj) {
        Class<? extends Configuration> self = this.getClass();
        Field[] fields = self.getDeclaredFields();

        Arrays.stream(fields)
                .parallel()
                .forEach((field) -> setField(field, obj.getValue(field.getName()))
                );
    }

    private void setField(Field field, String value) {
        try {
            if (value != null && !value.isEmpty()) {
                field.setAccessible(true);
                field.set(this, value);
            }

        } catch (IllegalAccessException e) {
            System.err.println("Cannot access field: " + field.getName());
            // variable left unset
        }
    }

    private void loadConfigFromEnv() {
        setEachField((name) -> System.getenv(name.toUpperCase()));
    }

    private void loadConfigFromFile(String filename) {
        Properties prop = new Properties();
        ClassLoader loader = this.getClass().getClassLoader();

        InputStream input = loader.getResourceAsStream(filename);
        if (input == null) {
            return;
        }

        try {
            prop.load(input);
            setEachField(prop::getProperty);
        } catch (IOException e) {
            // Properties not loaded
            System.err.println("Cannot load property file: " + filename);
        }
    }

    public String getServerString() {
        return "http://" + host + ":" + port + base;
    }

    public String getDbString() {
        return "jdbc:postgresql://" + db_host + "/" + db_name;
    }

    public String getDbUser() {
        return db_user;
    }

    public String getDbPassword() {
        return db_passwd;
    }

    @FunctionalInterface
    private interface GetValueInterface {
        String getValue(String fieldName);
    }
}
