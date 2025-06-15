package server.handlers;

import server.httpUtils.HttpCodes;
import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.httpresponse.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class EchoHandler implements Handler{
    public HttpResponse handle(HttpRequest request) {
        String path = request.getTarget().substring("/echo/".length());
        if(isGzip(request)){
            return HttpResponse.builder().responseCode(HttpCodes.OK).contentType("text/plain").contentEncoding("gzip").body(compress(path)).build();
        }
        return HttpResponse.builder().responseCode(HttpCodes.OK).contentType("text/plain").body(path).build();
    }

    private static byte[] compress(String data) {
        if (data == null || data.isEmpty()) {
            return new byte[0];
        }

        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {

            gzipStream.write(data.getBytes(StandardCharsets.UTF_8));
            gzipStream.finish();
            return byteStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to compress data", e);
        }
    }

    private static boolean isGzip(HttpRequest request) {
        List<String> acceptEncoding = Arrays.asList(request.getHttpHeader().getAcceptEncoding().split(", "));
        return acceptEncoding.contains("gzip");
    }
}
