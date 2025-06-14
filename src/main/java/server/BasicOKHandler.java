package server;

import server.handlers.Handler;

public class BasicOKHandler implements Handler {
    public HttpResponse handle(HttpRequest request) {
        return new HttpResponse(HttpCodes.OK, "OK", "text/plain");
    }

}
