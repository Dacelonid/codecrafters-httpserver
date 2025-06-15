package server.handlers;

import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.HttpResponse;

public interface Handler {
    HttpResponse handle(HttpRequest request);
}
