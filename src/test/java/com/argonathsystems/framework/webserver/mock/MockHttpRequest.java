package com.argonathsystems.framework.webserver.mock;

import com.argonathsystems.framework.webserver.HttpMethod;
import com.argonathsystems.framework.webserver.HttpRequest;
import com.argonathsystems.framework.webserver.UserPrincipal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Mock implementation of {@link HttpRequest} for testing.
 * 
 * <p>This class provides a fully configurable HTTP request that can be used
 * in unit tests without requiring actual HTTP connections.
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * MockHttpRequest request = MockHttpRequest.builder()
 *     .method(HttpMethod.GET)
 *     .path("/api/quests/quest123")
 *     .pathParam("id", "quest123")
 *     .queryParam("include", "details")
 *     .header("Authorization", "Bearer token123")
 *     .user(MockUserPrincipal.player("TestPlayer"))
 *     .build();
 * 
 * assertEquals("quest123", request.getPathParam("id"));
 * assertEquals(Optional.of("details"), request.getQueryParam("include"));
 * }</pre>
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public final class MockHttpRequest implements HttpRequest {
    
    private final HttpMethod method;
    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> pathParams;
    private final Map<String, String> headers;
    private final String body;
    private final UserPrincipal user;
    
    private MockHttpRequest(Builder builder) {
        this.method = builder.method;
        this.path = builder.path;
        this.queryParams = Collections.unmodifiableMap(new HashMap<>(builder.queryParams));
        this.pathParams = Collections.unmodifiableMap(new HashMap<>(builder.pathParams));
        this.headers = Collections.unmodifiableMap(new HashMap<>(builder.headers));
        this.body = builder.body;
        this.user = builder.user;
    }
    
    /**
     * Creates a builder for MockHttpRequest.
     * 
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Creates a simple GET request for the given path.
     * 
     * @param path the request path
     * @return a new MockHttpRequest
     */
    public static MockHttpRequest get(String path) {
        return builder().method(HttpMethod.GET).path(path).build();
    }
    
    /**
     * Creates a simple POST request with the given path and body.
     * 
     * @param path the request path
     * @param body the request body
     * @return a new MockHttpRequest
     */
    public static MockHttpRequest post(String path, String body) {
        return builder()
            .method(HttpMethod.POST)
            .path(path)
            .body(body)
            .header("Content-Type", "application/json")
            .build();
    }
    
    @Override
    public HttpMethod getMethod() {
        return method;
    }
    
    @Override
    public String getPath() {
        return path;
    }
    
    @Override
    public Optional<String> getQueryParam(String name) {
        return Optional.ofNullable(queryParams.get(name));
    }
    
    @Override
    public Map<String, String> getQueryParams() {
        return queryParams;
    }
    
    @Override
    public String getPathParam(String name) {
        return pathParams.get(name);
    }
    
    @Override
    public Map<String, String> getPathParams() {
        return pathParams;
    }
    
    @Override
    public Optional<String> getHeader(String name) {
        // Case-insensitive header lookup
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }
    
    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    @Override
    public String getBody() {
        return body;
    }
    
    @Override
    public Optional<UserPrincipal> getUser() {
        return Optional.ofNullable(user);
    }
    
    @Override
    public Optional<String> getContentType() {
        return getHeader("Content-Type");
    }
    
    @Override
    public String toString() {
        return "MockHttpRequest{" +
            "method=" + method +
            ", path='" + path + '\'' +
            ", queryParams=" + queryParams +
            ", pathParams=" + pathParams +
            ", hasBody=" + (body != null && !body.isEmpty()) +
            ", authenticated=" + (user != null) +
            '}';
    }
    
    /**
     * Builder for {@link MockHttpRequest}.
     */
    public static final class Builder {
        private HttpMethod method = HttpMethod.GET;
        private String path = "/";
        private final Map<String, String> queryParams = new HashMap<>();
        private final Map<String, String> pathParams = new HashMap<>();
        private final Map<String, String> headers = new HashMap<>();
        private String body = "";
        private UserPrincipal user;
        
        private Builder() {}
        
        /**
         * Sets the HTTP method.
         * 
         * @param method the HTTP method
         * @return this builder
         */
        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }
        
        /**
         * Sets the request path.
         * 
         * @param path the request path
         * @return this builder
         */
        public Builder path(String path) {
            this.path = path;
            return this;
        }
        
        /**
         * Adds a query parameter.
         * 
         * @param name the parameter name
         * @param value the parameter value
         * @return this builder
         */
        public Builder queryParam(String name, String value) {
            this.queryParams.put(name, value);
            return this;
        }
        
        /**
         * Adds a path parameter.
         * 
         * @param name the parameter name
         * @param value the parameter value
         * @return this builder
         */
        public Builder pathParam(String name, String value) {
            this.pathParams.put(name, value);
            return this;
        }
        
        /**
         * Adds a header.
         * 
         * @param name the header name
         * @param value the header value
         * @return this builder
         */
        public Builder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }
        
        /**
         * Sets the request body.
         * 
         * @param body the request body
         * @return this builder
         */
        public Builder body(String body) {
            this.body = body;
            return this;
        }
        
        /**
         * Sets the authenticated user.
         * 
         * @param user the user principal
         * @return this builder
         */
        public Builder user(UserPrincipal user) {
            this.user = user;
            return this;
        }
        
        /**
         * Builds the MockHttpRequest.
         * 
         * @return a new MockHttpRequest instance
         */
        public MockHttpRequest build() {
            return new MockHttpRequest(this);
        }
    }
}
