package server.handlers;

import server.httpUtils.HttpCodes;
import server.httpUtils.HttpConstants;
import server.httpstructure.httprequest.HttpRequest;
import server.httpstructure.httpresponse.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PostFileHandler implements Handler {
    @Override
    public HttpResponse handle(HttpRequest request) {
        String file = request.getTarget().substring("/files/".length());

        Path path = Path.of(HttpConstants.baseDir).resolve(file).normalize();
        if(createFile(path, request.getBody())){
            return HttpResponse.builder().responseCode(HttpCodes.CREATED).body("Created").build();
        }
        return HttpResponse.builder().responseCode(HttpCodes.INTERNAL_SERVER_ERROR).body("Internal Server Error").build();
    }

    private boolean createFile(Path fileName, String content) {
        try {
            // Combine base directory and filename
            Path dirPath = Paths.get(HttpConstants.baseDir);
            Path filePath = dirPath.resolve(fileName);

            // Ensure directory exists
            Files.createDirectories(dirPath);

            // Write content to file (creates or overwrites)
            Files.writeString(filePath, content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
