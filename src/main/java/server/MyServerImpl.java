package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServerImpl implements Runnable {
    public static final String REQUEST_SEPARATOR = "\r\n";
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
                        HttpCodes responseCode = handleLine(line);
                    outputStream.write(buildResponse("Hello World", responseCode));
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

    private HttpCodes handleLine(String s) {
        HttpRequest request = HttpRequest.parse(s);
        if (request.getTarget().equals("/") || request.getTarget().equals("/index.html")) {
            return HttpCodes.OK;
        }
        return HttpCodes.NOT_FOUND;
    }


    private byte[] buildResponse(String body, HttpCodes responseCode) {
        return ("HTTP/1.1 " + responseCode.getCode() + " " + responseCode.getMessage() + REQUEST_SEPARATOR +
                "Content-Length: " + body.length() + REQUEST_SEPARATOR +
                REQUEST_SEPARATOR + body).getBytes();
    }
}
