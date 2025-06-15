package server.httpstructure.httpresponse;

import static server.httpUtils.HttpConstants.*;

public record EntityHeaders(String contentType, String contentEncoding, int contentLength) {
    public String toString() {
        String contentEncodingString = this.contentEncoding == null ? "" : REQUEST_SEPARATOR + CONTENT_ENCODING + contentEncoding;
        return CONTENT_TYPE + contentType + contentEncodingString + REQUEST_SEPARATOR +CONTENT_LENGTH + contentLength;
    }
}
