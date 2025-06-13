import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
     try(ServerSocket serverSocket = new ServerSocket(4221)) {
       serverSocket.setReuseAddress(true);

       Socket accept = serverSocket.accept();
       OutputStream outputStream = accept.getOutputStream();
       outputStream.write("Hello World".getBytes());
       System.out.println("accepted new connection");



     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}
