package server.httpstructure;

import java.util.Arrays;

public class HttpRequest {
    private final RequestLine requestLine;
    private final HttpHeader httpHeader;
    private final String body;

    private HttpRequest(RequestLine requestLine, HttpHeader httpHeader, String body) {
        this.requestLine = requestLine;
        this.httpHeader = httpHeader;
        this.body = body;
    }


    public String getTarget() {
        return requestLine.getTarget();
    }


    public HttpHeader getHttpHeader() {
        return httpHeader;
    }

    public static HttpRequest from(String request) {
        String[] parts = request.split("\\r\\n");
        RequestLine requestLine = new RequestLine(parts[0]);

        String[] headers = Arrays.copyOfRange(parts, 1, parts.length);
        HttpHeader httpHeader = HttpHeader.build(headers);

        String body = String.join("\r\n", Arrays.copyOfRange(parts, headers.length + 1, parts.length));

        return new HttpRequest(requestLine, httpHeader, body);
    }
}
