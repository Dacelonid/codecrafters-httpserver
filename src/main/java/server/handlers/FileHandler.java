package server.handlers;

import server.httpUtils.HttpCodes;
import server.httpUtils.HttpConstants;
import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.httpresponse.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandler implements Handler {

    @Override
    public HttpResponse handle(HttpRequest request) {
        String file = request.getTarget().substring("/files/".length());
        Path path = Path.of(HttpConstants.baseDir).resolve(file).normalize();
        if (path.toFile().exists()) {
            try {
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
}
