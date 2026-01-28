package com.argonathsystems.framework.webserver;

/**
 * Functional interface for handling HTTP requests.
 * 
 * <p>Route handlers receive a request and response object, and are expected to:
 * <ol>
 *   <li>Read data from the request (query params, headers, body)</li>
 *   <li>Process the request (query database, call services, etc.)</li>
 *   <li>Write the response (set status, headers, body)</li>
 * </ol>
 * 
 * <h2>Example:</h2>
 * <pre>{@code
 * RouteHandler getQuestHandler = (request, response) -> {
 *     String questId = request.getPathParam("id");
 *     
 *     questService.findById(questId).ifPresentOrElse(
 *         quest -> {
 *             response.setContentType("application/json");
 *             response.write(toJson(quest));
 *         },
 *         () -> {
 *             response.setStatus(404);
 *             response.write("{\"error\": \"Quest not found\"}");
 *         }
 *     );
 * };
 * 
 * webServer.registerRoute("/api/quests/:id", getQuestHandler);
 * }</pre>
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface RouteHandler {
    
    /**
     * Handles an HTTP request.
     * 
     * <p>This method is called when a request matches the registered route.
     * The handler should read from {@code request}, process the data, and
     * write to {@code response}.
     * 
     * <p>Exceptions thrown by this method will result in a 500 Internal Server Error
     * being returned to the client.
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws Exception if an error occurs during request processing
     */
    void handle(HttpRequest request, HttpResponse response) throws Exception;
}
