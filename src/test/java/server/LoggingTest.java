package server;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("logging")
public class LoggingTest {
    @Test
    void logsRequestsToLogFile() throws Exception {
        // Assume log file is created at logs/access.log
        File logFile = new File("logs/access.log");
        long before = logFile.length();

        // Make a request
        new java.net.URL("http://localhost:8080/echo?msg=logtest").openConnection().getInputStream().close();
        Thread.sleep(100); // give time for logging

        long after = logFile.length();
        assertTrue(after > before, "Expected log file to grow");
    }
}