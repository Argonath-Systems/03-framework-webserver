/**
 * Mock implementations of WebServer Framework interfaces for testing.
 * 
 * <p>This package provides reusable mock implementations that can be used
 * by downstream modules to test their HTTP API integrations without
 * requiring actual HTTP connections or the Nitrado plugin.
 * 
 * <h2>Available Mocks:</h2>
 * <ul>
 *   <li>{@link com.argonathsystems.framework.webserver.mock.MockWebServerAccessor} - 
 *       Simulates web server, captures routes, allows request simulation</li>
 *   <li>{@link com.argonathsystems.framework.webserver.mock.MockHttpRequest} - 
 *       Configurable HTTP request for testing handlers</li>
 *   <li>{@link com.argonathsystems.framework.webserver.mock.MockHttpResponse} - 
 *       Captures response data for verification</li>
 *   <li>{@link com.argonathsystems.framework.webserver.mock.MockUserPrincipal} - 
 *       Configurable user authentication for testing permissions</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Create mock web server
 * MockWebServerAccessor webServer = new MockWebServerAccessor();
 * 
 * // Initialize the controller under test
 * QuestApiController controller = new QuestApiController(webServer, questService);
 * controller.register();
 * 
 * // Create authenticated request
 * MockHttpRequest request = MockHttpRequest.builder()
 *     .method(HttpMethod.GET)
 *     .path("/api/quests/quest123")
 *     .pathParam("id", "quest123")
 *     .user(MockUserPrincipal.player("TestPlayer")
 *         .toBuilder()
 *         .permission("quests.read")
 *         .build())
 *     .build();
 * 
 * // Simulate request
 * MockHttpResponse response = webServer.simulateRequest(request);
 * 
 * // Verify response
 * assertEquals(200, response.getStatus());
 * assertTrue(response.isJson());
 * assertTrue(response.getBody().contains("quest123"));
 * }</pre>
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
package com.argonathsystems.framework.webserver.mock;
