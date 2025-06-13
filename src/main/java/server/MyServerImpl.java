package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServerImpl implements Runnable {
    private final int port;

    public MyServerImpl(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            Socket accept = serverSocket.accept();
            OutputStream outputStream = accept.getOutputStream();

            outputStream.write(buildResponse("Hello World", 200));
            outputStream.flush();
            outputStream.close();
            accept.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }


    private byte[] buildResponse(String body, int responseCode) {
        return ("HTTP/1.1 " + responseCode + " OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" + body).getBytes();
    }
}
