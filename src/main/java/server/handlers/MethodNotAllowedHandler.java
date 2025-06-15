package server.handlers;

import server.httpUtils.HttpCodes;
import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.HttpResponse;

public class MethodNotAllowedHandler implements Handler{
    @Override
    public HttpResponse handle(HttpRequest request) {
        return new HttpResponse(HttpCodes.NOT_ALLOWED, "text/plain", "Not Allowed");
    }
}
