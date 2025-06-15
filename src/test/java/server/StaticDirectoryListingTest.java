package server;

// 3. Static Directory Listing Test
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("dirlist")
public class StaticDirectoryListingTest {
    @Test
    void listsDirectoryContentsAsHtml() throws Exception {
        URL url = new URL("http://localhost:8080/files/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
        String response = scanner.hasNext() ? scanner.next() : "";

        assertTrue(response.contains("<html") || response.contains(".txt"), "Should contain file list");
    }
}