package server.httpstructure;

public class RequestLine {
    String method;
    String target;
    String version;

    public RequestLine(String requestLineString) {
        String[] requestLineParts = requestLineString.split(" ");
        this.method =requestLineParts[0];
        this.target = requestLineParts[1];
        this.version = requestLineParts[2];
    }

    public String getTarget() {
        return target;
    }
}
