package server;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyServerImplTest {
    @Test
    public void testRun() throws IOException, URISyntaxException {
        int port = 4221;
        Thread thread = new Thread(new MyServerImpl(port));
        thread.start();

        URL url = new URI("http://localhost:4221").toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(true);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line = in.readLine();
        assertEquals("Hello World", line);
    }

}
