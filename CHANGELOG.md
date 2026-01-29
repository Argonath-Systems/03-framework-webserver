# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
- Comprehensive Javadoc documentation
- README with usage examples and architecture diagrams
- Library catalog entry (LIB-046)

### Fixed
- Eliminated all `Object` usages in adapter layer with typed interfaces
