package server.handlers;

import server.httpUtils.HttpCodes;
import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.httpresponse.HttpResponse;

import java.util.Arrays;
import java.util.List;

public class EchoHandler implements Handler{
    public HttpResponse handle(HttpRequest request) {
        String path = request.getTarget().substring("/echo/".length());
        if(isGzip(request)){
            return HttpResponse.builder().responseCode(HttpCodes.OK).contentType("text/plain").contentEncoding("gzip").body(path).build();
        }
        return HttpResponse.builder().responseCode(HttpCodes.OK).contentType("text/plain").body(path).build();
    }

    private static boolean isGzip(HttpRequest request) {
        List<String> acceptEncoding = Arrays.asList(request.getHttpHeader().getAcceptEncoding().split(", "));
        return acceptEncoding.contains("gzip");
    }
}
