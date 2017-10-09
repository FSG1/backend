package org.fsg1.fmms.backend.resources;

import java.util.Map;

/**
 * Configuration class.
 *
 * @author Tobias Derksen
 */
public final class Configuration {

    private String host;

    private String port;

    private String base;

    private String dbUser;

    private String dbPasswd;

    private String db;

    /**
     * Private constructor.
     * Class cannot be instantiated directly
     */
    private Configuration() {
    }

    /**
     * Leads configuration from environment variables.
     *
     * @return Concrete Configuration instance
     */
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

    /**
     * Returns the grizzly server uri.
     *
     * @return Server string to start grizzly
     */
    public String getServerString() {
        return "http://" + host + ":" + port + base;
    }

    /**
     * Returns the database jdbc uri.
     *
     * @return Postgresql Db String to use for JDBC
     */
    public String getDbString() {
        return "jdbc:postgresql://" + db;
    }

    /**
     * Returns the database username.
     *
     * @return Database username
     */
    public String getDbUser() {
        return dbUser;
    }

    /**
     * Returns the database password.
     *
     * @return Database password
     */
    public String getDbPassword() {
        return dbPasswd;
    }

    /**
     * Builder class for configuration.
     *
     * @author Tobias Derksen
     */
    public static final class Builder {

        private String host;

        private String port;

        private String base;

        private String dbUser;

        private String dbPasswd;

        private String db;

        /**
         * Sets hostname.
         *
         * @param newHost Hostname for the server
         * @return Fluent interface
         */
        public Builder setHost(final String newHost) {
            this.host = newHost;
            return this;
        }

        /**
         * Sets the Port Number for the Server.
         *
         * @param newPort Port Number
         * @return Fluent interface
         */
        public Builder setPort(final String newPort) {
            this.port = newPort;
            return this;
        }

        /**
         * Sets the server´s base path.
         *
         * @param newBase Base Path
         * @return Fluent interface
         */
        public Builder setBase(final String newBase) {
            this.base = newBase;
            return this;
        }

        /**
         * Sets the database connection uri.
         *
         * @param newDb Jdbc conform database uri
         * @return Fluent interface
         */
        public Builder setDb(final String newDb) {
            this.db = newDb;
            return this;
        }

        /**
         * Sets the database username.
         *
         * @param newDbUser Username
         * @return Fluent interface
         */
        public Builder setDbUser(final String newDbUser) {
            this.dbUser = newDbUser;
            return this;
        }

        /**
         * Sets the database password.
         *
         * @param newDbPasswd Password
         * @return Fluent interface
         */
        public Builder setDbPassword(final String newDbPasswd) {
            this.dbPasswd = newDbPasswd;
            return this;
        }

        /**
         * Builds the configuration object.
         * Can be called many times. Returns always a new object
         *
         * @return Concrete configuration object
         */
        public Configuration build() {
            Configuration config = new Configuration();
            config.base = base;
            config.db = db;
            config.dbPasswd = dbPasswd;
            config.dbUser = dbUser;
            config.port = port;
            config.host = host;

            return config;
        }
    }
}
