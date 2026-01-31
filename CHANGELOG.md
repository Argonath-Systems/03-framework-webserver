# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.1.0] - 2026-01-31

### Added
- **Designer Portal Authentication System** (Phase 6):
  - `DesignerPermission` - Bitfield permission enum (synced with TypeScript)
  - `DesignerRole` - Role presets (Quest Designer, Prefab Builder, Worldgen Builder, etc.)
  - `JwtService` - JWT token generation and validation using JJWT
  - `DiscordOAuthService` - Discord OAuth2 integration for authentication
  - `DesignerUser` - User entity with Discord account + in-game player linking
  - `LinkCodeManager` - Temporary link codes for in-game account linking
  - `AuthController` - REST endpoints for auth flow (/api/v1/auth/*)
  - `DesignerLinkCommand` - `/designer link <code>` in-game command
- **Auth DTOs**:
  - `DiscordCallbackRequest` - OAuth callback request
  - `AuthResponse` - User + tokens response
  - `AuthUser` - User details for API
  - `AuthTokens` - JWT token pair
  - `RefreshRequest` - Token refresh request
  - `LinkCodeResponse` - Generated link code response
  - `LinkStatusResponse` - Account link status
- JJWT dependency for JWT token handling
- Gson dependency for JSON serialization

### Security
- JWT-based authentication with access and refresh tokens
- Discord role-based authorization mapping
- Secure link code generation (6-char, unambiguous characters, 5-min expiry)
- Permission bitfield system matching frontend implementation

## [1.0.0] - 2026-01-29

### Added
- Initial WebServer Framework implementation
- `WebServerAccessor` interface for platform-agnostic HTTP API exposure
- `RouteHandler` functional interface for request handling
- `HttpRequest` wrapper for accessing request data (query params, path params, headers, body, user)
- `HttpResponse` builder for constructing responses (status, headers, JSON, error helpers)
- `UserPrincipal` interface for authentication and permission checking
- `HttpMethod` enum for HTTP verb support (GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS)
- **Mock implementations** for testing:
  - `MockHttpRequest` with builder pattern and factory methods
  - `MockHttpResponse` with response capture and status helpers
  - `MockWebServerAccessor` with route capture and request simulation
  - `MockUserPrincipal` with permission testing and wildcard support
- **Unit test coverage** for all mock implementations
- **Nitrado adapter integration** (02-adapter-hytale):
  - `NitradoWebServerAdapter` - Production-ready Nitrado WebServer Plugin adapter
  - `HyPrefabApiController` - REST API for HyPrefab visual designer (typed with `PrefabService`)
  - `PrefabService` - Typed interface for prefab operations
- Comprehensive Javadoc documentation
- README with usage examples and architecture diagrams
- Library catalog entry (LIB-046)

### Fixed
- Eliminated all `Object` usages in adapter layer with typed interfaces
- Fixed Nitrado authentication import path (`authentication` not `auth`)
- Fixed `PluginBase` type casting (changed from `Object` to proper type)
- Installed HyQuest API client dependency to local Maven repository
