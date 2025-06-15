package server.httpstructure.httprequest;

public record ClientInfo(String userAgent,String referer,String from, String xRequestedWith) {
}
