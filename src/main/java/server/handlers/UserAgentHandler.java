package server.handlers;

import server.httpUtils.HttpCodes;
import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.httpresponse.HttpResponse;

public class UserAgentHandler implements Handler{

    @Override
    public HttpResponse handle(HttpRequest request) {
        return HttpResponse.builder().responseCode(HttpCodes.OK).connection(request.getHttpHeader().getConnection()).body(request.getHttpHeader().getUserAgent()).build();
    }
}
