package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import server.httpUtils.HttpConstants;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        ConnectionBuilder builder = new ConnectionBuilder("http://localhost:4221", "GET");

        HttpURLConnection con = builder.build();

        String line = readFromConnection(con);
        ExpectedResponse.builder()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .contentLength("2")
                .body("OK")
                .contentType("text/plain").build().assertMatches(con, line);
    }

    @Test
    public void unknownMethodGetForbidden() throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        ConnectionBuilder builder = new ConnectionBuilder("http://localhost:4221", "TRACE");

        HttpURLConnection con = builder.build();
        con.setDoOutput(true);

        String line = readFromConnection(con);

        ExpectedResponse.builder()
                .statusCode(405)
                .statusLine("HTTP/1.1 405 Not Allowed")
                .contentLength("11")
                .body("Not Allowed")
                .contentType("text/plain").build().assertMatches(con, line);

    }

    @Test
    public void echotest() throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        HttpURLConnection con = new ConnectionBuilder("http://localhost:4221/echo/something", "GET").build();
        String line = readFromConnection(con);

        ExpectedResponse.builder()
                .statusCode(200)
                .body("something")
                .statusLine("HTTP/1.1 200 OK")
                .contentLength("9")
                .contentType("text/plain")
                .build().assertMatches(con, line);
    }

    @Test
    public void echotestCompressed() throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        HttpURLConnection con = new ConnectionBuilder("http://localhost:4221/echo/something", "GET").doOutput(true).acceptEncoding("gzip").build();

        String line = readFromConnection(con);

        ExpectedResponse.builder()
                .statusCode(200)
                .body("something")
                .statusLine("HTTP/1.1 200 OK")
                .contentEncoding("text/plain")
                .contentLength("29")
                .contentType("text/plain")
                .contentEncoding("gzip")
                .build()
                .assertMatches(con, line);
    }

    @Test
    public void echotestCompressedMultipleAccept() throws IOException, URISyntaxException {
        if (!local) {
            return;
        }


        HttpURLConnection con = new ConnectionBuilder("http://localhost:4221/echo/something", "GET").doOutput(true).acceptEncoding("encoding1, gzip, encoding2,  encoding3").build();
        String line = readFromConnection(con);
        ExpectedResponse.builder()
                .statusCode(200)
                .body("something")
                .statusLine("HTTP/1.1 200 OK")
                .contentLength("29")
                .contentType("text/plain")
                .contentEncoding("gzip")
                .build().assertMatches(con, line);
    }

    @Test
    public void echotestCompressedMultipleInvalidAccept() throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        HttpURLConnection con = new ConnectionBuilder("http://localhost:4221/echo/something", "GET").doOutput(true).acceptEncoding("encoding1, encoding2, encoding3").build();

        String line = readFromConnection(con);

        ExpectedResponse.builder()
                .statusCode(200)
                .body("something")
                .statusLine("HTTP/1.1 200 OK")
                .contentLength("9")
                .contentType("text/plain")
                .contentEncoding(null) //explicit in the CodeCrafters testing
                .build()
                .assertMatches(con, line);
    }

    @Test
    public void echotestUnknownEncoding() throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        HttpURLConnection con = new ConnectionBuilder("http://localhost:4221/echo/something", "GET").doOutput(true).acceptEncoding("invalid-encoding").build();
        String line = readFromConnection(con);
        ExpectedResponse.builder()
                .statusCode(200)
                .body("something")
                .statusLine("HTTP/1.1 200 OK")
                .contentLength("9")
                .contentType("text/plain")
                .contentEncoding(null)
                .build().assertMatches(con, line);
    }

    @Test
    public void validPathAndInvalidPathGet200And404Response() throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        HttpURLConnection goodcon = new ConnectionBuilder("http://localhost:4221/index.html", "GET").build();
        String line = readFromConnection(goodcon);
        ExpectedResponse.builder()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .contentLength("2")
                .body("OK")
                .contentType("text/plain")
                .build()
                .assertMatches(goodcon, line);

        HttpURLConnection badcon = new ConnectionBuilder("http://localhost:4221/gjhksdgaku.html", "GET").build();
        line = readFromConnection(badcon);
        ExpectedResponse.builder()
                .statusCode(404)
                .statusLine("HTTP/1.1 404 Not Found")
                .contentLength("9")
                .body("Not Found")
                .contentType("text/plain")
                .build()
                .assertMatches(badcon, line);
    }

    @Test
    public void userAgentHandling() throws IOException, URISyntaxException {
        if (!local) {
            return;
        }

        HttpURLConnection goodcon = new ConnectionBuilder("http://localhost:4221/user-agent", "GET").build();
        assertEquals(200, goodcon.getResponseCode());
        String actual = readFromConnection(goodcon);
        ExpectedResponse.builder()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .contentLength("11")
                .contentType("text/plain")
                .body("Java/24.0.1")
                .build()
                .assertMatches(goodcon, actual);
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

        HttpURLConnection goodcon = new ConnectionBuilder("http://localhost:4221/files/banana", "GET").build();
        ExpectedResponse.builder()
                .statusCode(404)
                .statusLine("HTTP/1.1 404 Not Found")
                .contentLength("9")
                .body("Not Found")
                .contentType("text/plain")
                .build().assertMatches(goodcon, readFromConnection(goodcon));
    }

    @Test
    public void fileHandlingFileIsFound(@TempDir Path tempDir) throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        HttpConstants.baseDir = tempDir.toString();
        createFile("apple", "apple");

        HttpURLConnection goodcon = new ConnectionBuilder("http://localhost:4221/files/apple", "GET").build();
        ExpectedResponse.builder()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .contentLength("5")
                .body("apple")
                .contentType("application/octet-stream")
                .build().assertMatches(goodcon, readFromConnection(goodcon));
    }

    @Test
    public void fileHandlingPostFile(@TempDir Path tempDir) throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        HttpConstants.baseDir = tempDir.toString();

        HttpURLConnection con = new ConnectionBuilder("http://localhost:4221/files/apple", "POST").doOutput(true).contentType("text/plain").contentLength(5).build();

        String body = "apple";
        // Actually write the body to the server
        try (OutputStream os = con.getOutputStream()) {
            os.write(body.getBytes());
            os.flush();
        }

        ExpectedResponse.builder()
                .statusCode(201)
                .statusLine("HTTP/1.1 201 Created")
                .contentLength("7")
                .body("Created")
                .contentType("text/plain")
                .build().assertMatches(con, readFromConnection(con));

        String fileContents = readFile("apple");
        assertEquals("apple", fileContents);
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
            return "";
        }

        // Check for gzip encoding
        String encoding = con.getHeaderField("Content-Encoding");
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
            stream = new GZIPInputStream(stream);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n"); // optional: preserve line breaks
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

            // Write content to the file (creates or overwrites)
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


    static class ConnectionBuilder {
        private final URL url;
        private final String method;
        private boolean output;
        private String encoding;
        private String contentType;
        private int length;

        ConnectionBuilder(String url, String method) throws URISyntaxException, MalformedURLException {
            this.url = new URI(url).toURL();
            this.method = method;
        }


        HttpURLConnection build() throws IOException {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);
            con.setDoOutput(output);
            if (encoding != null) con.setRequestProperty("Accept-Encoding", encoding);
            if (contentType != null) con.setRequestProperty("Content-Type", contentType);
            if (length > 0) con.setRequestProperty("Content-Length", Integer.toString(length));
            return con;
        }

        public ConnectionBuilder doOutput(boolean output) {
            this.output = output;
            return this;
        }

        public ConnectionBuilder acceptEncoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        public ConnectionBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public ConnectionBuilder contentLength(int length) {
            this.length = length;
            return this;
        }
    }
}
