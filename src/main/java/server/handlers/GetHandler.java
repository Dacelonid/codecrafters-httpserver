package server.handlers;

import server.httpstructure.HttpRequest;

public class GetHandler {
    public Handler chooseHandler(HttpRequest request) {
        String command = getCommand(request);
        return switch (command) {
            case "/", "/index.html" -> new BasicOKHandler();
            case "echo" -> new EchoHandler();
            case "files" -> new FileHandler();
            case "user-agent" -> new UserAgentHandler();
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
        if (target.contains("/echo/"))
            return "echo";
        if (target.contains("/files/"))
            return "files";
        if (target.contains("/user-agent"))
            return "user-agent";
        return target;
    }
}
