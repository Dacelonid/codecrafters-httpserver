package server.httpstructure;

import server.httpUtils.HttpMethod;

public class RequestLine {
    public HttpMethod getMethod() {
        return method;
    }

    HttpMethod method;
    String target;
    String version;

    public RequestLine(String requestLineString) {
        String[] requestLineParts = requestLineString.split(" ");
        this.method =HttpMethod.fromString(requestLineParts[0]);
        this.target = requestLineParts[1];
        this.version = requestLineParts[2];
    }

    public String getTarget() {
        return target;
    }
}
