package server.httpstructure.httprequest;

public class HttpHeader {
    private final ClientInfo clientInfo;
    private final GeneralInfo generalInfo;
    private final ContentNegotiation contentNegotiationInfo;

    private HttpHeader(String userAgent, String host, String accept, String acceptEncoding, String connection) {
        this.clientInfo = new ClientInfo(userAgent, null, null, null);
        this.generalInfo = new GeneralInfo(host, connection);
        this.contentNegotiationInfo = new ContentNegotiation(accept, acceptEncoding);
    }

    public static HttpHeader build(String[] headers) {
        String userAgent = "";
        String host = "";
        String accept = "";
        String acceptEncoding = "";
        String connection = "";
        for (String header : headers) {
            if (header.startsWith("Host")) {
                host = header.substring(5).trim();
            }
            if (header.startsWith("User-Agent")) {
                userAgent = header.substring(11).trim();
            }
            if (header.startsWith("Accept-Encoding")) {
                acceptEncoding = header.substring(15).trim();
            }
        }

        return new HttpHeader(userAgent, host, accept, acceptEncoding, connection);
    }

    public String getUserAgent() {
        return clientInfo.userAgent();
    }

    public String getHost() {
        return generalInfo.host();
    }
}
