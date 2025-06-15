package server.httpstructure.httprequest;

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


    public RequestLine getRequestLine() {
        return requestLine;
    }

    public String getTarget() {
        return requestLine.getTarget();
    }


    public HttpHeader getHttpHeader() {
        return httpHeader;
    }

    public static HttpRequest from(String request) {
        // Split into headers and body using the first occurrence of \r\n\r\n
        String[] headerAndBody = request.split("\r\n\r\n", 2);
        String headerSection = headerAndBody[0];
        String body = headerAndBody.length > 1 ? headerAndBody[1] : "";

        // Split the header section into individual lines
        String[] lines = headerSection.split("\r\n");
        if(lines.length < 2) return new HttpRequest(null, null, null);
        RequestLine requestLine = new RequestLine(lines[0]);

        // Extract headers (excluding the request line)
        String[] headerLines = Arrays.copyOfRange(lines, 1, lines.length);
        HttpHeader httpHeader = HttpHeader.build(headerLines);

        return new HttpRequest(requestLine, httpHeader, body);
    }


    public String getBody() {
        return body;
    }
}
