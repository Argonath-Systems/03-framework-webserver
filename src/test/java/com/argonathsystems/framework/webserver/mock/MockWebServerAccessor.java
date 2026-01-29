package com.argonathsystems.framework.webserver.mock;

import com.argonathsystems.framework.webserver.HttpMethod;
import com.argonathsystems.framework.webserver.HttpRequest;
import com.argonathsystems.framework.webserver.HttpResponse;
import com.argonathsystems.framework.webserver.RouteHandler;
import com.argonathsystems.framework.webserver.WebServerAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock implementation of {@link WebServerAccessor} for testing.
 * 
 * <p>This class captures all registered routes and allows simulating
 * HTTP requests against them in unit tests.
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * MockWebServerAccessor webServer = new MockWebServerAccessor();
 * 
 * // Register routes (typically done by code under test)
 * MyApiController controller = new MyApiController(webServer);
 * controller.register();
 * 
 * // Verify route was registered
 * assertTrue(webServer.hasRoute("/api/quests", HttpMethod.GET));
 * 
 * // Simulate a request
 * MockHttpRequest request = MockHttpRequest.get("/api/quests");
 * MockHttpResponse response = webServer.simulateRequest(request);
 * 
 * assertEquals(200, response.getStatus());
 * }</pre>
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public final class MockWebServerAccessor implements WebServerAccessor {
    
    private final Map<RouteKey, RouteHandler> routes = new ConcurrentHashMap<>();
    private final Map<RouteKey, List<RouteInvocation>> invocations = new ConcurrentHashMap<>();
    private boolean available = true;
    private int port = 7003;
    private String baseUrl = "http://localhost:7003";
    
    /**
     * Creates a new MockWebServerAccessor.
     */
    public MockWebServerAccessor() {
        // Default configuration
    }
    
    @Override
    public void registerRoute(String path, RouteHandler handler) {
        registerRoute(path, HttpMethod.GET, handler);
    }
    
    @Override
    public void registerRoute(String path, HttpMethod method, RouteHandler handler) {
        if (!available) {
            throw new IllegalStateException("Web server is not available");
        }
        
        RouteKey key = new RouteKey(path, method);
        routes.put(key, handler);
        invocations.put(key, new ArrayList<>());
    }
    
    @Override
    public void unregisterRoute(String path) {
        routes.keySet().removeIf(key -> key.path.equals(path));
        invocations.keySet().removeIf(key -> key.path.equals(path));
    }
    
    @Override
    public void unregisterAllRoutes(Object owner) {
        routes.clear();
        invocations.clear();
    }
    
    @Override
    public boolean isAvailable() {
        return available;
    }
    
    @Override
    public int getPort() {
        if (!available) {
            throw new IllegalStateException("Web server is not available");
        }
        return port;
    }
    
    @Override
    public String getBaseUrl() {
        if (!available) {
            throw new IllegalStateException("Web server is not available");
        }
        return baseUrl;
    }
    
    // =========================================================================
    // Test Configuration Methods
    // =========================================================================
    
    /**
     * Sets whether the web server is available.
     * 
     * @param available true if available, false otherwise
     * @return this for chaining
     */
    public MockWebServerAccessor setAvailable(boolean available) {
        this.available = available;
        return this;
    }
    
    /**
     * Sets the port number.
     * 
     * @param port the port number
     * @return this for chaining
     */
    public MockWebServerAccessor setPort(int port) {
        this.port = port;
        return this;
    }
    
    /**
     * Sets the base URL.
     * 
     * @param baseUrl the base URL
     * @return this for chaining
     */
    public MockWebServerAccessor setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }
    
    // =========================================================================
    // Test Inspection Methods
    // =========================================================================
    
    /**
     * Checks if a route is registered.
     * 
     * @param path the route path
     * @param method the HTTP method
     * @return true if the route is registered
     */
    public boolean hasRoute(String path, HttpMethod method) {
        return routes.containsKey(new RouteKey(path, method));
    }
    
    /**
     * Checks if a GET route is registered.
     * 
     * @param path the route path
     * @return true if the GET route is registered
     */
    public boolean hasRoute(String path) {
        return hasRoute(path, HttpMethod.GET);
    }
    
    /**
     * Gets the handler for a specific route.
     * 
     * @param path the route path
     * @param method the HTTP method
     * @return the handler, or null if not registered
     */
    public RouteHandler getHandler(String path, HttpMethod method) {
        return routes.get(new RouteKey(path, method));
    }
    
    /**
     * Gets the number of registered routes.
     * 
     * @return the route count
     */
    public int getRouteCount() {
        return routes.size();
    }
    
    /**
     * Gets all registered route paths.
     * 
     * @return list of route keys
     */
    public List<RouteKey> getRegisteredRoutes() {
        return new ArrayList<>(routes.keySet());
    }
    
    // =========================================================================
    // Request Simulation
    // =========================================================================
    
    /**
     * Simulates an HTTP request against registered routes.
     * 
     * <p>This method finds the matching route and invokes its handler,
     * returning the captured response.
     * 
     * @param request the request to simulate
     * @return the response from the handler
     * @throws IllegalArgumentException if no matching route is found
     * @throws RuntimeException if the handler throws an exception
     */
    public MockHttpResponse simulateRequest(MockHttpRequest request) {
        RouteHandler handler = findHandler(request.getPath(), request.getMethod());
        if (handler == null) {
            throw new IllegalArgumentException(
                "No route registered for " + request.getMethod() + " " + request.getPath());
        }
        
        MockHttpResponse response = new MockHttpResponse();
        
        try {
            handler.handle(request, response);
        } catch (Exception e) {
            response.setStatus(500);
            response.writeError(500, "Internal Server Error: " + e.getMessage());
        }
        
        // Record invocation
        RouteKey key = new RouteKey(request.getPath(), request.getMethod());
        invocations.computeIfAbsent(key, k -> new ArrayList<>())
            .add(new RouteInvocation(request, response));
        
        return response;
    }
    
    /**
     * Finds a handler for the given path and method.
     * Supports path parameter matching (e.g., /api/quests/:id).
     */
    private RouteHandler findHandler(String path, HttpMethod method) {
        // First try exact match
        RouteHandler exact = routes.get(new RouteKey(path, method));
        if (exact != null) {
            return exact;
        }
        
        // Try pattern matching for path parameters
        for (Map.Entry<RouteKey, RouteHandler> entry : routes.entrySet()) {
            if (entry.getKey().method == method && matchesPattern(entry.getKey().path, path)) {
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    /**
     * Checks if a path matches a pattern with path parameters.
     */
    private boolean matchesPattern(String pattern, String path) {
        String[] patternParts = pattern.split("/");
        String[] pathParts = path.split("/");
        
        if (patternParts.length != pathParts.length) {
            return false;
        }
        
        for (int i = 0; i < patternParts.length; i++) {
            if (patternParts[i].startsWith(":")) {
                continue; // Parameter placeholder matches anything
            }
            if (!patternParts[i].equals(pathParts[i])) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Gets the number of times a route was invoked.
     * 
     * @param path the route path
     * @param method the HTTP method
     * @return the invocation count
     */
    public int getInvocationCount(String path, HttpMethod method) {
        List<RouteInvocation> list = invocations.get(new RouteKey(path, method));
        return list == null ? 0 : list.size();
    }
    
    /**
     * Clears all registered routes and invocation history.
     */
    public void reset() {
        routes.clear();
        invocations.clear();
    }
    
    // =========================================================================
    // Inner Classes
    // =========================================================================
    
    /**
     * Represents a unique route identifier.
     */
    public record RouteKey(String path, HttpMethod method) {
        @Override
        public String toString() {
            return method + " " + path;
        }
    }
    
    /**
     * Records a single route invocation for verification.
     */
    public record RouteInvocation(HttpRequest request, HttpResponse response) {}
}
