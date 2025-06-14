package server.handlers;

import server.HttpCodes;
import server.HttpRequest;
import server.HttpResponse;

public interface Handler {
    default HttpResponse handle(HttpRequest request){
        return new HttpResponse(HttpCodes.NOT_FOUND, "Not Found", "Content-Type: text/plain");
    };
}
