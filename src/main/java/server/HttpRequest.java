package server;

import java.util.Arrays;

public class HttpRequest {
    public HttpMethod getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public String getVersion() {
        return version;
    }

    private HttpMethod method;
    private String target;
    private String version;

    private HttpRequest(HttpMethod method, String target, String version) {
        this.method = method;
        this.target = target;
        this.version = version;
    }

    public static HttpRequest parse(String request) {
        System.out.println("request - " + request);
        String[] parts = request.split("\\r\\n");
        String requestLine = parts[0];
        String[] requestLineParts = requestLine.split(" ");
        return new HttpRequest(HttpMethod.valueOf(requestLineParts[0]), requestLineParts[1], requestLineParts[2]);
    }
}
