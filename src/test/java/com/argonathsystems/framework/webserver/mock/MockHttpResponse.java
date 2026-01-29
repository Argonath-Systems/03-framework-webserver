package com.argonathsystems.framework.webserver.mock;

import com.argonathsystems.framework.webserver.HttpResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of {@link HttpResponse} for testing.
 * 
 * <p>This class captures all response data written by route handlers,
 * allowing verification in unit tests.
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * MockHttpResponse response = new MockHttpResponse();
 * 
 * // Call the handler under test
 * handler.handle(request, response);
 * 
 * // Verify the response
 * assertEquals(200, response.getStatus());
 * assertEquals("application/json", response.getContentType());
 * assertTrue(response.getBody().contains("\"id\":\"quest123\""));
 * }</pre>
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public final class MockHttpResponse implements HttpResponse {
    
    private int status = 200;
    private final Map<String, String> headers = new HashMap<>();
    private final StringBuilder body = new StringBuilder();
    
    /**
     * Creates a new MockHttpResponse with default values.
     */
    public MockHttpResponse() {
        // Default status is 200 OK
    }
    
    @Override
    public void setStatus(int code) {
        this.status = code;
    }
    
    @Override
    public void setHeader(String name, String value) {
        this.headers.put(name, value);
    }
    
    @Override
    public void setContentType(String contentType) {
        this.headers.put("Content-Type", contentType);
    }
    
    @Override
    public void write(String content) {
        this.body.append(content);
    }
    
    // =========================================================================
    // Test Inspection Methods
    // =========================================================================
    
    /**
     * Gets the HTTP status code that was set.
     * 
     * @return the status code
     */
    public int getStatus() {
        return status;
    }
    
    /**
     * Gets the Content-Type header value.
     * 
     * @return the content type, or null if not set
     */
    public String getContentType() {
        return headers.get("Content-Type");
    }
    
    /**
     * Gets a specific header value.
     * 
     * @param name the header name
     * @return the header value, or null if not set
     */
    public String getHeader(String name) {
        return headers.get(name);
    }
    
    /**
     * Gets all headers as a map.
     * 
     * @return unmodifiable map of headers
     */
    public Map<String, String> getHeaders() {
        return Map.copyOf(headers);
    }
    
    /**
     * Gets the response body as a string.
     * 
     * @return the response body
     */
    public String getBody() {
        return body.toString();
    }
    
    /**
     * Checks if the response is a success (2xx status code).
     * 
     * @return true if status is 200-299
     */
    public boolean isSuccess() {
        return status >= 200 && status < 300;
    }
    
    /**
     * Checks if the response is a client error (4xx status code).
     * 
     * @return true if status is 400-499
     */
    public boolean isClientError() {
        return status >= 400 && status < 500;
    }
    
    /**
     * Checks if the response is a server error (5xx status code).
     * 
     * @return true if status is 500-599
     */
    public boolean isServerError() {
        return status >= 500 && status < 600;
    }
    
    /**
     * Checks if the response has a JSON content type.
     * 
     * @return true if Content-Type is application/json
     */
    public boolean isJson() {
        String contentType = getContentType();
        return contentType != null && contentType.contains("application/json");
    }
    
    /**
     * Resets the response to default state for reuse.
     */
    public void reset() {
        this.status = 200;
        this.headers.clear();
        this.body.setLength(0);
    }
    
    @Override
    public String toString() {
        return "MockHttpResponse{" +
            "status=" + status +
            ", contentType='" + getContentType() + '\'' +
            ", bodyLength=" + body.length() +
            '}';
    }
}
