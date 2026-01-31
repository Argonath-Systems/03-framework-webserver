# Framework Webserver - Implementation Tracking

> **Module**: `03-framework-webserver`  
> **Status**: 🟢 COMPLETE (Interfaces + Mocks + Nitrado Adapter + Auth)  
> **Last Updated**: 2026-01-31  
> **Version**: 1.1.0

---

## Overview

The Framework Webserver provides a **platform-agnostic HTTP API abstraction layer** enabling mods and tools to expose RESTful endpoints without coupling to specific HTTP server implementations. The actual server (Nitrado WebServer Plugin) is provided by the adapter layer (`02-adapter-hytale`).

**Architecture Decision**: Uses Nitrado WebServer Plugin (shared HTTP server) instead of embedded servers like Javalin/Jetty. This provides authentication integration, shared resources, and TLS support out-of-the-box.

---

## Implementation Summary

| Category | Complete | Total | Percentage |
|----------|----------|-------|------------|
| HTTP Interfaces | 6 | 6 | 100% |
| Mock Implementations | 4 | 4 | 100% |
| Unit Tests | 4 | 4 | 100% |
| Auth System | 8 | 8 | 100% |
| WebSocket Support | 0 | 3 | 0% |
| **Overall** | **22** | **25** | **88%** |

---

## Component Matrix

### Interfaces (Complete)

| Component | Class | Status | Description |
|-----------|-------|--------|-------------|
| HTTP Method | `HttpMethod` | ✅ | GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS enum |
| HTTP Request | `HttpRequest` | ✅ | Request abstraction with query/path params, headers, body, user |
| HTTP Response | `HttpResponse` | ✅ | Response builder with status, headers, JSON support |
| Route Handler | `RouteHandler` | ✅ | Functional interface for request handling |
| WebServer Accessor | `WebServerAccessor` | ✅ | Accessor interface for route management |
| User Principal | `UserPrincipal` | ✅ | Authentication/authorization abstraction |

### Auth System (Complete - Phase 6)

| Component | Class | Status | Description |
|-----------|-------|--------|-------------|
| Permission Enum | `DesignerPermission` | ✅ | Bitfield permissions for Designer portal |
| Role Enum | `DesignerRole` | ✅ | Role presets (Quest Designer, Prefab Builder, etc.) |
| JWT Service | `JwtService` | ✅ | Token generation and validation |
| Discord OAuth | `DiscordOAuthService` | ✅ | Discord OAuth2 integration |
| User Entity | `DesignerUser` | ✅ | Designer portal user with Discord/player linking |
| Link Code Manager | `LinkCodeManager` | ✅ | In-game account linking codes |
| Auth Controller | `AuthController` | ✅ | REST endpoints for auth flow |
| Link Command | `DesignerLinkCommand` | ✅ | `/designer link` in-game command |

### Auth DTOs (Complete)

| Component | Class | Status | Description |
|-----------|-------|--------|-------------|
| Discord Callback | `DiscordCallbackRequest` | ✅ | OAuth callback request |
| Auth Response | `AuthResponse` | ✅ | User + tokens response |
| Auth User | `AuthUser` | ✅ | User details DTO |
| Auth Tokens | `AuthTokens` | ✅ | JWT token pair |
| Refresh Request | `RefreshRequest` | ✅ | Token refresh request |
| Link Code Response | `LinkCodeResponse` | ✅ | Generated link code |
| Link Status | `LinkStatusResponse` | ✅ | Account link status |

### Mock Implementations (Complete)

| Component | Class | Status | Description |
|-----------|-------|--------|-------------|
| Mock Request | `MockHttpRequest` | ✅ | Builder pattern, factory methods (get/post) |
| Mock Response | `MockHttpResponse` | ✅ | Captures response data, status helpers |
| Mock WebServer | `MockWebServerAccessor` | ✅ | Route capture, request simulation |
| Mock Principal | `MockUserPrincipal` | ✅ | Permission testing, wildcard support |

### Unit Tests (Complete)

| Component | Test Class | Status | Description |
|-----------|------------|--------|-------------|
| Mock Request | `MockHttpRequestTest` | ✅ | Builder, factory, header tests |
| Mock Response | `MockHttpResponseTest` | ✅ | Status, JSON, error tests |
| Mock WebServer | `MockWebServerAccessorTest` | ✅ | Route, simulation tests |
| Mock Principal | `MockUserPrincipalTest` | ✅ | Permission, wildcard tests |

### Nitrado Adapter (Enabled)

| Component | Class | Status | Description |
|-----------|-------|--------|-------------|
| Nitrado Adapter | `NitradoWebServerAdapter` | ✅ | Nitrado WebServer Plugin integration (ENABLED - compiles successfully) |
| HyQuest Controller | `HyQuestApiController` | ⏸️ | Quest API (disabled - needs API method mapping updates) |
| HyPrefab Controller | `HyPrefabApiController` | ✅ | Prefab API (ENABLED - uses typed PrefabService) |
| Prefab Service | `PrefabService` | ✅ | Typed prefab operations interface |

### WebSocket (Not Started)

| Component | Class | Status | Description |
|-----------|-------|--------|-------------|
| WebSocket Server | `WebSocketServer` | ⬜ | Connection management |
| Session Manager | `SessionManager` | ⬜ | Client session tracking |
| Message Handler | `MessageHandler` | ⬜ | Incoming message routing |

### Security (Not Started)

| Component | Class | Status | Description |
|-----------|-------|--------|-------------|
| Auth Provider | `AuthProvider` | ⬜ | Token/session authentication |
| CORS Handler | `CorsHandler` | ⬜ | Cross-origin support |
| Rate Limiter | `HttpRateLimiter` | ⬜ | Request throttling |

---

## Package Structure

```
com.argonathsystems.framework.webserver/
├── HttpMethod.java                 ✅ Complete (7 methods)
├── HttpRequest.java                ✅ Complete
├── HttpResponse.java               ✅ Complete
├── RouteHandler.java               ✅ Complete
├── UserPrincipal.java              ✅ Complete (auth abstraction)
├── WebServerAccessor.java          ✅ Complete
├── mock/
│   ├── package-info.java           ✅ Complete
│   ├── MockHttpRequest.java        ✅ Complete (builder + factory)
│   ├── MockHttpResponse.java       ✅ Complete (capture + helpers)
│   ├── MockUserPrincipal.java      ✅ Complete (permission + wildcard)
│   └── MockWebServerAccessor.java  ✅ Complete (route + simulation)
├── websocket/
│   ├── WebSocketServer.java        ⬜ Not Started
│   ├── SessionManager.java         ⬜ Not Started
│   └── MessageHandler.java         ⬜ Not Started
└── security/
    ├── AuthProvider.java           ⬜ Not Started
    ├── CorsHandler.java            ⬜ Not Started
    └── HttpRateLimiter.java        ⬜ Not Started

test/
└── com.argonathsystems.framework.webserver.mock/
    ├── MockHttpRequestTest.java    ✅ Complete
    ├── MockHttpResponseTest.java   ✅ Complete
    ├── MockUserPrincipalTest.java  ✅ Complete
    └── MockWebServerAccessorTest.java ✅ Complete
```

---

## Source Statistics

| Metric | Value |
|--------|-------|
| Source Files | 10 |
| Test Files | 4 |
| Lines of Code | ~850 |

---

## Adapter Status

The Nitrado WebServer adapter is **ENABLED** and compiling successfully in `02-adapter-hytale`:

| Component | Status | Notes |
|-----------|--------|-------|
| **NitradoWebServerAdapter** | ✅ Enabled | Fixed import path (`authentication` not `auth`), fixed `PluginBase` types |
| **PrefabService** | ✅ Enabled | Typed interface with `PrefabData`, `SpawnRequest`, `SpawnResult` records |
| **HyPrefabApiController** | ✅ Enabled | Uses typed `PrefabService` interface (no `Object` usage) |
| **HyQuestApiController** | ⏸️ Disabled | Needs API method name mapping (`questsGet` vs `listQuests` etc.) |

### Resolved Dependencies:

| Dependency | Status | Location |
|------------|--------|----------|
| Nitrado WebServer Plugin | ✅ Available | `externals/nitrado-webserver-1.0.0.jar` |
| Nitrado Authentication | ✅ Fixed | `net.nitrado.hytale.plugins.webserver.authentication.HytaleUserPrincipal` |
| HyQuest API Client | ✅ Installed | `~/.m2/repository/com/argonathsystems/lib/hyquest-api-client/1.0.0-SNAPSHOT/` |
| PluginBase Type | ✅ Fixed | Changed from `Object` to `com.hypixel.hytale.server.core.plugin.PluginBase` |

---

## Roadmap

| Version | Target | Features |
|---------|--------|----------|
| 1.0.0 | ✅ Current | Interface definitions, mocks, unit tests |
| 1.1.0 | Q2 2026 | WebSocket support |
| 1.2.0 | Q2 2026 | Security (CORS, Rate Limiting) |
| 2.0.0 | Q3 2026 | Full production ready |

---

## Changelog

### v1.0.0 (2026-01-29)
- HTTP interface definitions (6 interfaces)
- Mock implementations for testing (4 mocks)
- Unit test coverage for all mocks
- Library catalog entry (LIB-046)
- Nitrado JAR installed to local Maven
- Adapter files prepared (disabled pending external dependencies)
- Replaced all `Object` usages with typed interfaces
