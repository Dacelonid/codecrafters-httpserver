package server.handlers;

import server.httpUtils.HttpCodes;
import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.httpresponse.HttpResponse;

public class NotFoundHandler implements Handler {
    @Override
    public HttpResponse handle(HttpRequest request) {
        return HttpResponse.builder().responseCode(HttpCodes.NOT_FOUND).connection(request.getHttpHeader().getConnection()).body("Not Found").build();
    }
}
