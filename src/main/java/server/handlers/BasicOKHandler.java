package server.handlers;

import server.httpUtils.HttpCodes;
import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.httpresponse.HttpResponse;

public class BasicOKHandler implements Handler {
    public HttpResponse handle(HttpRequest request) {
        return HttpResponse.builder().responseCode(HttpCodes.OK).connection(request.getHttpHeader().getConnection()).contentType("text/plain").body("OK").build();
    }

}
