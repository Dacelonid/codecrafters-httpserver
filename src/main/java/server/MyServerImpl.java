package server;

import server.handlers.EchoHandler;
import server.handlers.Handler;
import server.handlers.NotFoundHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServerImpl implements Runnable {
    private final int port;
    private ServerSocket serverSocket;
    private boolean keepRunning = true;

    public void stop() {
        this.keepRunning = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close(); // This will unblock accept()
            } catch (IOException e) {
                System.out.println("Error closing server socket: " + e.getMessage());
            }
        }
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
                try (Socket accept = serverSocket.accept();
                     OutputStream outputStream = accept.getOutputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()))) {
                    String line;
                    if ((line = reader.readLine()) != null) {
                        HttpResponse response = handleLine(line);
                        outputStream.write(buildResponse(response));
                        outputStream.flush();
                    }
                } catch (IOException e) {
                    if (keepRunning) //only log exception if we get an IO exception while running
                        System.out.println("We should still be running, but there was an issue" + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private HttpResponse handleLine(String s) {
        HttpRequest request = HttpRequest.parse(s);
        Handler handler = chooseHandler(request);
        return handler.handle(request);
    }

    private static Handler chooseHandler(HttpRequest request) {
        String command = request.getCommand();
        return switch (command) {
            case "/", "/index.html" -> new BasicOKHandler();
            case "echo" -> new EchoHandler();
            default -> new NotFoundHandler();
        };
    }


    private byte[] buildResponse(HttpResponse response) {
        return response.toString().getBytes();
    }
}
