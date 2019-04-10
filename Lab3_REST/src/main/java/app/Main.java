package app;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import server.StudentsServer;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class Main {
    public static final String PATH = "C:\\Users\\Kasia\\Desktop\\SI\\Lab3_REST\\src\\main\\resources\\data.json";

    public static void main(String[] args){
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8000).build();
        ResourceConfig config = new ResourceConfig(StudentsServer.class);
        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
        try {
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
