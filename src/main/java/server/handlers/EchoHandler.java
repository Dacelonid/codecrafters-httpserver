package server.handlers;

import server.HttpCodes;
import server.HttpRequest;
import server.HttpResponse;

public class EchoHandler implements Handler{
    public HttpResponse handle(HttpRequest request) {
        String path = request.getTarget().substring("/echo/".length());
        return new HttpResponse(HttpCodes.OK, path, "text/plain");
    }
}
