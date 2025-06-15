package server.httpstructure.httprequest;

import static server.httpUtils.HttpConstants.*;

public class HttpHeader {
    private final ClientInfo clientInfo;
    private final GeneralInfo generalInfo;
    private final ContentNegotiation contentNegotiationInfo;

    private HttpHeader(HttpHeaderBuilder builder) {
        this.clientInfo = new ClientInfo(builder.userAgent, builder.referer, builder.from, builder.xRequestedWith);
        this.generalInfo = new GeneralInfo(builder.host, builder.connection, builder.upgrade, builder.via, builder.cacheControl, builder.pragma, builder.trailer);
        this.contentNegotiationInfo = new ContentNegotiation(
                builder.accept,
                builder.acceptEncoding,
                builder.acceptLanguage,
                builder.acceptCharset
        );
    }

    public static HttpHeaderBuilder builder() {
        return new HttpHeaderBuilder();
    }

    public static HttpHeader build(String[] headers) {
        HttpHeaderBuilder builder = builder();
        for (String header : headers) {
            if (header.startsWith("Host:")) {
                builder = builder.host(header.substring("Host:".length()).trim());
            }
            if (header.startsWith("User-Agent:")) {
                builder = builder.userAgent(header.substring("User-Agent:".length()).trim());
            }
            if (header.startsWith("Accept-Encoding:")) {
                builder = builder.acceptEncoding(header.substring("Accept-Encoding:".length()).trim());
            }
        }
        return builder.build();
    }

    public String getUserAgent() {
        return clientInfo.userAgent();
    }

    public String getHost() {
        return generalInfo.host();
    }

    public String getAccept() {
        return contentNegotiationInfo.accept();
    }

    public String getAcceptEncoding() {
        return contentNegotiationInfo.acceptEncoding();
    }


    // === Builder Class ===
    public static class HttpHeaderBuilder {
        private String upgrade = DEFAULT_EMPTY;
        private String via = DEFAULT_EMPTY;
        private String cacheControl = DEFAULT_EMPTY;
        private String pragma = DEFAULT_EMPTY;
        private String trailer = DEFAULT_EMPTY;
        private String referer = DEFAULT_EMPTY;
        private String from = DEFAULT_EMPTY;
        private String xRequestedWith = DEFAULT_EMPTY;

        private String userAgent = DEFAULT_EMPTY;
        private String host = DEFAULT_EMPTY;
        private String connection = DEFAULT_CONNECTION;

        private String accept = DEFAULT_ACCEPT;
        private String acceptEncoding = DEFAULT_ENCODING;
        private String acceptLanguage = DEFAULT_LANGUAGE;
        private String acceptCharset = DEFAULT_CHARSET;

        public HttpHeaderBuilder upgrade(String upgrade) {
            this.upgrade = upgrade;
            return this;
        }

        public HttpHeaderBuilder via(String via) {
            this.via = via;
            return this;
        }

        public HttpHeaderBuilder cacheControl(String cacheControl) {
            this.cacheControl = cacheControl;
            return this;
        }

        public HttpHeaderBuilder pragma(String pragma) {
            this.pragma = pragma;
            return this;
        }

        public HttpHeaderBuilder trailer(String trailer) {
            this.trailer = trailer;
            return this;
        }

        public HttpHeaderBuilder referer(String referer) {
            this.referer = referer;
            return this;
        }

        public HttpHeaderBuilder from(String from) {
            this.from = from;
            return this;
        }

        public HttpHeaderBuilder xRequestedWith(String xRequestedWith) {
            this.xRequestedWith = xRequestedWith;
            return this;
        }

        public HttpHeaderBuilder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public HttpHeaderBuilder host(String host) {
            this.host = host;
            return this;
        }

        public HttpHeaderBuilder connection(String connection) {
            this.connection = connection;
            return this;
        }

        public HttpHeaderBuilder accept(String accept) {
            this.accept = accept;
            return this;
        }

        public HttpHeaderBuilder acceptEncoding(String acceptEncoding) {
                this.acceptEncoding = acceptEncoding;
            return this;
        }

        public HttpHeaderBuilder acceptLanguage(String acceptLanguage) {
            this.acceptLanguage = acceptLanguage;
            return this;
        }

        public HttpHeaderBuilder acceptCharset(String acceptCharset) {
            this.acceptCharset = acceptCharset;
            return this;
        }

        public HttpHeader build() {
            return new HttpHeader(this);
        }
    }
}
