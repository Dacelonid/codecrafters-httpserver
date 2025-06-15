package server.handlers;

import server.httpUtils.HttpCodes;
import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.HttpResponse;

public class BasicOKHandler implements Handler {
    public HttpResponse handle(HttpRequest request) {
        return new HttpResponse(HttpCodes.OK, "text/plain", "OK");
    }

}
