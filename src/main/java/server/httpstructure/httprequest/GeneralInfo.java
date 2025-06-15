package server.httpstructure.httprequest;

public record GeneralInfo(String host, String connection, String upgrade, String via, String cacheControl, String pragma, String trailer) { }
