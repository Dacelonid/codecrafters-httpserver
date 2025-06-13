import server.MyServerImpl;

public class Main {
    public static void main(String[] args) {
        int port = 4221;
        Thread thread = new Thread(new MyServerImpl(port));
        thread.start();
    }
}
