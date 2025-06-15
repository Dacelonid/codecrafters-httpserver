package server.httpstructure.httprequest;

import static server.httpUtils.HttpConstants.*;

public record ContentNegotiation(
        String accept,
        String acceptEncoding,
        String acceptLanguage,
        String acceptCharset
) {

    // Overload: only accept
    public ContentNegotiation(String accept, String acceptEncoding) {
        this(accept, acceptEncoding, DEFAULT_LANGUAGE, DEFAULT_CHARSET);
    }

}
