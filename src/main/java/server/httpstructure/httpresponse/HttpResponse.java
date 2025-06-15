package server.httpstructure.httpresponse;

import server.httpUtils.HttpCodes;

import static server.httpUtils.HttpConstants.*;

public class HttpResponse {


    private final HttpStatus statusLine;
    private final String body;
    private final EntityHeaders entityHeaders;

    private HttpResponse(HttpCodes responseCode, String contentType, String contentEncoding, String body) {
        this.statusLine = new HttpStatus(HTTP_VERSION, responseCode);
        this.entityHeaders = new EntityHeaders(contentType, contentEncoding, body.length());
        this.body = body;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public String toString() {
        return statusLine + REQUEST_SEPARATOR + entityHeaders + BLANK_LINE + body;
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }

    public static class HttpResponseBuilder {
        private HttpCodes responseCode;
        private String body;
        private String contentType = "text/plain";
        private String contentEncoding;

        public HttpResponse build() {
            return new HttpResponse(responseCode, contentType,contentEncoding, body);
        }
        public HttpResponseBuilder responseCode(HttpCodes responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public HttpResponseBuilder body(String body) {
            this.body = body;
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


    }
}
