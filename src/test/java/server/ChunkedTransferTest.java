package server;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import static org.junit.jupiter.api.Assertions.*;

@Tag("chunked")
public class ChunkedTransferTest {
    @Test
    void responseIsChunked() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL("http://localhost:8080/stream").openConnection();
        conn.setRequestMethod("GET");
        assertEquals(200, conn.getResponseCode());

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = reader.readLine();
        assertNotNull(line);
        assertFalse(line.isEmpty(), "Expected chunked data to begin arriving");
    }
}
