package com.argonathsystems.framework.webserver;

import java.util.Map;
import java.util.Optional;

/**
 * Platform-agnostic HTTP request wrapper.
 * 
 * <p>Provides access to HTTP request data without coupling to Jakarta Servlet API
 * or any specific HTTP server implementation.
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public interface HttpRequest {
    
    /**
     * Gets the HTTP method (GET, POST, etc.).
     * 
     * @return the HTTP method
     */
    HttpMethod getMethod();
    
    /**
     * Gets the request path (e.g., "/api/quests").
     * 
     * @return the request path
     */
    String getPath();
    
    /**
     * Gets a query parameter value.
     * 
     * <p>For URL {@code /api/quests?page=2&limit=10}, calling
     * {@code getQueryParam("page")} returns {@code Optional.of("2")}.
     * 
     * @param name the parameter name
     * @return the parameter value, or empty if not present
     */
    Optional<String> getQueryParam(String name);
    
    /**
     * Gets all query parameters as a map.
     * 
     * @return map of query parameter names to values
     */
    Map<String, String> getQueryParams();
    
    /**
     * Gets a path parameter value.
     * 
     * <p>For route {@code /api/quests/:id} and URL {@code /api/quests/quest123},
     * calling {@code getPathParam("id")} returns {@code "quest123"}.
     * 
     * @param name the parameter name
     * @return the parameter value, or null if not present
     */
    String getPathParam(String name);
    
    /**
     * Gets all path parameters as a map.
     * 
     * @return map of path parameter names to values
     */
    Map<String, String> getPathParams();
    
    /**
     * Gets a request header value.
     * 
     * @param name the header name (case-insensitive)
     * @return the header value, or empty if not present
     */
    Optional<String> getHeader(String name);
    
    /**
     * Gets all request headers as a map.
     * 
     * @return map of header names to values
     */
    Map<String, String> getHeaders();
    
    /**
     * Gets the request body as a string.
     * 
     * @return the request body, or empty string if no body
     */
    String getBody();
    
    /**
     * Gets the authenticated user principal.
     * 
     * <p>Returns empty if the request is not authenticated (anonymous).
     * 
     * @return the user principal, or empty if anonymous
     */
    Optional<UserPrincipal> getUser();
    
    /**
     * Gets the content type header.
     * 
     * @return the content type, or empty if not set
     */
    Optional<String> getContentType();
}
