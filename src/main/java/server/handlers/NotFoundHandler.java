package server.handlers;

import server.httpUtils.HttpCodes;
import server.httpstructure.HttpRequest;
import server.httpstructure.HttpResponse;

public class NotFoundHandler implements Handler {
    @Override
    public HttpResponse handle(HttpRequest request) {
        return new HttpResponse(HttpCodes.NOT_FOUND, "Content-Type: text/plain", "Not Found");
    }
}
