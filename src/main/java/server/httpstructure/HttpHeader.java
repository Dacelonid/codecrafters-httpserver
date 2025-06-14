package server.httpstructure;

public class HttpHeader {
    private final String userAgent;
    private final String host;
    private final String accept;
    private final String connection;

    private HttpHeader(String userAgent, String host, String accept, String connection) {
        this.userAgent = userAgent;
        this.host = host;
        this.accept = accept;
        this.connection = connection;
    }

    public static HttpHeader build(String[] headers){
        String userAgent = "";
        String host = "";
        String accept = "";
        String connection = "";
        for (String header : headers) {
            if (header.startsWith("Host")) {
                host = header.substring(5).trim();
            }
            if (header.startsWith("User-Agent")) {userAgent = header.substring(11).trim(); }
        }

        return new HttpHeader(userAgent, host, accept, connection);
    }

    public String getUserAgent() {
        return userAgent;
    }
}
