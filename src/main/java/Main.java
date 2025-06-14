import server.MyServerImpl;
import server.httpUtils.HttpConstants;

public class Main {
    public static void main(String[] args) {
        String directory = null;

        for (int i = 0; i < args.length; i++) {
            if ("--directory".equals(args[i]) && i + 1 < args.length) {
                directory = args[i + 1];
                break;
            }
        }
        if (directory != null) {
            HttpConstants.baseDir = directory;
        }
        int port = 4221;
        Thread thread = new Thread(new MyServerImpl(port));
        thread.start();
    }
}
