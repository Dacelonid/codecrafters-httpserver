package server.handlers;

import server.httpstructure.HttpRequest;

public class PostHandler {
    public Handler chooseHandler(HttpRequest request) {
        String command = getCommand(request);
        return switch (command) {
            case "files" -> new PostFileHandler();
            default -> new NotFoundHandler();
        };


    }
    /**
     * Convenicence method to get the String value of the command so that we can use a switch expression in the
     * method that calls this
     *
     * @param request contains the request that is being made of our server
     * @return a String representing one of the allowed commands, or just the entire string
     */
    public static String getCommand(HttpRequest request) {
        String target = request.getTarget();
        if (target.contains("/files/"))
            return "files";
        return target;
    }
}
