package server.handlers;

import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.httpresponse.HttpResponse;

public interface Handler {
    HttpResponse handle(HttpRequest request);
}
