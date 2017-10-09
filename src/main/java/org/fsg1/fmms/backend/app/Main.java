package org.fsg1.fmms.backend.resources;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 */
public final class Main {

    /**
     * Private constructor.
     * Class should never be instantiated.
     */
    private Main() {
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        Configuration config = Configuration.fromEnv();

        // create a resource config that scans for JAX-RS resources and providers
        // in resources package
        final ResourceConfig rc = new ResourceConfig();

        AppBinder di = new AppBinder();
        di.bind(config).to(Configuration.class);

        rc.register(di);
        rc.packages("resources");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(config.getServerString()), rc);
    }

    /**
     * Main method.
     * @param args Command line arguments
     * @throws IOException if an error occurs while attempting to start the server.
     */
    public static void main(final String[] args) throws IOException {
        System.out.println("Booting server ...");

        final HttpServer server = startServer();

        // register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping server ...");
            server.shutdownNow();
        }, "shutdownHook"));

        // run
        try {
            System.out.println("Starting server ...");
            server.start();
            System.out.println("Press CTRL^C to exit..");
            Thread.currentThread().join();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}

