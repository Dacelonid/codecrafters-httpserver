package server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Objects;

public class ExpectedResponse {
    private final int statusCode;
    private final String body;
    private final String statusLine;
    private final String contentType;
    private final String contentLength;
    private final String contentEncoding;

    private ExpectedResponse(Builder builder) {
        this.statusCode = builder.statusCode;
        this.body = builder.body;
        this.statusLine = builder.statusLine;
        this.contentType = builder.contentType;
        this.contentLength = builder.contentLength;
        this.contentEncoding = builder.contentEncoding;
    }

    public void assertMatches(HttpURLConnection con, String actualBody) throws IOException {
        assertEquals(statusCode, con.getResponseCode(), "Unexpected status code");
        assertEquals(body, actualBody, "Unexpected body content");
        assertEquals(statusLine, con.getHeaderField(0), "Unexpected status line");
        assertEquals(contentType, con.getHeaderField("Content-Type"), "Unexpected Content-Type");
        assertEquals(contentLength, con.getHeaderField("Content-Length"), "Unexpected Content-Length");

        if (contentEncoding == null) {
            assertNull(con.getHeaderField("Content-Encoding"), "Expected no Content-Encoding header");
        } else {
            assertEquals(contentEncoding, con.getHeaderField("Content-Encoding"), "Unexpected Content-Encoding");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int statusCode;
        private String body = "";
        private String statusLine;
        private String contentType;
        private String contentLength;
        private String contentEncoding;

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder statusLine(String statusLine) {
            this.statusLine = statusLine;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder contentLength(String contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        public Builder contentEncoding(String contentEncoding) {
            this.contentEncoding = contentEncoding;
            return this;
        }

        public ExpectedResponse build() {
            return new ExpectedResponse(this);
        }
    }

    // Optional: equals/hashCode for test reuse
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpectedResponse that)) return false;
        return statusCode == that.statusCode &&
                Objects.equals(body, that.body) &&
                Objects.equals(statusLine, that.statusLine) &&
                Objects.equals(contentType, that.contentType) &&
                Objects.equals(contentLength, that.contentLength) &&
                Objects.equals(contentEncoding, that.contentEncoding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, body, statusLine, contentType, contentLength, contentEncoding);
    }
}
