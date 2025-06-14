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
                .connection("keep-alive")
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
                .connection("keep-alive")
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
                .connection("keep-alive")
                .build().assertMatches(con, line);
    }

    @Test
    public void echotestConnectionClose() throws IOException, URISyntaxException {
        if (!local) {
            return;
        }
        HttpURLConnection con = new ConnectionBuilder("http://localhost:4221/echo/something", "GET").connection("close").build();
        String line = readFromConnection(con);

        ExpectedResponse.builder()
                .statusCode(200)
                .body("something")
                .statusLine("HTTP/1.1 200 OK")
                .contentLength("9")
                .contentType("text/plain")
                .connection("close")
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
                .connection("keep-alive")
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
                .connection("keep-alive")
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
                .connection("keep-alive")
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
                .connection("keep-alive")
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
                .connection("keep-alive")
                .build()
                .assertMatches(goodcon, line);

        HttpURLConnection badcon = new ConnectionBuilder("http://localhost:4221/gjhksdgaku.html", "GET").build();
        line = readFromConnection(badcon);
        ExpectedResponse.builder()
                .statusCode(404)
                .statusLine("HTTP/1.1 404 Not Found")
                .contentLength("9")
                .connection("keep-alive")
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
                .connection("keep-alive")
                .body("Java/24.0.1")
                .build()
                .assertMatches(goodcon, actual);
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
                .connection("keep-alive")
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
                .connection("keep-alive")
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
                .connection("keep-alive")
                .statusLine("HTTP/1.1 201 Created")
                .contentLength("7")
                .body("Created")
                .contentType("text/plain")
                .build().assertMatches(con, readFromConnection(con));

        String fileContents = readFile("apple");
        assertEquals("apple", fileContents);
    }

    @Test
    public void fileDownloadWithGzipEncoding(@TempDir Path tempDir) throws IOException, URISyntaxException {
        if (!local) return;

        HttpConstants.baseDir = tempDir.toString();
        createFile("gzip.txt", "compressme");

        HttpURLConnection con = new ConnectionBuilder("http://localhost:4221/files/gzip.txt", "GET")
                .acceptEncoding("gzip").build();
        String line = readFromConnection(con);

        ExpectedResponse.builder()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .contentLength("30") // Should match actual gzip size of "compressme"
                .contentType("text/plain")
                .contentEncoding("gzip")
                .connection("keep-alive")
                .body("compressme")
                .build().assertMatches(con, line);
    }

    @Test
    public void echoWithConnectionCloseHeader() throws IOException, URISyntaxException {
        if (!local) return;

        HttpURLConnection con = new ConnectionBuilder("http://localhost:4221/echo/test", "GET")
                .connection("close").build();
        String line = readFromConnection(con);

        ExpectedResponse.builder()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .contentLength("4")
                .contentType("text/plain")
                .connection("close")
                .body("test")
                .build().assertMatches(con, line);
    }



    @Test
    public void filePostWithConnectionClose(@TempDir Path tempDir) throws IOException, URISyntaxException {
        if (!local) return;

        HttpConstants.baseDir = tempDir.toString();
        HttpURLConnection con = new ConnectionBuilder("http://localhost:4221/files/closed", "POST")
                .doOutput(true)
                .contentType("text/plain")
                .contentLength(5)
                .connection("close")
                .build();

        try (OutputStream os = con.getOutputStream()) {
            os.write("data!".getBytes());
        }

        ExpectedResponse.builder()
                .statusCode(201)
                .statusLine("HTTP/1.1 201 Created")
                .contentLength("7")
                .body("Created")
                .contentType("text/plain")
                .connection("close")
                .build().assertMatches(con, readFromConnection(con));

        assertEquals("data!", readFile("closed"));
    }

    @Test
    public void userAgentWithGzip() throws IOException, URISyntaxException {
        if (!local) return;

        HttpURLConnection con = new ConnectionBuilder("http://localhost:4221/user-agent", "GET")
                .acceptEncoding("gzip")
                .build();

        String line = readFromConnection(con);
        ExpectedResponse.builder()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .contentType("text/plain")
                .connection("keep-alive")
                .contentEncoding("gzip")
                .body("Java/24.0.1")
                .contentLength("31")
                .build().assertMatches(con, line);
    }

    @Test
    public void fileDownloadWithInvalidGzipInMiddle(@TempDir Path tempDir) throws IOException, URISyntaxException {
        if (!local) return;

        HttpConstants.baseDir = tempDir.toString();
        createFile("weird.gz", "stillplain");

        HttpURLConnection con = new ConnectionBuilder("http://localhost:4221/files/weird.gz", "GET")
                .acceptEncoding("identity, g-zip, br")
                .build();

        String line = readFromConnection(con);
        ExpectedResponse.builder()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .contentType("application/octet-stream")
                .contentLength("10")
                .connection("keep-alive")
                .body("stillplain")
                .contentEncoding(null)
                .build().assertMatches(con, line);
    }

    @Test
    public void filePathTraversalAttempt(@TempDir Path tempDir) throws IOException, URISyntaxException {
        if (!local) return;

        HttpConstants.baseDir = tempDir.toString();
        createFile("safe.txt", "nope");

        HttpURLConnection con = new ConnectionBuilder("http://localhost:4221/files/../safe.txt", "GET").build();
        assertEquals(404, con.getResponseCode(), "Should not allow path traversal");
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
        private String connection = "keep-alive";

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
            con.setRequestProperty("Connection", connection);
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

        public ConnectionBuilder connection(String connection) {
            this.connection = connection;
            return this;
        }
    }
}
