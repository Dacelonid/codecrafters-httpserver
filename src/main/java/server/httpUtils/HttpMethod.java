package server.httpUtils;

import java.util.Arrays;

public enum HttpMethod {
    GET, POST, UNKNOWN;//PUT, DELETE, HEAD, OPTIONS, TRACE, CONNECT, PATCH,

    public static HttpMethod fromString(String method) {
        if (method == null) return GET; // or UNKNOWN
        return Arrays.stream(HttpMethod.values())
                .filter(m -> m.name().equalsIgnoreCase(method))
                .findFirst()
                .orElse(UNKNOWN); // or UNKNOWN
    }
}
