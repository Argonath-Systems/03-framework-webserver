# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial WebServer Framework implementation
- `WebServerAccessor` interface for platform-agnostic HTTP API exposure
- `RouteHandler` functional interface for request handling
- `HttpRequest` wrapper for accessing request data
- `HttpResponse` builder for constructing responses
- `UserPrincipal` interface for authentication and permission checking
- `HttpMethod` enum for HTTP verb support
- Comprehensive Javadoc documentation
- README with usage examples and architecture diagrams

## [1.0.0] - 2026-01-27

### Added
- Initial release
- Platform-agnostic web server abstraction
- Zero Hytale imports in business logic
- Support for route registration and cleanup
- Built-in authentication and permission checking interfaces
- JSON response helpers
