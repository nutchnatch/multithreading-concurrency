package httpserver.optimizer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyThroughputHttpServer {

    private static final String INPUT_FILE = "resources/war_and_peace.txt";
    /**
     * Increase this value inorder to compare the performance of the server
     */
    private static int NUMBER_OF_THREADS = 8;
    public static void main(String[] args) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(INPUT_FILE)));
        startServer(text);
    }

    private static void startServer(String text) throws IOException {
        /**
         * Receives ip address and a backlog size which defines the size of the queue for http server
         */
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/search", new WordCountHandler(text));
        /**
         * Schedule each incoming http request to a pool of worker threads
         */
        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        server.setExecutor(executor);
        /**
         * Once started, our application starts listening for http calls on port 8000
         * Each request will be handled by WordCountHandler and executed by the fixed thread pool
         */
        server.start();
    }

    private static class WordCountHandler implements HttpHandler {

        private String text;
        public WordCountHandler(String text) {
            this.text = text;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String query = httpExchange.getRequestURI().getQuery();
            String[] keyValue = query.split("=");

            String action = keyValue[0];
            String word = keyValue[1];
            if(!action.equals("word")) {
                httpExchange.sendResponseHeaders(400, 0);
                return;
            }

            long count = countWord(word);
            final byte[] response = Long.toString(count).getBytes();
            httpExchange.sendResponseHeaders(200, response.length);
            final OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(response);
            outputStream.close();
        }

        private long countWord(String word) {
            long count = 0;
            int index = 0;
            while(index >= 0) {
                index = text.indexOf(word, index);
                if(index >= 0) {
                    count ++;
                    index ++;
                }
            }
            return count;
        }
    }
}
