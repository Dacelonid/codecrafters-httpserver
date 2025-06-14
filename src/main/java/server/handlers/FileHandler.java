package server.handlers;

import server.httpUtils.HttpCodes;
import server.httpUtils.HttpConstants;
import server.httpstructure.HttpRequest;
import server.httpstructure.HttpResponse;

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
                return new HttpResponse(HttpCodes.OK, "application/octet-stream", getFileContents(path));
            } catch (IOException e) {
                return new HttpResponse(HttpCodes.NOT_FOUND, "text/plain", "Not Found");
            }
        }
        else{
            return new HttpResponse(HttpCodes.NOT_FOUND, "text/plain", "Not Found");
        }
    }

    private String getFileContents(Path pathToFile) throws IOException {
        return Files.readString(pathToFile);
    }
}
