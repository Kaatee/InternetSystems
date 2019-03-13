import com.cedarsoftware.util.io.JsonWriter;
import com.sun.net.httpserver.*;

import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

public class TPSIServer {
    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new RootHandler());

        server.createContext("/echo/", new EchoHandler());

        server.createContext("/redirect/301", new RedirectHandler());
        server.createContext("/redirect/302", new RedirectHandler());
        server.createContext("/redirect/303", new RedirectHandler());
        server.createContext("/redirect/307", new RedirectHandler());
        server.createContext("/redirect/308", new RedirectHandler());

        server.createContext("/cookies/", new CookiestHandler());
        server.createContext("/auth/", new AuthHandler());

        HttpContext context = server.createContext("/auth2/", new Auth2Handler());
        context.setAuthenticator(new BasicAuthenticator("/auth2/") {
            @Override
            public boolean checkCredentials(String username, String password) {
                return username.equals("user") && password.equals("password");
            }
        });

        System.out.println("Starting server on port: " + port);
        server.start();
    }

    static class RootHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                byte[] encoded = Files.readAllBytes(Paths.get("C:\\Users\\Kasia\\Desktop\\SI\\Lab01\\src\\main\\java\\index.html"));

                exchange.getResponseHeaders().set("Content-Type", "text/html;charset=utf-8");
                exchange.sendResponseHeaders(200,encoded.length);
                OutputStream os = exchange.getResponseBody(); //pobranie strumienia dla odpowiedzi
                os.write(encoded);
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class EchoHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                Map map = exchange.getRequestHeaders();
                String json = JsonWriter.objectToJson(map);

                byte[] encoded = JsonWriter.formatJson(json).getBytes();

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200,encoded.length);
                OutputStream os = exchange.getResponseBody();
                os.write(encoded);
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    static class RedirectHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String address = exchange.getRequestURI().toString();
               /* /redirect/301
                        0 - "";
                        1 - "redirect";
                        2 - "301"*/

                Logger logger = Logger.getLogger(getClass().getName());
                logger.info(address);

                int code = Integer.parseInt(address.split("/")[2]);

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(code, -1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class CookiestHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String address = exchange.getRequestURI().toString();

                Random rand = new Random();
                int randInt = rand.nextInt();

                String randStr = String.valueOf(randInt);
                byte[] byteStr = randStr.getBytes();

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Set-Cookie", randStr);
                exchange.sendResponseHeaders(200,randStr.length());

                OutputStream os = exchange.getResponseBody();
                os.write(byteStr);
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    static class AuthHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String header = exchange.getRequestHeaders().getFirst("Authorization");
                int err = 401;
                String text = "Nie uwierzytelniles sie poprawnie";

               if(header!=null){
                   String[] parts = header.split("\\s");
                   //for(int i=0; i<parts.length; i++)
                   //System.out.println(parts[i]);

                   Base64.Decoder decoder = Base64.getDecoder();
                   String decoded = new String(decoder.decode(parts[1]));
                   //System.out.println("Decoded: " + decoded);

                   String[] data = decoded.split(":");
                   //System.out.println(data.length);
                   if(data.length>0 )
                   if(data[0].equals("user") && data[1].equals("password")){
                       err = 200;
                       text = "Hello World /auth/!";
                   }
               }

                byte[] bytes = text.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.getResponseHeaders().set("WWW-Authenticate", "Basic realm=MyDomain");
                exchange.sendResponseHeaders(err,bytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    static class Auth2Handler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String text = "Hello world /auth2/!";
                byte[] byteStr = text.getBytes();

                exchange.getResponseHeaders().set("Content-Type", "text/plain");

                exchange.sendResponseHeaders(200,byteStr.length);

                OutputStream os = exchange.getResponseBody();
                os.write(byteStr);
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }









}
