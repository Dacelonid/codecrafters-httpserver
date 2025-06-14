package server;

import server.handlers.*;
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
                String rawRequest = String.join("\r\n", requestLines) + "\r\n\r\n";
                HttpResponse response = handleLine(rawRequest);
                outputStream.write(response.getBytes());
                outputStream.flush();
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }


    private static List<String> readLines(BufferedReader reader) throws IOException {
        List<String> requestLines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            requestLines.add(line);
        }
        return requestLines;
    }

    private HttpResponse handleLine(String s) {
        HttpRequest request = HttpRequest.from(s);
        Handler handler = chooseHandler(request);
        return handler.handle(request);
    }

    private static Handler chooseHandler(HttpRequest request) {
        String command = getCommand(request);
        return switch (command) {
            case "/", "/index.html" -> new BasicOKHandler();
            case "echo" -> new EchoHandler();
            case "user-agent" -> new UserAgentHandler();
            default -> new NotFoundHandler();
        };


    }
    public static String getCommand(HttpRequest request) {
        String target = request.getTarget();
        if(target.contains("/echo/"))
            return "echo";
        if(target.contains("/user-agent"))
            return "user-agent";
        return target;
    }

}
