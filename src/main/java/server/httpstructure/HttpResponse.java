package server.httpstructure;

import server.httpUtils.HttpCodes;

import static server.httpUtils.HttpConstants.*;

public class HttpResponse {


    private final HttpStatus statusLine;
    private final String header;
    private final String body;

    public HttpResponse(HttpCodes responseCode, String header, String body) {
        this.statusLine = new HttpStatus(HTTP_VERSION, responseCode);
        this.header = CONTENT_TYPE + header + REQUEST_SEPARATOR + CONTENT_LENGTH + body.length();
        this.body = body;
    }

    public String toString() {
        return statusLine + REQUEST_SEPARATOR + header + BLANK_LINE + body;
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }
}
