package com.argonathsystems.framework.webserver.mock;

import com.argonathsystems.framework.webserver.HttpMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MockHttpRequest}.
 */
@DisplayName("MockHttpRequest")
class MockHttpRequestTest {
    
    @Nested
    @DisplayName("Builder")
    class BuilderTests {
        
        @Test
        @DisplayName("should create GET request with defaults")
        void shouldCreateGetRequestWithDefaults() {
            MockHttpRequest request = MockHttpRequest.builder().build();
            
            assertEquals(HttpMethod.GET, request.getMethod());
            assertEquals("/", request.getPath());
            assertEquals("", request.getBody());
            assertTrue(request.getQueryParams().isEmpty());
            assertTrue(request.getPathParams().isEmpty());
            assertTrue(request.getHeaders().isEmpty());
            assertTrue(request.getUser().isEmpty());
        }
        
        @Test
        @DisplayName("should create request with all properties")
        void shouldCreateRequestWithAllProperties() {
            MockUserPrincipal user = MockUserPrincipal.player("TestPlayer");
            
            MockHttpRequest request = MockHttpRequest.builder()
                .method(HttpMethod.POST)
                .path("/api/quests")
                .queryParam("page", "1")
                .queryParam("limit", "10")
                .pathParam("id", "quest123")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer token")
                .body("{\"name\":\"Test Quest\"}")
                .user(user)
                .build();
            
            assertEquals(HttpMethod.POST, request.getMethod());
            assertEquals("/api/quests", request.getPath());
            assertEquals(Optional.of("1"), request.getQueryParam("page"));
            assertEquals(Optional.of("10"), request.getQueryParam("limit"));
            assertEquals("quest123", request.getPathParam("id"));
            assertEquals(Optional.of("application/json"), request.getHeader("Content-Type"));
            assertEquals("{\"name\":\"Test Quest\"}", request.getBody());
            assertTrue(request.getUser().isPresent());
            assertEquals("TestPlayer", request.getUser().get().getName());
        }
    }
    
    @Nested
    @DisplayName("Factory Methods")
    class FactoryMethodTests {
        
        @Test
        @DisplayName("get() should create GET request")
        void getShouldCreateGetRequest() {
            MockHttpRequest request = MockHttpRequest.get("/api/quests");
            
            assertEquals(HttpMethod.GET, request.getMethod());
            assertEquals("/api/quests", request.getPath());
        }
        
        @Test
        @DisplayName("post() should create POST request with body")
        void postShouldCreatePostRequestWithBody() {
            MockHttpRequest request = MockHttpRequest.post("/api/quests", "{\"name\":\"Test\"}");
            
            assertEquals(HttpMethod.POST, request.getMethod());
            assertEquals("/api/quests", request.getPath());
            assertEquals("{\"name\":\"Test\"}", request.getBody());
            assertEquals(Optional.of("application/json"), request.getContentType());
        }
    }
    
    @Nested
    @DisplayName("Header Lookup")
    class HeaderLookupTests {
        
        @Test
        @DisplayName("should find header case-insensitively")
        void shouldFindHeaderCaseInsensitively() {
            MockHttpRequest request = MockHttpRequest.builder()
                .header("Content-Type", "application/json")
                .build();
            
            assertEquals(Optional.of("application/json"), request.getHeader("content-type"));
            assertEquals(Optional.of("application/json"), request.getHeader("CONTENT-TYPE"));
            assertEquals(Optional.of("application/json"), request.getHeader("Content-Type"));
        }
        
        @Test
        @DisplayName("should return empty for missing header")
        void shouldReturnEmptyForMissingHeader() {
            MockHttpRequest request = MockHttpRequest.builder().build();
            
            assertTrue(request.getHeader("X-Custom-Header").isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Query and Path Params")
    class ParamTests {
        
        @Test
        @DisplayName("should return empty for missing query param")
        void shouldReturnEmptyForMissingQueryParam() {
            MockHttpRequest request = MockHttpRequest.builder().build();
            
            assertTrue(request.getQueryParam("missing").isEmpty());
        }
        
        @Test
        @DisplayName("should return null for missing path param")
        void shouldReturnNullForMissingPathParam() {
            MockHttpRequest request = MockHttpRequest.builder().build();
            
            assertNull(request.getPathParam("missing"));
        }
        
        @Test
        @DisplayName("should return immutable query params")
        void shouldReturnImmutableQueryParams() {
            MockHttpRequest request = MockHttpRequest.builder()
                .queryParam("key", "value")
                .build();
            
            assertThrows(UnsupportedOperationException.class, 
                () -> request.getQueryParams().put("new", "value"));
        }
    }
}
