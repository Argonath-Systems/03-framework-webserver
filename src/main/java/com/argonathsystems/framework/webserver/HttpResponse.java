package com.argonathsystems.framework.webserver;

import java.util.Map;

/**
 * Platform-agnostic HTTP response builder.
 * 
 * <p>Allows setting response status, headers, and body content without
 * coupling to Jakarta Servlet API or any specific HTTP server implementation.
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public interface HttpResponse {
    
    /**
     * Sets the HTTP status code.
     * 
     * <p>Common status codes:
     * <ul>
     *   <li>200 OK - Request succeeded</li>
     *   <li>201 Created - Resource created</li>
     *   <li>204 No Content - Success with no response body</li>
     *   <li>400 Bad Request - Invalid request data</li>
     *   <li>401 Unauthorized - Authentication required</li>
     *   <li>403 Forbidden - Insufficient permissions</li>
     *   <li>404 Not Found - Resource not found</li>
     *   <li>500 Internal Server Error - Server-side error</li>
     * </ul>
     * 
     * @param code the HTTP status code
     */
    void setStatus(int code);
    
    /**
     * Sets a response header.
     * 
     * @param name the header name
     * @param value the header value
     */
    void setHeader(String name, String value);
    
    /**
     * Sets the Content-Type header.
     * 
     * <p>Common content types:
     * <ul>
     *   <li>{@code application/json} - JSON data</li>
     *   <li>{@code text/html} - HTML content</li>
     *   <li>{@code text/plain} - Plain text</li>
     *   <li>{@code application/octet-stream} - Binary data</li>
     * </ul>
     * 
     * @param contentType the content type
     */
    void setContentType(String contentType);
    
    /**
     * Writes the response body.
     * 
     * <p>This method can be called multiple times to append to the response.
     * 
     * @param content the content to write
     */
    void write(String content);
    
    /**
     * Writes the response body as JSON.
     * 
     * <p>Automatically sets Content-Type to {@code application/json}.
     * 
     * @param content the JSON content to write
     */
    default void writeJson(String content) {
        setContentType("application/json");
        write(content);
    }
    
    /**
     * Writes an error response with JSON body.
     * 
     * <p>Example: {@code writeError(404, "Quest not found")}
     * produces: {@code {"error": "Quest not found"}}
     * 
     * @param statusCode the HTTP status code
     * @param message the error message
     */
    default void writeError(int statusCode, String message) {
        setStatus(statusCode);
        writeJson(String.format("{\"error\":\"%s\"}", message));
    }
}
