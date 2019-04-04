import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.io.IOException;

import sun.misc.IOUtils;

import com.fasterxml.jackson.databind.MappingIterator;


public class Server {
    private static ArrayList<CsvRow> csvObjects = new ArrayList<>();
    private static final String FILEPATH = "C:\\Users\\Kasia\\Desktop\\SI\\Lab02_PROXY\\src\\main\\java\\ststistics.csv";

    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        Reader reader = Files.newBufferedReader(Paths.get(FILEPATH));
        CSVReader csvReader = new CSVReader(reader);

        String[] row;

        while((row = csvReader.readNext())!= null){
            CsvRow object = new CsvRow(row[0], Integer.parseInt(row[1]), Integer.parseInt(row[2]), Integer.parseInt(row[3]));
            csvObjects.add(object);
        }

        server.createContext("/", new RootHandler());

        System.out.println("Starting server on port: " + port);
        server.start();
    }

    static class RootHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException{
            //System.out.println("Zapytanie");
            try {
                int code;
                byte[] toWrite = {};
                URL url = new URL(exchange.getRequestURI().toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                ArrayList<String> forbiddenUrls = readFile("C:\\Users\\Kasia\\Desktop\\SI\\Lab02_PROXY\\src\\main\\forbiddenUris.txt");
                System.out.println(exchange.getRequestURI().getAuthority());
                boolean cont = false;
                for (String s : forbiddenUrls){
                    if ((exchange.getRequestURI().getAuthority()+exchange.getRequestURI().getPath()).contains(s)){
                        cont=true;
                        break;
                    }
                }
                if (cont) {
                    code = 403;
                    toWrite = "Forbidden".getBytes();
                } else {
                    int receivedCount=0;
                    int sendCount=0;
                    //Logger logger = Logger.getLogger("Logger");
                    //logger.info(exchange.getRequestMethod() + " uri: " + exchange.getRequestURI().toString());

                    connection.setInstanceFollowRedirects(false);
                    HttpURLConnection.setFollowRedirects(false);

                    connection.setRequestMethod(exchange.getRequestMethod());

                    for (Map.Entry<String, List<String>> header : exchange.getRequestHeaders().entrySet()) {
                        connection.addRequestProperty(header.getKey(), String.join(", ", header.getValue()));
                    }

                    String method = exchange.getRequestMethod().toLowerCase();
                    System.out.println(method);

                    if (method.equals("post") || method.equals("put") || method.equals("patch")) {
                        connection.setDoOutput(true);
                        OutputStream outputStream = connection.getOutputStream();
                        byte[] bytes = IOUtils.readFully(exchange.getRequestBody(), -1, false);
                        outputStream.write(bytes);
                        sendCount = bytes.length;
                    }

                    code = connection.getResponseCode();
                    try {
                        toWrite = IOUtils.readFully(connection.getInputStream(), -1, false);
                        receivedCount=toWrite.length;
                    } catch (Exception e) {
                        if (connection.getErrorStream() != null)
                            toWrite = IOUtils.readFully(connection.getErrorStream(), -1, false);
                            receivedCount=toWrite.length;
                            e.printStackTrace();
                    }

                    for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
                        if (header.getKey() != null && !header.getKey().toLowerCase().equals("transfer-encoding"))
                            exchange.getResponseHeaders().add(header.getKey(), String.join(", ", header.getValue()));
                    }

                    //Statistics handle
                    boolean isInList=false;
                    for (CsvRow obj: csvObjects ) {
                        if (obj.getUri().equals(exchange.getRequestURI().getAuthority())) {
                            obj.setRequestCount(obj.getRequestCount() + 1);
                            obj.setSendBytesCount(obj.getSendBytesCount() + sendCount);
                            obj.setReceivedBytesCount(obj.getReceivedBytesCount() + receivedCount);
                            //System.out.println("Mialem taki rekord");
                            isInList = true;
                        }
                    }
                    if(!isInList) {
                        CsvRow newRow = new CsvRow(exchange.getRequestURI().getAuthority(), 1,sendCount, receivedCount );
                        csvObjects.add(newRow);
                        //System.out.println("Nie mialem takiego rekordu");
                    }
                    }
                    //System.out.println("Dlugosc listy obiektow na koncu:"+csvObjects.size());

                exchange.sendResponseHeaders(code, toWrite.length);
                OutputStream os = exchange.getResponseBody(); //pobranie strumienia dla odpowiedzi
                os.write(toWrite);
                os.close();

                //Write to csv
                try (
                        Writer writer = Files.newBufferedWriter(Paths.get(FILEPATH));
                ) {
                    ColumnPositionMappingStrategy<CsvRow> mappingStrategy = new ColumnPositionMappingStrategy<CsvRow>();
                    mappingStrategy.setType(CsvRow.class);
                    String[] columns = new String[] { "uri", "requestCount", "sendBytesCount", "receivedBytesCount" };
                    mappingStrategy.setColumnMapping(columns);

                    StatefulBeanToCsvBuilder<CsvRow> builder = new StatefulBeanToCsvBuilder<CsvRow>(writer);
                    StatefulBeanToCsv<CsvRow> beanToCsv = builder.withMappingStrategy(mappingStrategy).build();

                    //StatefulBeanToCsv<CsvRow> beanToCsv = new StatefulBeanToCsvBuilder(writer).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).build();
                    beanToCsv.write(csvObjects);
                }

            } catch (Exception e) {
                System.out.println("JEstem w catch");
                e.printStackTrace();
            }
        }


        private ArrayList<String> readFile(String filePath) {
            ArrayList<String> list = new ArrayList<>();

            try {
                File file = new File(filePath);
                BufferedReader br = new BufferedReader(new FileReader(file));

                String st;
                while ((st = br.readLine()) != null)
                    list.add(st);
            } catch (Exception e) {
            }
            return list;
        }

        private ArrayList<CsvRow> readDataFromCsv(String filePath) {
            ArrayList<CsvRow> list = new ArrayList<>();

            try {
                File file = new File(filePath);
                BufferedReader br = new BufferedReader(new FileReader(file));
                String[] splitedLine;
                String st;
                String uri;
                int requestCount;
                int sendBytesCount;
                int receivedBytesVount;

                while ((st = br.readLine()) != null) {
                    splitedLine = st.split(",");
                    if (splitedLine.length == 4) {
                        uri = splitedLine[0];
                        requestCount = Integer.parseInt(splitedLine[1]);
                        sendBytesCount = Integer.parseInt(splitedLine[2]);
                        receivedBytesVount = Integer.parseInt(splitedLine[3]);

                        CsvRow csvRow = new CsvRow(uri, requestCount, sendBytesCount, receivedBytesVount);
                        list.add(csvRow);
                    }
                }
            } catch (Exception e) {
            }

            return list;
        }
    }


}



