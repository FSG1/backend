package resources;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    static final String BASE_URI = "http://localhost:8080/fmms/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        Configuration config = new Configuration();

        // create a resource config that scans for JAX-RS resources and providers
        // in resources package
        final ResourceConfig rc = new ResourceConfig();

        AppBinder di = new AppBinder();
        di.bind(config).to(Configuration.class);

        rc.register(di);
        rc.packages("resources");

        System.out.println("Server listen on: " + config.getServerString());

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(config.getServerString()), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
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

