package server.httpUtils;

public enum HttpCodes {
    OK(200, "OK"),CREATED(201, "Created"), NOT_FOUND(404, "Not Found"), NOT_ALLOWED(405, "Not Allowed"), INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    private final int code;
    private final String message;

    private HttpCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }


}
