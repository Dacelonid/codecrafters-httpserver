package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static server.HttpResponse.REQUEST_SEPARATOR;

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
                    if ((line = reader.readLine()) != null){
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
        if (request.getTarget().equals("/") || request.getTarget().equals("/index.html")) {
            return new HttpResponse(HttpCodes.OK, "OK", "text/plain");
        } else if (request.getTarget().startsWith("/echo")) {
            String path = request.getTarget().substring("/echo/".length());

            return new HttpResponse(HttpCodes.OK, path, "text/plain");
        }
        return new HttpResponse(HttpCodes.NOT_FOUND, "Not Found", "Content-Type: text/plain");
    }


    private byte[] buildResponse(HttpResponse response) {
        return response.toString().getBytes();
    }
}
