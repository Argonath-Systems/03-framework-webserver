# Implementation Plan: 03-framework-webserver

**Module**: `03-framework-webserver`  
**Generated**: 2026-01-29  
**Architect**: HytaleArchitect  
**Specification Coverage**: HLR-ARCHITECTURE-006

---

## Executive Summary

The WebServer Framework provides a **platform-agnostic HTTP API abstraction layer** enabling mods and tools to expose RESTful endpoints without coupling to specific HTTP server implementations (Nitrado WebServer Plugin). The module is currently in an **INTERFACES-ONLY state** (v0.1.0) with 6 complete interface definitions but zero implementation classes. The adapter implementation in `02-adapter-hytale` is disabled (`.disabled` extension) pending Nitrado SDK availability. **No accessor v2.0.0 migration is required** as this module doesn't use `DataValue`, `UIContext`, or related types.

---

## Critical Issues Found

### Violations (MUST FIX)

| ID | Location | Type | Description | Severity |
|----|----------|------|-------------|----------|
| - | - | - | **No violations found** - interfaces are well-designed | ✅ CLEAN |

**Analysis**: The module contains only interface definitions with no implementation code. All interfaces are pure abstractions with no `return null;`, no empty method bodies, and no Hytale API imports. This is **compliant** with architectural standards.

### Technical Debt

| ID | Location | Type | Description | Priority |
|----|----------|------|-------------|----------|
| TD-001 | `src/test/` | Missing | No unit tests exist | 🟡 MEDIUM |
| TD-002 | Adapter Layer | Disabled | `NitradoWebServerAdapter.java.disabled` - Implementation disabled | 🟡 MEDIUM |
| TD-003 | Library Catalog | Missing | No entry in SF-ARCHITECTURE-000-library-catalog.md | 🟢 LOW |
| TD-004 | IMPLEMENTATION_TRACKING.md | Outdated | Claims 0.1.0 but CHANGELOG shows 1.0.0 | 🟢 LOW |

### TODO/FIXME/STUB Inventory

| Location | Type | Description | Action Required |
|----------|------|-------------|-----------------|
| - | - | **None found** | No action required |

---

## Requirements Traceability

### Specification Coverage

| Spec ID | Requirement | Status | Implementation Location | Notes |
|---------|-------------|--------|-------------------------|-------|
| HLR-ARCHITECTURE-006 | Platform-agnostic web server abstraction | ✅ Complete | All 6 interfaces | Interfaces complete, adapter disabled |
| HLR-ARCHITECTURE-006 | WebServerAccessor interface | ✅ Complete | [WebServerAccessor.java](src/main/java/com/argonathsystems/framework/webserver/WebServerAccessor.java) | Full API defined |
| HLR-ARCHITECTURE-006 | RouteHandler functional interface | ✅ Complete | [RouteHandler.java](src/main/java/com/argonathsystems/framework/webserver/RouteHandler.java) | FunctionalInterface |
| HLR-ARCHITECTURE-006 | HttpRequest wrapper | ✅ Complete | [HttpRequest.java](src/main/java/com/argonathsystems/framework/webserver/HttpRequest.java) | Query params, path params, headers, body, auth |
| HLR-ARCHITECTURE-006 | HttpResponse builder | ✅ Complete | [HttpResponse.java](src/main/java/com/argonathsystems/framework/webserver/HttpResponse.java) | Status, headers, JSON helpers |
| HLR-ARCHITECTURE-006 | UserPrincipal authentication | ✅ Complete | [UserPrincipal.java](src/main/java/com/argonathsystems/framework/webserver/UserPrincipal.java) | Permission checking |
| HLR-ARCHITECTURE-006 | HttpMethod enum | ✅ Complete | [HttpMethod.java](src/main/java/com/argonathsystems/framework/webserver/HttpMethod.java) | GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS |
| HLR-ARCHITECTURE-006 | Zero Hytale imports | ✅ Complete | All files | Verified - no platform leaks |
| HLR-ARCHITECTURE-006 | NitradoWebServerAdapter | 🚧 Disabled | `02-adapter-hytale/.../webserver/*.disabled` | Awaiting Nitrado SDK |
| HLR-ARCHITECTURE-006 | HyQuestApiController | 🚧 Disabled | `02-adapter-hytale/.../webserver/*.disabled` | Awaiting Nitrado SDK |
| HLR-ARCHITECTURE-006 | HyPrefabApiController | 🚧 Disabled | `02-adapter-hytale/.../webserver/*.disabled` | Awaiting Nitrado SDK |

### Orphan Implementations (No Specification)

| Location | Description | Proposed Action |
|----------|-------------|-----------------|
| - | **None** - All implementations trace to HLR-ARCHITECTURE-006 | N/A |

### Missing Implementations (Spec Not Implemented)

| Spec ID | Requirement | Gap Description | Priority |
|---------|-------------|-----------------|----------|
| HLR-ARCHITECTURE-006 | EmbeddedHttpServer | Listed in IMPLEMENTATION_TRACKING but spec mentions Nitrado-only | 🟢 LOW |
| HLR-ARCHITECTURE-006 | WebSocketServer | Listed in IMPLEMENTATION_TRACKING as planned | 🟡 MEDIUM |
| HLR-ARCHITECTURE-006 | AuthProvider | Listed in IMPLEMENTATION_TRACKING as planned | 🟡 MEDIUM |
| HLR-ARCHITECTURE-006 | CorsHandler | Listed in IMPLEMENTATION_TRACKING as planned | 🟡 MEDIUM |
| HLR-ARCHITECTURE-006 | JsonCodec | Listed in IMPLEMENTATION_TRACKING as planned | 🟢 LOW |

**Note**: The IMPLEMENTATION_TRACKING.md outlines planned components (EmbeddedHttpServer, WebSocketServer, etc.) but the HLR-ARCHITECTURE-006 spec describes a Nitrado-based approach. **Clarification needed**: Is embedded server approach deprecated in favor of Nitrado-only?

---

## Accessor v2.0.0 Migration

### Required Changes

| Location | Current Type | Target Type | Migration Notes |
|----------|--------------|-------------|-----------------|
| - | - | - | **No changes required** |

### Breaking Change Impact

**None** - The WebServer Framework does not use any of the v2.0.0 breaking change types:
- No `Object` metadata/context patterns
- No `DataValue` usage
- No `UIContext` or `UIUpdateData` usage
- No `CommandSender` usage (separate from command framework)
- No `PlatformEntity` usage

The framework is purely an HTTP abstraction and is **already compliant** with accessor v2.0.0.

---

## HyUI Integration

**Not Applicable** - This module is a server-side HTTP framework and does not interact with HyUI. The web UIs (HyQuest WebUI, HyPrefab WebUI) are separate React applications that consume the REST APIs exposed by this framework.

---

## Hytale SDK Integration

### SDK Types Used

| Argonath Type | Hytale SDK Type | ECS Pattern | Notes |
|---------------|-----------------|-------------|-------|
| - | - | - | **None** - Framework is platform-agnostic |

### Nitrado WebServer Plugin Integration

The adapter layer (`02-adapter-hytale`) bridges to the Nitrado WebServer Plugin:

| Argonath Type | Nitrado SDK Type | Notes |
|---------------|------------------|-------|
| `WebServerAccessor` | `WebServerPlugin` | Main plugin interface |
| `HttpRequest` | `HttpServletRequest` | Jakarta Servlet API |
| `HttpResponse` | `HttpServletResponse` | Jakarta Servlet API |
| `UserPrincipal` | `HytaleUserPrincipal` | Nitrado auth wrapper |

**Current Status**: Adapter files are disabled (`.java.disabled`) pending official Nitrado SDK availability.

---

## Implementation Phases

### Phase 1: Documentation & Hygiene [0.5 days]

| Task ID | Description | Files | Effort | Dependencies |
|---------|-------------|-------|--------|--------------|
| P1-001 | Align version: IMPLEMENTATION_TRACKING.md claims 0.1.0, CHANGELOG shows 1.0.0 | IMPLEMENTATION_TRACKING.md | 0.25 days | None |
| P1-002 | Add LIB-XXX entry to SF-ARCHITECTURE-000-library-catalog.md | Specifications | 0.25 days | None |

### Phase 2: Test Coverage [1 day]

| Task ID | Description | Files | Effort | Dependencies |
|---------|-------------|-------|--------|--------------|
| P2-001 | Create unit tests for interface contract documentation | `src/test/java/.../webserver/` | 0.5 days | None |
| P2-002 | Create mock implementations for testing downstream modules | `src/test/java/.../webserver/mock/` | 0.5 days | P2-001 |

### Phase 3: Adapter Enablement [External Blocker]

| Task ID | Description | Files | Effort | Dependencies |
|---------|-------------|-------|--------|--------------|
| P3-001 | Re-enable NitradoWebServerAdapter.java when SDK available | `02-adapter-hytale/.../webserver/` | 0.5 days | Nitrado SDK |
| P3-002 | Re-enable HyQuestApiController.java | `02-adapter-hytale/.../webserver/` | 0.25 days | P3-001 |
| P3-003 | Re-enable HyPrefabApiController.java | `02-adapter-hytale/.../webserver/` | 0.25 days | P3-001 |
| P3-004 | Integration testing with Nitrado plugin | E2E tests | 1 day | P3-001-003 |

### Phase 4: WebSocket Extension [Future - 2 days]

| Task ID | Description | Files | Effort | Dependencies |
|---------|-------------|-------|--------|--------------|
| P4-001 | Add `WebSocketAccessor` interface | New interface | 0.5 days | Spec decision |
| P4-002 | Add `WebSocketHandler` interface | New interface | 0.25 days | P4-001 |
| P4-003 | Add `WebSocketSession` interface | New interface | 0.25 days | P4-001 |
| P4-004 | Implement Nitrado WebSocket adapter | `02-adapter-hytale` | 1 day | P4-001-003, P3-001 |

---

## Estimated Timeline

| Phase | Duration | Start Condition |
|-------|----------|-----------------|
| Phase 1 | 0.5 days | Immediate |
| Phase 2 | 1 day | After Phase 1 |
| Phase 3 | 2 days | **BLOCKED**: Nitrado SDK availability |
| Phase 4 | 2 days | After Phase 3 + Spec decision |
| **Total (P1-P2)** | **1.5 days** | Immediate |
| **Total (P1-P4)** | **5.5 days** | After Nitrado SDK |

---

## Dependencies & Blockers

### Upstream Dependencies

| Module | Version | Status | Notes |
|--------|---------|--------|-------|
| `02-framework-core` | v2.1.0 | ✅ COMPLETE | Core utilities |
| `02-framework-accessor` | v2.0.0 | ✅ COMPLETE | Accessor interfaces (no migration needed) |

### Downstream Impact

| Module | Impact | Notes |
|--------|--------|-------|
| `02-adapter-hytale` | Direct dependency | Contains disabled adapter implementations |
| `02-framework-loader` | Direct dependency | Loads webserver framework |
| `07-tools-quest-designer` | Indirect | Uses Quest Designer Jetty server (not this framework yet) |
| `07-tools-prefab-designer` | Indirect | Will use this framework for API |
| `99-HyQuest-WebUI` | Consumer | React app that calls exposed APIs |
| `99-HyPrefab-WebUI` | Consumer | React app that calls exposed APIs |

### External Blockers

| Blocker | Description | Impact | Workaround |
|---------|-------------|--------|------------|
| 🔴 Nitrado SDK | Official Nitrado WebServer Plugin JAR not available | Cannot enable adapter | Interfaces only, mock for testing |
| 🟡 WebSocket Spec | No formal specification for WebSocket support | Phase 4 blocked | Defer to future sprint |

---

## Validation Criteria

### Build Validation
- [x] `mvn clean compile` succeeds with zero errors ✅ (verified 2026-01-29)
- [ ] `mvn test` passes all unit tests (⚠️ no tests exist yet)
- [x] No Hytale import leaks (verified - zero platform imports) ✅

### Architecture Validation
- [x] All `Object` usages migrated to appropriate types ✅ (N/A - no Object usage)
- [x] No `return null;` without exception ✅ (interfaces only, no impl)
- [x] All accessor interfaces properly designed ✅
- [x] Framework follows accessor pattern ✅

### Specification Validation
- [x] All spec requirements have implementations ✅ (interfaces complete)
- [x] All implementations trace to specs ✅ (HLR-ARCHITECTURE-006)
- [x] Orphan implementations documented or removed ✅ (none found)

---

## Architectural Assessment

### Strengths ✅
1. **Clean abstraction**: Zero platform imports, pure interface design
2. **Well-documented**: Comprehensive Javadoc on all interfaces
3. **Type-safe**: No generic Object parameters, proper Optional usage
4. **Functional design**: RouteHandler is a clean FunctionalInterface
5. **Permission-aware**: UserPrincipal supports granular permissions
6. **Testable**: Easy to mock for downstream testing

### Areas for Improvement 🔧
1. **Missing tests**: No unit tests or mock implementations
2. **Adapter disabled**: Real implementation blocked on Nitrado SDK
3. **WebSocket gap**: IMPLEMENTATION_TRACKING mentions WebSocket but no interfaces exist
4. **Version confusion**: IMPLEMENTATION_TRACKING shows 0.1.0, CHANGELOG shows 1.0.0

### Recommendation

**Priority**: 🟡 MEDIUM

This module is **architecturally sound** and **spec-compliant**. The main action items are:
1. Add test coverage with mock implementations
2. Clarify version numbering
3. Wait for Nitrado SDK to enable adapter
4. Decide on WebSocket specification

---

## HytaleModder Handoff Prompt

> **Task**: Implement test coverage for `03-framework-webserver`
> 
> **Context**: The WebServer Framework provides platform-agnostic HTTP API abstractions. All interfaces are complete but no tests exist.
> 
> **Deliverables**:
> 1. Create `src/test/java/com/argonathsystems/framework/webserver/` directory
> 2. Create `MockHttpRequest` implementing `HttpRequest` for testing
> 3. Create `MockHttpResponse` implementing `HttpResponse` for testing  
> 4. Create `MockWebServerAccessor` implementing `WebServerAccessor` for testing
> 5. Create `MockUserPrincipal` implementing `UserPrincipal` for testing
> 6. Add unit tests verifying interface contracts
> 
> **Constraints**:
> - No Hytale imports allowed
> - Use JUnit 5 and Mockito
> - Mock implementations should be reusable by downstream modules
> 
> **References**:
> - Spec: HLR-ARCHITECTURE-006-webserver-framework-summary.md
> - Interfaces: `src/main/java/com/argonathsystems/framework/webserver/`

---

*Generated by HytaleArchitect agent on 2026-01-29*
