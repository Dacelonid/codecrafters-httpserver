package server.httpstructure.httpresponse;

import server.httpUtils.HttpCodes;

import java.nio.charset.StandardCharsets;

import static server.httpUtils.HttpConstants.*;

public class HttpResponse {


    private final HttpStatus statusLine;
    private final byte[] body;
    private final EntityHeaders entityHeaders;

    private final String connection;

    private HttpResponse(HttpCodes responseCode, String contentType, String contentEncoding, byte[] body, String connection) {
        this.statusLine = new HttpStatus(HTTP_VERSION, responseCode);
        this.connection = connection;
        this.entityHeaders = new EntityHeaders(contentType, contentEncoding, body.length);
        this.body = body;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }
    @Override
    public String toString() {
        // Only return headers as string — useful for debugging
        return statusLine + REQUEST_SEPARATOR + "Connection: " + connection + REQUEST_SEPARATOR + entityHeaders + BLANK_LINE;
    }

    public byte[] getBytes() {
        // Properly assemble headers + binary body
        byte[] headerBytes = toString().getBytes(StandardCharsets.US_ASCII);
        byte[] response = new byte[headerBytes.length + body.length];

        System.arraycopy(headerBytes, 0, response, 0, headerBytes.length);
        System.arraycopy(body, 0, response, headerBytes.length, body.length);

        return response;
    }

    public static class HttpResponseBuilder {
        private HttpCodes responseCode;
        private byte[] body;
        private String contentType = "text/plain";
        private String contentEncoding;
        private String connection = "Keep-Alive";

        public HttpResponse build() {
            return new HttpResponse(responseCode, contentType, contentEncoding, body, connection);
        }

        public HttpResponseBuilder responseCode(HttpCodes responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public HttpResponseBuilder body(String text) {
            this.body = text.getBytes(StandardCharsets.UTF_8);
            return this;
        }

        public HttpResponseBuilder body(byte[] data) {
            this.body = data;
            return this;
        }

        public HttpResponseBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;

        }

        public HttpResponseBuilder contentEncoding(String contentEncoding) {
            this.contentEncoding = contentEncoding;
            return this;
        }

        public HttpResponseBuilder connection(String connection){
            this.connection = connection;
            return this;
        }


    }
}
