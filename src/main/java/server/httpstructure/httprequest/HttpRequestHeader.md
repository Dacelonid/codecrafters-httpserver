| **Category**                 | **Header Name**       | **Description**                                                        | **Mandatory**           | Added |
|------------------------------|-----------------------|------------------------------------------------------------------------|-------------------------|-------|
| **General**                  | `Host`                | Domain name of the server (and optionally port); required in HTTP/1.1. | ✅ Yes (HTTP/1.1+)       | Yes   |
|                              | `Connection`          | Controls persistence of the connection (e.g., `keep-alive`).           | ❌ No                    | Yes
|                              | `Upgrade`             | Requests protocol upgrade (e.g., to WebSockets).                       | ❌ No                    |
|                              | `Via`                 | Describes intermediate proxies/gateways.                               | ❌ No                    |
|                              | `Cache-Control`       | Controls caching behavior (e.g., `no-cache`, `max-age`).               | ❌ No                    |
|                              | `Pragma`              | Legacy HTTP/1.0 caching directive (commonly `no-cache`).               | ❌ No                    |
|                              | `Trailer`             | Declares headers to be sent after the body in chunked encoding.        | ❌ No                    |
| **Client Info**              | `User-Agent`          | Identifies the client (browser, curl, etc.).                           | ❌ No                    | Yes   |
|                              | `Referer`             | Indicates the previous page (URL).                                     | ❌ No                    |
|                              | `From`                | Email address of the user or agent making the request.                 | ❌ No                    |
|                              | `X-Requested-With`    | Typically used for identifying AJAX requests.                          | ❌ No                    |
| **Content Negotiation**      | `Accept`              | Specifies preferred response media types (e.g., `application/json`).   | ❌ No                    | Yes
|                              | `Accept-Encoding`     | Accepted compression algorithms (e.g., `gzip`, `deflate`).             | ❌ No                    | Yes
|                              | `Accept-Language`     | Preferred languages (e.g., `en-US`).                                   | ❌ No                    |
|                              | `Accept-Charset`      | Preferred character encodings (e.g., `utf-8`).                         | ❌ No                    |
| **Authentication & Cookies** | `Authorization`       | Credentials for accessing protected resources (e.g., Bearer token).    | ❌ No                    |
|                              | `Cookie`              | Sends stored cookies to the server.                                    | ❌ No                    |
| **Entity (Request Body)**    | `Content-Type`        | MIME type of the body (e.g., `application/json`).                      | ✅ If body present       |
|                              | `Content-Length`      | Size of the body in bytes.                                             | ✅ If body (not chunked) |
|                              | `Transfer-Encoding`   | Used when body is sent using chunked encoding.                         | ❌ No                    |
|                              | `Content-Encoding`    | Encoding applied to the body (e.g., `gzip`).                           | ❌ No                    |
|                              | `Content-Language`    | Language of the body content.                                          | ❌ No                    |
|                              | `Expect`              | Indicates client expectations (e.g., `100-continue`).                  | ❌ No                    |
| **Conditional**              | `If-Modified-Since`   | Only send response if resource modified after the given date.          | ❌ No                    |
|                              | `If-None-Match`       | Conditional request based on ETag mismatch.                            | ❌ No                    |
|                              | `If-Match`            | Conditional request based on ETag match.                               | ❌ No                    |
|                              | `If-Unmodified-Since` | Send only if the resource hasn't changed since the given date.         | ❌ No                    |
|                              | `Range`               | Request only part of the resource (e.g., for resume/download).         | ❌ No                    |
