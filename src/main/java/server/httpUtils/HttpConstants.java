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
    public static final String DEFAULT_CONTENT_TYPE = "text/plain";
    public static final String DEFAULT_CONTENT_LENGTH = "0";
    public static final String DEFAULT_CONTENT_ENCODING = "binary";
    public static final String DEFAULT_CONTENT_LANGUAGE = "en-US";
    public static final String DEFAULT_CONTENT_CHARSET = "UTF-8";
    public static final String DEFAULT_CONNECTION = "keep-alive";
    public static final String DEFAULT_UPGRADE = "none";
    public static final String DEFAULT_TRAILER = "none";
    public static final String DEFAULT_VIA = "1.1 (codecrafters-http-server-java)";
    public static final String DEFAULT_CACHE_CONTROL = "no-cache";
    public static final String DEFAULT_PRAGMA = "no-cache";



    public static String baseDir = "";

}
