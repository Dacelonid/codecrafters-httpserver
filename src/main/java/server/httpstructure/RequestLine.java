package server.httpstructure;

import server.httpUtils.HttpMethod;

public class RequestLine {
    HttpMethod method;
    String target;
    String version;

    public RequestLine(String requestLineString) {
        String[] requestLineParts = requestLineString.split(" ");
        this.method =HttpMethod.valueOf(requestLineParts[0]);
        this.target = requestLineParts[1];
        this.version = requestLineParts[2];
    }

    public String getTarget() {
        return target;
    }
}
