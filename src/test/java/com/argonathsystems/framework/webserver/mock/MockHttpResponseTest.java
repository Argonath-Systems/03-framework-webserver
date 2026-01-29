package com.argonathsystems.framework.webserver.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MockHttpResponse}.
 */
@DisplayName("MockHttpResponse")
class MockHttpResponseTest {
    
    private MockHttpResponse response;
    
    @BeforeEach
    void setUp() {
        response = new MockHttpResponse();
    }
    
    @Nested
    @DisplayName("Default State")
    class DefaultStateTests {
        
        @Test
        @DisplayName("should have default status 200")
        void shouldHaveDefaultStatus200() {
            assertEquals(200, response.getStatus());
        }
        
        @Test
        @DisplayName("should have empty body")
        void shouldHaveEmptyBody() {
            assertEquals("", response.getBody());
        }
        
        @Test
        @DisplayName("should be success by default")
        void shouldBeSuccessByDefault() {
            assertTrue(response.isSuccess());
            assertFalse(response.isClientError());
            assertFalse(response.isServerError());
        }
    }
    
    @Nested
    @DisplayName("Status Codes")
    class StatusCodeTests {
        
        @Test
        @DisplayName("should identify 2xx as success")
        void shouldIdentify2xxAsSuccess() {
            for (int status : new int[]{200, 201, 204, 299}) {
                response.setStatus(status);
                assertTrue(response.isSuccess(), "Status " + status + " should be success");
            }
        }
        
        @Test
        @DisplayName("should identify 4xx as client error")
        void shouldIdentify4xxAsClientError() {
            for (int status : new int[]{400, 401, 403, 404, 499}) {
                response.setStatus(status);
                assertTrue(response.isClientError(), "Status " + status + " should be client error");
            }
        }
        
        @Test
        @DisplayName("should identify 5xx as server error")
        void shouldIdentify5xxAsServerError() {
            for (int status : new int[]{500, 501, 503, 599}) {
                response.setStatus(status);
                assertTrue(response.isServerError(), "Status " + status + " should be server error");
            }
        }
    }
    
    @Nested
    @DisplayName("Content Type")
    class ContentTypeTests {
        
        @Test
        @DisplayName("should set and get content type")
        void shouldSetAndGetContentType() {
            response.setContentType("application/json");
            
            assertEquals("application/json", response.getContentType());
            assertTrue(response.isJson());
        }
        
        @Test
        @DisplayName("should identify JSON content type with charset")
        void shouldIdentifyJsonWithCharset() {
            response.setContentType("application/json; charset=utf-8");
            
            assertTrue(response.isJson());
        }
        
        @Test
        @DisplayName("should not be JSON for other content types")
        void shouldNotBeJsonForOtherTypes() {
            response.setContentType("text/html");
            
            assertFalse(response.isJson());
        }
    }
    
    @Nested
    @DisplayName("Body Writing")
    class BodyWritingTests {
        
        @Test
        @DisplayName("should append multiple writes")
        void shouldAppendMultipleWrites() {
            response.write("Hello");
            response.write(" ");
            response.write("World");
            
            assertEquals("Hello World", response.getBody());
        }
        
        @Test
        @DisplayName("writeJson should set content type")
        void writeJsonShouldSetContentType() {
            response.writeJson("{\"key\":\"value\"}");
            
            assertEquals("application/json", response.getContentType());
            assertEquals("{\"key\":\"value\"}", response.getBody());
        }
        
        @Test
        @DisplayName("writeError should set status and JSON body")
        void writeErrorShouldSetStatusAndJsonBody() {
            response.writeError(404, "Not found");
            
            assertEquals(404, response.getStatus());
            assertTrue(response.isJson());
            assertTrue(response.getBody().contains("Not found"));
        }
    }
    
    @Nested
    @DisplayName("Headers")
    class HeaderTests {
        
        @Test
        @DisplayName("should set and get headers")
        void shouldSetAndGetHeaders() {
            response.setHeader("X-Custom", "value");
            
            assertEquals("value", response.getHeader("X-Custom"));
        }
        
        @Test
        @DisplayName("should return null for missing header")
        void shouldReturnNullForMissingHeader() {
            assertNull(response.getHeader("X-Missing"));
        }
        
        @Test
        @DisplayName("getHeaders should return immutable copy")
        void getHeadersShouldReturnImmutableCopy() {
            response.setHeader("Key", "Value");
            
            assertThrows(UnsupportedOperationException.class,
                () -> response.getHeaders().put("New", "Value"));
        }
    }
    
    @Nested
    @DisplayName("Reset")
    class ResetTests {
        
        @Test
        @DisplayName("should reset to default state")
        void shouldResetToDefaultState() {
            response.setStatus(404);
            response.setContentType("text/html");
            response.write("Some body");
            
            response.reset();
            
            assertEquals(200, response.getStatus());
            assertNull(response.getContentType());
            assertEquals("", response.getBody());
        }
    }
}
