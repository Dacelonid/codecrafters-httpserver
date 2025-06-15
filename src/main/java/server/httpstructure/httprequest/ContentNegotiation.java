package server.httpstructure.httprequest;

public record ContentNegotiation(
        String accept,
        String acceptEncoding,
        String acceptLanguage,
        String acceptCharset
) {
}
