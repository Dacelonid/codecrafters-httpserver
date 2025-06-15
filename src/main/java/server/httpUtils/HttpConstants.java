package server.httpUtils;

public class HttpConstants {
    public static final String REQUEST_SEPARATOR = "\r\n";
    public static final String HTTP_VERSION = "HTTP/1.1";
    public static final String CONTENT_TYPE = "Content-type: ";
    public static final String CONTENT_LENGTH = "Content-length:";
    public static final String BLANK_LINE = REQUEST_SEPARATOR + REQUEST_SEPARATOR;


    public static final String DEFAULT_ENCODING = "identity";
    public static final String DEFAULT_LANGUAGE = "en-US";
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String DEFAULT_CONNECTION = "keep-alive";
    public static final String DEFAULT_ACCEPT = "*/*";
    public static final String DEFAULT_EMPTY = "";



    public static String baseDir = "";

}
