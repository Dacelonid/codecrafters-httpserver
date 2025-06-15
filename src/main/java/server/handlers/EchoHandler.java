package server.handlers;

import server.httpUtils.HttpCodes;
import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.httpresponse.HttpResponse;

public class EchoHandler implements Handler{
    public HttpResponse handle(HttpRequest request) {
        String path = request.getTarget().substring("/echo/".length());
        if(request.getHttpHeader().getAcceptEncoding().equals("gzip")){
            return HttpResponse.builder().responseCode(HttpCodes.OK).contentType("text/plain").contentEncoding("gzip").body(path).build();
        }
        return HttpResponse.builder().responseCode(HttpCodes.OK).contentType("text/plain").body(path).build();
    }
}
