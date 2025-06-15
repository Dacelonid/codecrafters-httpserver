| **Category**          | **Header Name**                | **Description**                                                                 | **Mandatory**          |
|-----------------------|--------------------------------|---------------------------------------------------------------------------------|------------------------|
| **General**           | `Date`                         | The date and time at which the response was generated.                          | ✅ Yes (HTTP/1.1)       |
|                       | `Connection`                   | Control options for the current connection (e.g., `close`, `keep-alive`).       | ❌ No                   |
|                       | `Cache-Control`                | Directives for caching mechanisms (e.g., `no-cache`, `max-age`).                | ❌ No                   |
|                       | `Pragma`                       | Legacy HTTP/1.0 header for caching (e.g., `no-cache`).                          | ❌ No                   |
|                       | `Trailer`                      | Declares trailing headers in chunked responses.                                 | ❌ No                   |
|                       | `Transfer-Encoding`            | Specifies encoding used to transfer the response body (e.g., `chunked`).        | ❌ No                   |
|                       | `Upgrade`                      | Suggests switching protocols (e.g., to WebSockets).                             | ❌ No                   |
|                       | `Via`                          | Describes intermediate proxies or gateways.                                     | ❌ No                   |
| **Response Metadata** | `Server`                       | Describes the server software (e.g., `Apache/2.4`).                             | ❌ No                   |
|                       | `Allow`                        | Lists allowed HTTP methods for the resource (e.g., `GET, POST`).                | ❌ No                   |
|                       | `Accept-Ranges`                | Indicates support for partial requests (e.g., `bytes`).                         | ❌ No                   |
|                       | `ETag`                         | Provides a unique identifier for the resource version.                          | ❌ No                   |
|                       | `Last-Modified`                | Timestamp of the last modification of the resource.                             | ❌ No                   |
|                       | `Location`                     | Used in redirection or resource creation to specify the new URI.                | ✅ Yes (3xx/201)        |
|                       | `Retry-After`                  | When the client can retry the request (used with 429/503 responses).            | ❌ No                   |
|                       | `Vary`                         | Specifies headers that influence the response (for caching).                    | ❌ No                   |
|                       | `Warning`                      | Carries additional information about the status or transformation.              | ❌ No                   |
| **Entity Headers**    | `Content-Type`                 | Media type of the response body (e.g., `application/json`, `text/html`).        | ✅ Yes (if body)        |
|                       | `Content-Length`               | Size of the response body in bytes.                                             | ✅ Yes (if not chunked) |
|                       | `Content-Encoding`             | Encoding used to compress the response (e.g., `gzip`).                          | ❌ No                   |
|                       | `Content-Language`             | Language of the response content.                                               | ❌ No                   |
|                       | `Content-Location`             | Alternate location of the returned content.                                     | ❌ No                   |
|                       | `Content-Range`                | Range of bytes returned in partial content responses.                           | ❌ No                   |
|                       | `Expires`                      | Date/time after which the response is considered stale.                         | ❌ No                   |
| **Authentication**    | `WWW-Authenticate`             | Indicates how to authenticate (e.g., `Basic realm="..."`).                      | ✅ Yes (401 only)       |
|                       | `Proxy-Authenticate`           | Like `WWW-Authenticate`, but for proxy authentication.                          | ✅ Yes (407 only)       |
|                       | `Set-Cookie`                   | Sets cookies in the client.                                                     | ❌ No                   |
| **CORS / Security**   | `Access-Control-Allow-Origin`  | CORS: specifies which origins are allowed.                                      | ❌ No                   |
|                       | `Access-Control-Allow-Headers` | CORS: allowed request headers.                                                  | ❌ No                   |
|                       | `Strict-Transport-Security`    | Enforces HTTPS by instructing the browser to only use HTTPS.                    | ❌ No                   |
|                       | `X-Content-Type-Options`       | Prevents MIME-sniffing (e.g., `nosniff`).                                       | ❌ No                   |
|                       | `X-Frame-Options`              | Controls whether the response can be embedded in frames (`DENY`, `SAMEORIGIN`). | ❌ No                   |
|                       | `X-XSS-Protection`             | Enables XSS filtering in browsers (deprecated).                                 | ❌ No                   |
