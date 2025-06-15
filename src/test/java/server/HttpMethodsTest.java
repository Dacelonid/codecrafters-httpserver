package server;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.net.HttpURLConnection;
import java.net.URL;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("methods")
public class HttpMethodsTest {
    @Test
    void supportsPutAndDelete() throws Exception {
        URL url = new URL("http://localhost:8080/files/test.txt");

        HttpURLConnection putConn = (HttpURLConnection) url.openConnection();
        putConn.setDoOutput(true);
        putConn.setRequestMethod("PUT");
        putConn.getOutputStream().write("data".getBytes());
        assertEquals(201, putConn.getResponseCode());

        HttpURLConnection delConn = (HttpURLConnection) url.openConnection();
        delConn.setRequestMethod("DELETE");
        assertEquals(200, delConn.getResponseCode());
    }
}
