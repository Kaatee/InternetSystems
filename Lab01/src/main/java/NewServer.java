import com.sun.net.httpserver.*;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;


public class NewServer {
    private static String path;
    private static int port;

    public static void main(String[] args) throws Exception {
        port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        path=args[0];
        server.createContext("/", new NewServer.RootHandler());

        System.out.println("Starting server");
        server.start();
    }

    static class RootHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                int code = 200;
                String addressTmp = exchange.getRequestURI().toString().replace("%20"," ");

                System.out.println("\ntmp: " + addressTmp);
                System.out.println("Path: "+ path);

                String address = path+addressTmp;

                System.out.println("Adres: "+ address);
                String canonicalPath = new File(address).getCanonicalPath();

                if(!canonicalPath.startsWith(path))
                    code=403;

                System.out.println("Po canonical path: " + canonicalPath);
                File file = new File(canonicalPath);

                byte [] toDisplay;
                String type = "";

                String content = "Lista plikow: \n<ul>";

                if(!canonicalPath.endsWith("/"))
                    address += "/";
                if(canonicalPath.endsWith("//"))
                    address += "/";

                //check if this location points directory etc.
                if(file.isDirectory()) {
                    for (File f : file.listFiles()) {
                        content += "<li><a href=\"" + "http://localhost:" + port + "/"+ addressTmp.replace("\\","") +"/" + f.getName() + "\">" + f.getName() + "</a></li>";
                    }
                    content += "</ul>";
                    toDisplay=content.getBytes();
                    type = "text/html;charset=utf-8";
                }

                else {
                    toDisplay = Files.readAllBytes(Paths.get(address));
                    type = Files.probeContentType(file.toPath());

                    if(type==null)
                        type = "text/html;charset=utf-8";
                }

                exchange.getResponseHeaders().set("Content-Type",type );

                if(code!=200) {
                    toDisplay= "Permission denied".getBytes();
                }

                exchange.sendResponseHeaders(code, toDisplay.length);
                OutputStream os = exchange.getResponseBody();
                os.write(toDisplay);
                os.close();

            } catch (Exception e) {
                String msg = "Nie ma takiego pliku";
                byte[] toDisplay= msg.getBytes();
                exchange.sendResponseHeaders(404, toDisplay.length);
                OutputStream os = exchange.getResponseBody();
                os.write(toDisplay);
                os.close();
                e.printStackTrace();
            }
        }
    }
}
