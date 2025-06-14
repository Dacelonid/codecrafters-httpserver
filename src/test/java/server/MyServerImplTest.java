package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyServerImplTest {

    private MyServerImpl myServer;
    private boolean local;

    @BeforeEach
    public void setup() {
        local = Boolean.parseBoolean(System.getenv("LOCAL"));
        int port = 4221;
        myServer = new MyServerImpl(port);
        Thread thread = new Thread(myServer);
        thread.start();
    }

    @AfterEach
    public void tearDown() {
        myServer.stop();
    }

    @Test
    public void noPathGet200Response() throws IOException, URISyntaxException {
        if(!local) {return;}
            URL url = new URI("http://localhost:4221").toURL();
            HttpURLConnection con = openConnection(url);

            String line = readFromConnection(con);
            assertEquals("OK", line);
    }

    @Test
    public void echoRest() throws IOException, URISyntaxException {
        if(!local) {return;}
            URL url = new URI("http://localhost:4221/echo/something").toURL();
            HttpURLConnection con = openConnection(url);

            String line = readFromConnection(con);
            assertEquals("something", line);
    }

    @Test
    public void validPathAndInvalidPathGet200And404Response() throws IOException, URISyntaxException {
        if(!local) {return;}
        String validUrl = "http://localhost:4221/index.html";
        String invalidUrl = "http://localhost:4221/gjhksdgaku.html";
        URL goodUrl = new URI(validUrl).toURL();
        URL badUrl = new URI(invalidUrl).toURL();

        HttpURLConnection goodcon = openConnection(goodUrl);
        assertEquals(200, goodcon.getResponseCode());
        assertEquals("OK", readFromConnection(goodcon));

        HttpURLConnection badcon = openConnection(badUrl);
        assertEquals(404, badcon.getResponseCode());
    }
//    @Test
    public void userAgentHandling() throws IOException, URISyntaxException {
        if(!local) {return;}
        String validUrl = "http://localhost:4221/user-agent";
        URL goodUrl = new URI(validUrl).toURL();

        HttpURLConnection goodcon = openConnection(goodUrl);
        assertEquals(200, goodcon.getResponseCode());
        assertEquals("foobar/1.2.3", readFromConnection(goodcon));

    }

    private static HttpURLConnection openConnection(URL goodUrl) throws IOException {
        HttpURLConnection con = (HttpURLConnection) goodUrl.openConnection();
        con.setRequestMethod("GET");

        con.setDoOutput(true);
        return con;
    }


    private String readFromConnection(HttpURLConnection con) throws IOException {
        return new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
    }
}
