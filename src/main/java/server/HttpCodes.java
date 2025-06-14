package server;

public enum HttpCodes {
    OK(200, "OK"), NOT_FOUND(404, "Not Found");

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    private int code;
    private String message;

    private HttpCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
