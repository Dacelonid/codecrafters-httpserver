package server.handlers;

import server.httpUtils.HttpCodes;
import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.HttpResponse;

public class EchoHandler implements Handler{
    public HttpResponse handle(HttpRequest request) {
        String path = request.getTarget().substring("/echo/".length());
        return new HttpResponse(HttpCodes.OK, "text/plain", path);
    }
}
