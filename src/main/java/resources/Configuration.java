package resources;


import java.util.Map;

public class Configuration {

    private String host;
    private String port;
    private String base;

    private String db_user;
    private String db_passwd;
    private String db;


    private Configuration() {
    }

    public static Configuration fromEnv() {
        Map<String, String> env = System.getenv();

        Builder builder = new Builder();

        builder.setHost(env.getOrDefault("HOST", "0.0.0.0"));
        builder.setPort(env.getOrDefault("PORT", "8080"));
        builder.setBase(env.getOrDefault("BASE", "/fmms"));
        builder.setDbUser(env.getOrDefault("DB_USER", "fmms"));
        builder.setDbPassword(env.getOrDefault("DB_PASSWD", ""));
        builder.setDb(env.getOrDefault("DB", "localhost:5432/fmms"));

        return builder.build();
    }

    public String getServerString() {
        return "http://" + host + ":" + port + base;
    }

    public String getDbString() {
        return "jdbc:postgresql://" + db;
    }

    public String getDbUser() {
        return db_user;
    }

    public String getDbPassword() {
        return db_passwd;
    }

    public static class Builder {
        private Configuration config = new Configuration();

        public Builder setHost(String host) {
            config.host = host;
            return this;
        }

        public Builder setPort(String port) {
            config.port = port;
            return this;
        }

        public Builder setBase(String base) {
            config.base = base;
            return this;
        }


        public Builder setDb(String db) {
            config.db = db;
            return this;
        }

        public Builder setDbUser(String db_user) {
            config.db_user = db_user;
            return this;
        }

        public Builder setDbPassword(String db_passwd) {
            config.db_passwd = db_passwd;
            return this;
        }

        public Configuration build() {
            return config;
        }
    }
}
