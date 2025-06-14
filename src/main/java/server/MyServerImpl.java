package server;

import server.handlers.*;
import server.httpUtils.HttpMethod;
import server.httpstructure.HttpRequest;
import server.httpstructure.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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

        // Read headers
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            requestBuilder.append(line).append("\r\n");
        }

        // End of headers
        requestBuilder.append("\r\n");

        // Look for Content-Length to determine how much body to read
        int contentLength = 0;
        for (String headerLine : requestBuilder.toString().split("\r\n")) {
            if (headerLine.toLowerCase().startsWith("content-length:")) {
                try {
                    contentLength = Integer.parseInt(headerLine.substring("content-length:".length()).trim());
                } catch (NumberFormatException ignored) {}
                break;
            }
        }

        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            int totalRead = 0;
            while (totalRead < contentLength) {
                int read = reader.read(bodyChars, totalRead, contentLength - totalRead);
                if (read == -1) break; // EOF
                totalRead += read;
            }
            requestBuilder.append(new String(bodyChars, 0, totalRead));
        }

        return List.of(requestBuilder.toString());
    }

    private HttpResponse handleLine(String s) {
        HttpRequest request = HttpRequest.from(s);
        if(request.getRequestLine().getMethod() == HttpMethod.GET) {
            GetHandler getHandler = new GetHandler();
            Handler handler = getHandler.chooseHandler(request);
            return handler.handle(request);
        }else if(request.getRequestLine().getMethod() == HttpMethod.POST){
            PostHandler postHandler = new PostHandler();
            Handler handler = postHandler.chooseHandler(request);
            return handler.handle(request);
        }
        return new MethodNotAllowedHandler().handle(request);
    }





}
