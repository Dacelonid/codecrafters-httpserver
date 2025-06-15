package server;

import server.handlers.GetHandler;
import server.handlers.MethodNotAllowedHandler;
import server.handlers.PostHandler;
import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.httpresponse.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static server.httpUtils.HttpConstants.CONTENT_LENGTH;


public class MyServerImpl implements Runnable {
    private final int port;
    private ServerSocket serverSocket;
    private boolean keepRunning = true;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public void stop() {
        this.keepRunning = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close(); // This will unblock accept()
            } catch (IOException e) {
                System.out.println("Error closing server socket: " + e.getMessage());
            }
        }
        threadPool.shutdown();
    }

    public MyServerImpl(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSock = new ServerSocket(port)) {
            this.serverSocket = serverSock; // assign to field
            serverSocket.setReuseAddress(true);
            while (keepRunning) {
                try {
                    Socket accept = serverSocket.accept();
                    threadPool.execute(() -> handleClient(accept));
                } catch (IOException e) {
                    if (keepRunning)
                        System.out.println("We should still be running, but there was an issue" + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (Socket socket = clientSocket;
             OutputStream outputStream = socket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            List<String> requestLines = readLines(reader);
            if (!requestLines.isEmpty()) {
                String rawRequest = String.join("\r\n", requestLines);
                HttpResponse response = handleLine(rawRequest);
                outputStream.write(response.getBytes());
                outputStream.flush();
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }

    private static List<String> readLines(BufferedReader reader) throws IOException {
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        int contentLength = 0;

        // Read headers and parse Content-Length
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            requestBuilder.append(line).append("\r\n");
            if (line.toLowerCase().startsWith(CONTENT_LENGTH.toLowerCase())) {
                try {
                    contentLength = Integer.parseInt(line.substring(CONTENT_LENGTH.length()).trim());
                } catch (NumberFormatException ignored) {
                }
            }
        }

        // End of headers
        requestBuilder.append("\r\n");

        // Read body if present
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            int totalRead = 0;
            while (totalRead < contentLength) {
                int read = reader.read(bodyChars, totalRead, contentLength - totalRead);
                if (read == -1) break;
                totalRead += read;
            }
            requestBuilder.append(bodyChars, 0, totalRead);
        }

        return List.of(requestBuilder.toString());
    }

    private HttpResponse handleLine(String requestString) {
        HttpRequest request = HttpRequest.from(requestString);
        var requestLine = request.getRequestLine();
        if (requestLine == null) {
            return new MethodNotAllowedHandler().handle(request);
        }

        return switch (requestLine.getMethod()) {
            case GET -> new GetHandler().chooseHandler(request).handle(request);
            case POST -> new PostHandler().chooseHandler(request).handle(request);
            default -> new MethodNotAllowedHandler().handle(request);
        };
    }


}
