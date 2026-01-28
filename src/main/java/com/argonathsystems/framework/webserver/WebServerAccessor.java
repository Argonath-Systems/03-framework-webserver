package com.argonathsystems.framework.webserver;

/**
 * Platform-agnostic web server accessor for HTTP API endpoints.
 * 
 * <p>This interface abstracts web server functionality, allowing mods and tools
 * to expose RESTful APIs without coupling to specific HTTP server implementations.
 * 
 * <p>The actual implementation (e.g., Nitrado WebServer Plugin) is provided by
 * the adapter layer (02-adapter-hytale).
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * public class QuestApiMod {
 *     private final WebServerAccessor webServer;
 *     
 *     public void initialize(WebServerAccessor webServer) {
 *         this.webServer = webServer;
 *         webServer.registerRoute("/api/quests", this::listQuests);
 *         webServer.registerRoute("/api/quests/:id", this::getQuest);
 *     }
 *     
 *     private void listQuests(HttpRequest req, HttpResponse res) {
 *         res.setContentType("application/json");
 *         res.write("[{\"id\":\"quest1\",\"name\":\"Tutorial Quest\"}]");
 *     }
 *     
 *     public void shutdown() {
 *         webServer.unregisterAllRoutes(this);
 *     }
 * }
 * }</pre>
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public interface WebServerAccessor {
    
    /**
     * Registers a route handler for GET requests.
     * 
     * <p>Routes are automatically prefixed with the plugin's namespace to avoid
     * collisions (e.g., {@code /PluginGroup/PluginName/api/quests}).
     * 
     * @param path the route path (e.g., "/api/quests")
     * @param handler the route handler
     * @throws IllegalArgumentException if path is null or empty
     * @throws IllegalStateException if web server is not available
     */
    void registerRoute(String path, RouteHandler handler);
    
    /**
     * Registers a route handler for specific HTTP method.
     * 
     * @param path the route path
     * @param method the HTTP method
     * @param handler the route handler
     * @throws IllegalArgumentException if path, method, or handler is null
     * @throws IllegalStateException if web server is not available
     */
    void registerRoute(String path, HttpMethod method, RouteHandler handler);
    
    /**
     * Unregisters a specific route.
     * 
     * @param path the route path to unregister
     */
    void unregisterRoute(String path);
    
    /**
     * Unregisters all routes registered by a specific owner.
     * 
     * <p>This should be called in plugin shutdown to clean up all routes.
     * 
     * @param owner the object that registered the routes (typically {@code this})
     */
    void unregisterAllRoutes(Object owner);
    
    /**
     * Checks if the web server is available.
     * 
     * <p>The web server may not be available if:
     * <ul>
     *   <li>The Nitrado WebServer plugin is not installed</li>
     *   <li>The server is starting up</li>
     *   <li>The server is shutting down</li>
     * </ul>
     * 
     * @return {@code true} if web server is available, {@code false} otherwise
     */
    boolean isAvailable();
    
    /**
     * Gets the port the web server is listening on.
     * 
     * @return the HTTP port number
     * @throws IllegalStateException if web server is not available
     */
    int getPort();
    
    /**
     * Gets the base URL for this plugin's routes.
     * 
     * <p>Example: {@code https://localhost:7003/MyGroup/MyPlugin}
     * 
     * @return the base URL
     * @throws IllegalStateException if web server is not available
     */
    String getBaseUrl();
}
