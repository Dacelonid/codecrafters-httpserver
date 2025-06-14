package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
        if (!local) {
            return;
        }
        URL url = new URI("http://localhost:4221").toURL();
        HttpURLConnection con = openConnection(url);

        String line = readFromConnection(con);
        assertEquals("OK", line);
        assertEquals("HTTP/1.1 200 OK", con.getHeaderField(0));
        assertEquals("text/plain", con.getHeaderField("Content-Type"));
        assertEquals("2", con.getHeaderField("Content-Length"));
    }
    @Test
    public void unknownMethodGetForbidden() throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        URL url = new URI("http://localhost:4221").toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("TRACE"); //Valid HTTP Method, but not supported by our code
        con.setDoOutput(true);

        String line = readFromConnection(con);
        assertEquals("Not Allowed", line);
        assertEquals("HTTP/1.1 405 Not Allowed", con.getHeaderField(0));
        assertEquals("text/plain", con.getHeaderField("Content-Type"));
        assertEquals("11", con.getHeaderField("Content-Length"));
    }

    @Test
    public void echoRest() throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        URL url = new URI("http://localhost:4221/echo/something").toURL();
        HttpURLConnection con = openConnection(url);
        String line = readFromConnection(con);
        assertEquals("something", line);
        assertEquals("HTTP/1.1 200 OK", con.getHeaderField(0));
        assertEquals("text/plain", con.getHeaderField("Content-Type"));
        assertEquals("9", con.getHeaderField("Content-Length"));

    }

    @Test
    public void validPathAndInvalidPathGet200And404Response() throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        String validUrl = "http://localhost:4221/index.html";
        String invalidUrl = "http://localhost:4221/gjhksdgaku.html";
        URL goodUrl = new URI(validUrl).toURL();
        URL badUrl = new URI(invalidUrl).toURL();

        HttpURLConnection goodcon = openConnection(goodUrl);
        assertEquals(200, goodcon.getResponseCode());
        assertEquals("OK", readFromConnection(goodcon));
        assertEquals("text/plain", goodcon.getHeaderField("Content-Type"));
        assertEquals("2", goodcon.getHeaderField("Content-Length"));
        assertEquals("HTTP/1.1 200 OK", goodcon.getHeaderField(0));

        HttpURLConnection badcon = openConnection(badUrl);
        assertEquals(404, badcon.getResponseCode());
        assertEquals("Not Found", readFromConnection(badcon));
        assertEquals("HTTP/1.1 404 Not Found", badcon.getHeaderField(0));
        assertEquals("text/plain", badcon.getHeaderField("Content-Type"));
        assertEquals("9", badcon.getHeaderField("Content-Length"));
    }

    @Test
    public void userAgentHandling() throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        String validUrl = "http://localhost:4221/user-agent";
        URL goodUrl = new URI(validUrl).toURL();

        HttpURLConnection goodcon = openConnection(goodUrl);
        assertEquals(200, goodcon.getResponseCode());
        String actual = readFromConnection(goodcon);
        assertEquals("Java/24.0.1", actual);
        assertEquals("HTTP/1.1 200 OK", goodcon.getHeaderField(0));
        assertEquals("text/plain", goodcon.getHeaderField("Content-Type"));
        assertEquals("11", goodcon.getHeaderField("Content-Length"));

    }

    @Test
    public void fileHandlingFileNotFound() throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        String validUrl = "http://localhost:4221/files/banana";
        URL goodUrl = new URI(validUrl).toURL();

        HttpURLConnection goodcon = openConnection(goodUrl);
        assertEquals(404, goodcon.getResponseCode());
        assertEquals("Not Found", readFromConnection(goodcon));
        assertEquals("HTTP/1.1 404 Not Found", goodcon.getHeaderField(0));
        assertEquals("text/plain", goodcon.getHeaderField("Content-Type"));
        assertEquals("9", goodcon.getHeaderField("Content-Length"));
    }
    @Test
    public void fileHandlingFileIsFound() throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        String validUrl = "http://localhost:4221/files/apple";
        URL goodUrl = new URI(validUrl).toURL();

        HttpURLConnection goodcon = openConnection(goodUrl);
        assertEquals(200, goodcon.getResponseCode());
        assertEquals("apple", readFromConnection(goodcon));
        assertEquals("HTTP/1.1 200 OK", goodcon.getHeaderField(0));
        assertEquals("application/octet-stream", goodcon.getHeaderField("Content-Type"));
        assertEquals("5", goodcon.getHeaderField("Content-Length"));
    }

    private static HttpURLConnection openConnection(URL goodUrl) throws IOException {
        HttpURLConnection con = (HttpURLConnection) goodUrl.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(true);
        return con;
    }


    private String readFromConnection(HttpURLConnection con) throws IOException {
        InputStream stream;
        int responseCode = con.getResponseCode();
        if (responseCode >= 400) {
            stream = con.getErrorStream();
        } else {
            stream = con.getInputStream();
        }

        if (stream == null) {
            return ""; // or throw exception
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString().trim();
        }
    }
}
