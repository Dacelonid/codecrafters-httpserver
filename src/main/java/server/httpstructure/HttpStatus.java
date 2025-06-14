package server.httpstructure;

import server.httpUtils.HttpCodes;

public class HttpStatus {
    private final String version;
    private final int code;
    private final String message;

    public HttpStatus(String version, HttpCodes code) {
        this.version = version;
        this.code = code.getCode();
        this.message = code.getMessage();
    }

    public String toString(){
        return version + " " + code + " " + message;
    }
}
