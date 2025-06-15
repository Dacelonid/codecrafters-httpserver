package server.handlers;

import server.httpUtils.HttpCodes;
import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.httpresponse.HttpResponse;

public class MethodNotAllowedHandler implements Handler{
    @Override
    public HttpResponse handle(HttpRequest request) {
        return HttpResponse.builder().responseCode(HttpCodes.NOT_ALLOWED).connection(request.getHttpHeader().getConnection()).body("Not Allowed").build();
    }
}
