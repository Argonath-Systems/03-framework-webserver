# WebServer Framework (03-framework-webserver)

**Platform-agnostic web server abstraction for HTTP APIs**

## Overview

The WebServer Framework provides a clean abstraction layer for serving HTTP APIs from Hytale plugins, following the Argonath Systems architecture principle of **Zero Hytale Imports in Business Logic**.

This framework enables mods and tools to expose RESTful APIs without coupling to any specific HTTP server implementation. The actual server (Nitrado WebServer Plugin) is provided by the adapter layer.

## Architecture

```
┌─────────────────────────────────────────┐
│   Mods (06-mod-*)                       │
│   - HyQuest Integration                 │
│   - HyPrefab Integration                │
│   Uses: WebServerAccessor               │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│   03-framework-webserver                │
│   - WebServerAccessor (interface)       │
│   - RouteHandler (interface)            │
│   - HttpRequest/Response (wrappers)     │
│   Platform Agnostic                     │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│   02-adapter-hytale                     │
│   - NitradoWebServerAdapter             │
│   Uses: Nitrado WebServer Plugin        │
└─────────────────────────────────────────┘
```

## Key Features

- ✅ **Platform-Agnostic**: No Hytale/Nitrado imports in business logic
- ✅ **Simple API**: Easy route registration and handling
- ✅ **Type-Safe**: Strongly-typed request/response wrappers
- ✅ **Authentication**: Built-in user principal access
- ✅ **Testable**: Easy to mock for unit tests

## Usage

### Registering Routes

```java
public class MyApiMod {
    private final WebServerAccessor webServer;
    
    public void initialize(WebServerAccessor webServer) {
        this.webServer = webServer;
        
        // Register GET /api/quests
        webServer.registerRoute("/api/quests", this::listQuests);
        
        // Register GET /api/quests/:id
        webServer.registerRoute("/api/quests/:id", this::getQuest);
        
        // Register POST /api/quests
        webServer.registerRoute("/api/quests", HttpMethod.POST, this::createQuest);
    }
    
    private void listQuests(HttpRequest request, HttpResponse response) {
        // Check authentication
        if (!request.getUser().isPresent()) {
            response.setStatus(401);
            response.write("{\"error\": \"Authentication required\"}");
            return;
        }
        
        // Query database/service
        List<Quest> quests = questService.findAll();
        
        // Send JSON response
        response.setContentType("application/json");
        response.write(toJson(quests));
    }
    
    private void getQuest(HttpRequest request, HttpResponse response) {
        String questId = request.getPathParam("id");
        
        questService.findById(questId).ifPresentOrElse(
            quest -> {
                response.setContentType("application/json");
                response.write(toJson(quest));
            },
            () -> {
                response.setStatus(404);
                response.write("{\"error\": \"Quest not found\"}");
            }
        );
    }
}
```

### Cleanup

```java
public void shutdown() {
    webServer.unregisterAllRoutes(this);
}
```

## Core Interfaces

### WebServerAccessor

Main interface for web server operations:

```java
public interface WebServerAccessor {
    void registerRoute(String path, RouteHandler handler);
    void registerRoute(String path, HttpMethod method, RouteHandler handler);
    void unregisterRoute(String path);
    void unregisterAllRoutes(Object owner);
    boolean isAvailable();
    int getPort();
}
```

### RouteHandler

Functional interface for handling HTTP requests:

```java
@FunctionalInterface
public interface RouteHandler {
    void handle(HttpRequest request, HttpResponse response) throws Exception;
}
```

### HttpRequest

Request wrapper providing:
- HTTP method, path, query parameters
- Headers, body content
- Path parameters (for `/api/items/:id`)
- User authentication (optional)

### HttpResponse

Response builder providing:
- Status code setting
- Header management
- Content type setting
- Body writing

## Integration with Other Frameworks

### HyQuest Integration

```java
public class HyQuestApiController {
    private final QuestService questService;
    private final WebServerAccessor webServer;
    
    public void register() {
        webServer.registerRoute("/api/v1/quests", this::listQuests);
        webServer.registerRoute("/api/v1/quests/:id", this::getQuest);
        webServer.registerRoute("/api/v1/quests", HttpMethod.POST, this::createQuest);
        webServer.registerRoute("/api/v1/quests/:id", HttpMethod.PUT, this::updateQuest);
        webServer.registerRoute("/api/v1/quests/:id", HttpMethod.DELETE, this::deleteQuest);
    }
}
```

### HyPrefab Integration

```java
public class HyPrefabApiController {
    private final PrefabService prefabService;
    private final WebServerAccessor webServer;
    
    public void register() {
        webServer.registerRoute("/api/v1/prefabs", this::listPrefabs);
        webServer.registerRoute("/api/v1/prefabs/:id", this::getPrefab);
        webServer.registerRoute("/api/v1/prefabs", HttpMethod.POST, this::createPrefab);
    }
}
```

## Testing

```java
@Test
void testQuestApi() {
    // Mock the web server
    WebServerAccessor mockWebServer = mock(WebServerAccessor.class);
    
    // Capture registered routes
    ArgumentCaptor<RouteHandler> handlerCaptor = ArgumentCaptor.forClass(RouteHandler.class);
    when(mockWebServer.registerRoute(eq("/api/quests"), handlerCaptor.capture()));
    
    // Initialize API
    QuestApi api = new QuestApi(mockWebServer, questService);
    api.register();
    
    // Test the handler
    HttpRequest mockRequest = mock(HttpRequest.class);
    HttpResponse mockResponse = mock(HttpResponse.class);
    
    handlerCaptor.getValue().handle(mockRequest, mockResponse);
    
    verify(mockResponse).setContentType("application/json");
}
```

## Dependencies

- `02-framework-core`: Core framework utilities
- `02-framework-accessor`: Accessor pattern interfaces
- JUnit 5 (testing)
- Mockito (testing)

## Related Documentation

- [Architecture Overview](../00-Argonath-Specifications/00-Architecture/00-Overview.md)
- [Nitrado WebServer Plugin](https://github.com/nitrado/hytale-plugin-webserver)
- [HyQuest Integration](../08-lib-hyquest-api-client/README.md)
- [HyPrefab Integration](../08-lib-prefab-api-client/README.md)

## License

MIT License - See [LICENSE](../LICENSE) for details.
