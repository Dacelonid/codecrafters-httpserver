package server.handlers;

import server.httpUtils.HttpCodes;
import server.httpUtils.HttpConstants;
import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.httpresponse.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class FileHandler implements Handler {

    @Override
    public HttpResponse handle(HttpRequest request) {
        String file = request.getTarget().substring("/files/".length());
        Path path = Path.of(HttpConstants.baseDir).resolve(file).normalize();
        if (path.toFile().exists()) {
            try {
                if(isGzip(request)){
                    return HttpResponse.builder().responseCode(HttpCodes.OK).contentType("text/plain").connection(request.getHttpHeader().getConnection()).contentEncoding("gzip").body(compress(getFileContents(path))).build();
                }
                return HttpResponse.builder().responseCode(HttpCodes.OK).connection(request.getHttpHeader().getConnection()).contentType("application/octet-stream").body(getFileContents(path)).build();
            } catch (IOException e) {
                return HttpResponse.builder().responseCode(HttpCodes.NOT_FOUND).connection(request.getHttpHeader().getConnection()).body("Not Found").build();
            }
        }
        else{
            return HttpResponse.builder().responseCode(HttpCodes.NOT_FOUND).connection(request.getHttpHeader().getConnection()).body("Not Found").build();
        }
    }

    private String getFileContents(Path pathToFile) throws IOException {
        return Files.readString(pathToFile);
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
