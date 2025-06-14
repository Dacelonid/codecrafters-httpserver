package server.handlers;

import server.httpstructure.HttpRequest;
import server.httpstructure.HttpResponse;

public interface Handler {
    HttpResponse handle(HttpRequest request);
}
