package com.argonathsystems.framework.webserver.mock;

import com.argonathsystems.framework.webserver.HttpMethod;
import com.argonathsystems.framework.webserver.RouteHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MockWebServerAccessor}.
 */
@DisplayName("MockWebServerAccessor")
class MockWebServerAccessorTest {
    
    private MockWebServerAccessor webServer;
    
    @BeforeEach
    void setUp() {
        webServer = new MockWebServerAccessor();
    }
    
    @Nested
    @DisplayName("Default State")
    class DefaultStateTests {
        
        @Test
        @DisplayName("should be available by default")
        void shouldBeAvailableByDefault() {
            assertTrue(webServer.isAvailable());
        }
        
        @Test
        @DisplayName("should have default port 7003")
        void shouldHaveDefaultPort() {
            assertEquals(7003, webServer.getPort());
        }
        
        @Test
        @DisplayName("should have default base URL")
        void shouldHaveDefaultBaseUrl() {
            assertEquals("http://localhost:7003", webServer.getBaseUrl());
        }
        
        @Test
        @DisplayName("should have no routes initially")
        void shouldHaveNoRoutesInitially() {
            assertEquals(0, webServer.getRouteCount());
        }
    }
    
    @Nested
    @DisplayName("Route Registration")
    class RouteRegistrationTests {
        
        @Test
        @DisplayName("should register GET route by default")
        void shouldRegisterGetRouteByDefault() {
            webServer.registerRoute("/api/test", (req, res) -> {});
            
            assertTrue(webServer.hasRoute("/api/test"));
            assertTrue(webServer.hasRoute("/api/test", HttpMethod.GET));
            assertEquals(1, webServer.getRouteCount());
        }
        
        @Test
        @DisplayName("should register route with specific method")
        void shouldRegisterRouteWithSpecificMethod() {
            webServer.registerRoute("/api/test", HttpMethod.POST, (req, res) -> {});
            
            assertTrue(webServer.hasRoute("/api/test", HttpMethod.POST));
            assertFalse(webServer.hasRoute("/api/test", HttpMethod.GET));
        }
        
        @Test
        @DisplayName("should allow multiple methods for same path")
        void shouldAllowMultipleMethodsForSamePath() {
            webServer.registerRoute("/api/test", HttpMethod.GET, (req, res) -> {});
            webServer.registerRoute("/api/test", HttpMethod.POST, (req, res) -> {});
            webServer.registerRoute("/api/test", HttpMethod.DELETE, (req, res) -> {});
            
            assertEquals(3, webServer.getRouteCount());
            assertTrue(webServer.hasRoute("/api/test", HttpMethod.GET));
            assertTrue(webServer.hasRoute("/api/test", HttpMethod.POST));
            assertTrue(webServer.hasRoute("/api/test", HttpMethod.DELETE));
        }
        
        @Test
        @DisplayName("should throw when not available")
        void shouldThrowWhenNotAvailable() {
            webServer.setAvailable(false);
            
            assertThrows(IllegalStateException.class,
                () -> webServer.registerRoute("/api/test", (req, res) -> {}));
        }
    }
    
    @Nested
    @DisplayName("Route Unregistration")
    class RouteUnregistrationTests {
        
        @Test
        @DisplayName("should unregister specific route")
        void shouldUnregisterSpecificRoute() {
            webServer.registerRoute("/api/test", (req, res) -> {});
            
            webServer.unregisterRoute("/api/test");
            
            assertFalse(webServer.hasRoute("/api/test"));
            assertEquals(0, webServer.getRouteCount());
        }
        
        @Test
        @DisplayName("should unregister all routes for path")
        void shouldUnregisterAllRoutesForPath() {
            webServer.registerRoute("/api/test", HttpMethod.GET, (req, res) -> {});
            webServer.registerRoute("/api/test", HttpMethod.POST, (req, res) -> {});
            
            webServer.unregisterRoute("/api/test");
            
            assertFalse(webServer.hasRoute("/api/test", HttpMethod.GET));
            assertFalse(webServer.hasRoute("/api/test", HttpMethod.POST));
        }
        
        @Test
        @DisplayName("should unregister all routes")
        void shouldUnregisterAllRoutes() {
            webServer.registerRoute("/api/a", (req, res) -> {});
            webServer.registerRoute("/api/b", (req, res) -> {});
            webServer.registerRoute("/api/c", (req, res) -> {});
            
            webServer.unregisterAllRoutes(this);
            
            assertEquals(0, webServer.getRouteCount());
        }
    }
    
    @Nested
    @DisplayName("Request Simulation")
    class RequestSimulationTests {
        
        @Test
        @DisplayName("should invoke handler and return response")
        void shouldInvokeHandlerAndReturnResponse() {
            webServer.registerRoute("/api/test", (req, res) -> {
                res.setStatus(200);
                res.writeJson("{\"message\":\"Hello\"}");
            });
            
            MockHttpRequest request = MockHttpRequest.get("/api/test");
            MockHttpResponse response = webServer.simulateRequest(request);
            
            assertEquals(200, response.getStatus());
            assertTrue(response.isJson());
            assertEquals("{\"message\":\"Hello\"}", response.getBody());
        }
        
        @Test
        @DisplayName("should pass request data to handler")
        void shouldPassRequestDataToHandler() {
            webServer.registerRoute("/api/quests", (req, res) -> {
                String page = req.getQueryParam("page").orElse("1");
                res.writeJson("{\"page\":" + page + "}");
            });
            
            MockHttpRequest request = MockHttpRequest.builder()
                .path("/api/quests")
                .queryParam("page", "5")
                .build();
            
            MockHttpResponse response = webServer.simulateRequest(request);
            
            assertTrue(response.getBody().contains("\"page\":5"));
        }
        
        @Test
        @DisplayName("should match routes with path parameters")
        void shouldMatchRoutesWithPathParameters() {
            webServer.registerRoute("/api/quests/:id", (req, res) -> {
                res.writeJson("{\"matched\":true}");
            });
            
            MockHttpRequest request = MockHttpRequest.builder()
                .path("/api/quests/quest123")
                .pathParam("id", "quest123")
                .build();
            
            MockHttpResponse response = webServer.simulateRequest(request);
            
            assertTrue(response.isSuccess());
        }
        
        @Test
        @DisplayName("should throw for unregistered route")
        void shouldThrowForUnregisteredRoute() {
            MockHttpRequest request = MockHttpRequest.get("/api/unknown");
            
            assertThrows(IllegalArgumentException.class,
                () -> webServer.simulateRequest(request));
        }
        
        @Test
        @DisplayName("should handle handler exceptions")
        void shouldHandleHandlerExceptions() {
            webServer.registerRoute("/api/error", (req, res) -> {
                throw new RuntimeException("Test error");
            });
            
            MockHttpRequest request = MockHttpRequest.get("/api/error");
            MockHttpResponse response = webServer.simulateRequest(request);
            
            assertEquals(500, response.getStatus());
            assertTrue(response.getBody().contains("Internal Server Error"));
        }
        
        @Test
        @DisplayName("should track invocation count")
        void shouldTrackInvocationCount() {
            webServer.registerRoute("/api/test", (req, res) -> {});
            
            webServer.simulateRequest(MockHttpRequest.get("/api/test"));
            webServer.simulateRequest(MockHttpRequest.get("/api/test"));
            webServer.simulateRequest(MockHttpRequest.get("/api/test"));
            
            assertEquals(3, webServer.getInvocationCount("/api/test", HttpMethod.GET));
        }
    }
    
    @Nested
    @DisplayName("Configuration")
    class ConfigurationTests {
        
        @Test
        @DisplayName("should be chainable")
        void shouldBeChainable() {
            MockWebServerAccessor configured = new MockWebServerAccessor()
                .setAvailable(true)
                .setPort(8080)
                .setBaseUrl("http://custom:8080");
            
            assertEquals(8080, configured.getPort());
            assertEquals("http://custom:8080", configured.getBaseUrl());
        }
        
        @Test
        @DisplayName("should throw when getting port while unavailable")
        void shouldThrowWhenGettingPortWhileUnavailable() {
            webServer.setAvailable(false);
            
            assertThrows(IllegalStateException.class, webServer::getPort);
        }
        
        @Test
        @DisplayName("should throw when getting base URL while unavailable")
        void shouldThrowWhenGettingBaseUrlWhileUnavailable() {
            webServer.setAvailable(false);
            
            assertThrows(IllegalStateException.class, webServer::getBaseUrl);
        }
    }
    
    @Nested
    @DisplayName("Reset")
    class ResetTests {
        
        @Test
        @DisplayName("should clear all routes on reset")
        void shouldClearAllRoutesOnReset() {
            webServer.registerRoute("/api/a", (req, res) -> {});
            webServer.registerRoute("/api/b", (req, res) -> {});
            
            webServer.reset();
            
            assertEquals(0, webServer.getRouteCount());
        }
    }
}
