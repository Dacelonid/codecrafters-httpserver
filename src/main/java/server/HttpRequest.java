package server;

import java.util.Arrays;

public class HttpRequest {
    private final HttpMethod method;
    private final String target;
    private final String version;
    private HttpRequest(HttpMethod method, String target, String version) {
        this.method = method;
        this.target = target;
        this.version = version;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public String getVersion() {
        return version;
    }

    public static HttpRequest parse(String request) {
        System.out.println("request - " + request);
        String[] parts = request.split("\\r\\n");
        String requestLine = parts[0];
        String[] requestLineParts = requestLine.split(" ");
        return new HttpRequest(HttpMethod.valueOf(requestLineParts[0]), requestLineParts[1], requestLineParts[2]);
    }

    public String getCommand() {
        if(target.contains("/echo/"))
            return "echo";
        return target;
    }
}
