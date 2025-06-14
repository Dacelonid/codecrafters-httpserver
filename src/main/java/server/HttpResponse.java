package server;

public class HttpResponse {
    public static final String REQUEST_SEPARATOR = "\r\n";
    private final HttpCodes responseCode;
    private final String body;
    private final String header;
    private final String version = "HTTP/1.1";

    public HttpResponse(HttpCodes responseCode, String body, String header) {
        this.responseCode = responseCode;
        this.body = body;
        this.header = "Content-type: " + header + REQUEST_SEPARATOR + "Content-length:" + body.length();
    }

    public HttpCodes getResponseCode() {
        return responseCode;
    }

    public String getBody() {
        return body;
    }

    public String getHeader() {
        return header;
    }

    public String getVersion() {
        return version;
    }

    public String toString() {
        return version + " " + responseCode.getCode() + " " + responseCode.getMessage()
                + REQUEST_SEPARATOR + header + REQUEST_SEPARATOR + REQUEST_SEPARATOR + body;
    }
}
