package server.httpstructure.httprequest;

import static server.httpUtils.HttpConstants.*;

public record GeneralInfo(String host, String connection, String upgrade, String via, String cacheControl, String pragma, String trailer) {
    // Overload: only accept
    public GeneralInfo(String host, String connection) {
        this(host, connection, DEFAULT_UPGRADE, DEFAULT_VIA, DEFAULT_CACHE_CONTROL, DEFAULT_PRAGMA, DEFAULT_TRAILER);
    }
}
