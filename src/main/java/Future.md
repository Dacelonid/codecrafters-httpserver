# Planned Features for HTTP Server

This document outlines the next features to implement in the HTTP server project, prioritized for maximum learning and real-world relevance.

---

## 1. 🔐 HTTPS (TLS) Support

**Goal:** Secure the HTTP server using HTTPS with TLS encryption.

**Why:** HTTPS is a production-grade requirement and teaches secure socket handling in Java.

**Tasks:**
- Generate a self-signed certificate (e.g., using `keytool`)
- Use `SSLServerSocket` to listen for TLS connections
- Serve the same HTTP handlers over secure connection
- Optionally support both HTTP and HTTPS on different ports

---

## 2. 📈 Logging and Access Logs

**Goal:** Log request details for observability and debugging.

**Why:** Every real server needs logging for monitoring and auditing.

**Tasks:**
- Log IP address, HTTP method, path, response status, and time taken
- Optionally log headers or request bodies for debugging
- Implement rotating or simple file-based logging

---

## 3. 📁 Serve Static Directory Listings

**Goal:** Display contents of a directory when a URL points to a folder.

**Why:** Emulates realistic file server behavior and teaches dynamic HTML generation.

**Tasks:**
- If the requested path is a directory, list its files as HTML or JSON
- Include links to files/subdirectories
- Optionally allow navigation up the directory tree

---

## 4. 🧠 Support HTTP Methods Beyond GET/POST

**Goal:** Add support for additional HTTP verbs to expand REST-style handling.

**Why:** Teaches correct method semantics and prepares for more advanced APIs.

**Tasks:**
- Support `PUT` and `DELETE` (e.g., create or remove files)
- Handle `OPTIONS` for CORS or introspection
- Handle `HEAD` (like `GET` but no body)

---

## 5. 🔁 Chunked Transfer Encoding

**Goal:** Implement chunked encoding for responses when content length is unknown in advance.

**Why:** Teaches streaming responses and HTTP/1.1 transfer encoding details.

**Tasks:**
- Add support for `Transfer-Encoding: chunked` responses
- Write correct chunked output format (`<length>\r\n<data>\r\n`)
- Implement `/stream` route that emits data slowly in chunks

---

## Notes

These features build progressively toward a full-featured, robust HTTP 1.1 server. Future enhancements might include:
- Request timeout handling
- Middleware support
- HTTP/2 upgrade path
