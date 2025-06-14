package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import server.httpUtils.HttpConstants;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public void fileHandlingFileIsFound(@TempDir Path tempDir) throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        HttpConstants.baseDir = tempDir.toString();
        createFile("apple", "apple");
        String validUrl = "http://localhost:4221/files/apple";
        URL goodUrl = new URI(validUrl).toURL();

        HttpURLConnection goodcon = openConnection(goodUrl);
        assertEquals(200, goodcon.getResponseCode());
        assertEquals("apple", readFromConnection(goodcon));
        assertEquals("HTTP/1.1 200 OK", goodcon.getHeaderField(0));
        assertEquals("application/octet-stream", goodcon.getHeaderField("Content-Type"));
        assertEquals("5", goodcon.getHeaderField("Content-Length"));
    }

    @Test
    public void fileHandlingPutFile(@TempDir Path tempDir) throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        HttpConstants.baseDir = tempDir.toString();
        String validUrl = "http://localhost:4221/files/apple";
        URL goodUrl = new URI(validUrl).toURL();

        HttpURLConnection con = (HttpURLConnection) goodUrl.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "text/plain");
        con.setRequestProperty("Content-Length", "5");
        con.setDoOutput(true);
        String body = "apple";
        // Actually write the body to the server
        try (OutputStream os = con.getOutputStream()) {
            os.write(body.getBytes());
            os.flush();
        }

        assertEquals(201, con.getResponseCode());
        assertEquals("Created", readFromConnection(con));
        assertEquals("HTTP/1.1 201 Created", con.getHeaderField(0));
        assertEquals("text/plain", con.getHeaderField("Content-Type"));
        assertEquals("7", con.getHeaderField("Content-Length"));
        String fileContents = readFile("apple");
        assertEquals("apple", fileContents);
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


    private void createFile(String fileName, String content) {
        try {
            // Combine base directory and filename
            Path dirPath = Paths.get(HttpConstants.baseDir);
            Path filePath = dirPath.resolve(fileName);

            // Ensure directory exists
            Files.createDirectories(dirPath);

            // Write content to file (creates or overwrites)
            Files.writeString(filePath, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFile(String fileName) {
        Path dirPath = Paths.get(HttpConstants.baseDir);
        Path filePath = dirPath.resolve(fileName);

        // Wait up to 1 second for the file to appear
        int maxAttempts = 1000;
        int delayMillis = 100;

        for (int i = 0; i < maxAttempts; i++) {
            if (Files.exists(filePath)) {
                try {
                    return Files.readString(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // If the file wasn't found after waiting
        return null;
    }

}
