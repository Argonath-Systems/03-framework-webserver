package com.argonathsystems.framework.webserver.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MockUserPrincipal}.
 */
@DisplayName("MockUserPrincipal")
class MockUserPrincipalTest {
    
    @Nested
    @DisplayName("Builder")
    class BuilderTests {
        
        @Test
        @DisplayName("should create user with defaults")
        void shouldCreateUserWithDefaults() {
            MockUserPrincipal user = MockUserPrincipal.builder().build();
            
            assertEquals("TestUser", user.getName());
            assertTrue(user.isPlayer());
            assertFalse(user.isServiceAccount());
            assertTrue(user.getPermissions().isEmpty());
        }
        
        @Test
        @DisplayName("should create user with all properties")
        void shouldCreateUserWithAllProperties() {
            MockUserPrincipal user = MockUserPrincipal.builder()
                .name("CustomUser")
                .isPlayer(false)
                .permission("read")
                .permission("write")
                .build();
            
            assertEquals("CustomUser", user.getName());
            assertFalse(user.isPlayer());
            assertTrue(user.isServiceAccount());
            assertEquals(Set.of("read", "write"), user.getPermissions());
        }
        
        @Test
        @DisplayName("should add multiple permissions at once")
        void shouldAddMultiplePermissionsAtOnce() {
            MockUserPrincipal user = MockUserPrincipal.builder()
                .permissions(Set.of("a", "b", "c"))
                .build();
            
            assertEquals(3, user.getPermissions().size());
        }
    }
    
    @Nested
    @DisplayName("Factory Methods")
    class FactoryMethodTests {
        
        @Test
        @DisplayName("player() should create player principal")
        void playerShouldCreatePlayerPrincipal() {
            MockUserPrincipal user = MockUserPrincipal.player("TestPlayer");
            
            assertEquals("TestPlayer", user.getName());
            assertTrue(user.isPlayer());
        }
        
        @Test
        @DisplayName("serviceAccount() should create service account")
        void serviceAccountShouldCreateServiceAccount() {
            MockUserPrincipal user = MockUserPrincipal.serviceAccount("svc.hyquest");
            
            assertEquals("svc.hyquest", user.getName());
            assertTrue(user.isServiceAccount());
            assertFalse(user.isPlayer());
        }
        
        @Test
        @DisplayName("admin() should create user with all permissions")
        void adminShouldCreateUserWithAllPermissions() {
            MockUserPrincipal user = MockUserPrincipal.admin("AdminPlayer");
            
            assertEquals("AdminPlayer", user.getName());
            assertTrue(user.hasPermission("anything"));
            assertTrue(user.hasPermission("quests.write"));
            assertTrue(user.hasPermission("admin.delete"));
        }
    }
    
    @Nested
    @DisplayName("Permission Checking")
    class PermissionCheckingTests {
        
        @Test
        @DisplayName("should have exact permission")
        void shouldHaveExactPermission() {
            MockUserPrincipal user = MockUserPrincipal.builder()
                .permission("quests.read")
                .build();
            
            assertTrue(user.hasPermission("quests.read"));
            assertFalse(user.hasPermission("quests.write"));
        }
        
        @Test
        @DisplayName("should support wildcard permissions")
        void shouldSupportWildcardPermissions() {
            MockUserPrincipal user = MockUserPrincipal.builder()
                .permission("quests.*")
                .build();
            
            assertTrue(user.hasPermission("quests.read"));
            assertTrue(user.hasPermission("quests.write"));
            assertTrue(user.hasPermission("quests.delete"));
            assertFalse(user.hasPermission("admin.read"));
        }
        
        @Test
        @DisplayName("should support global wildcard")
        void shouldSupportGlobalWildcard() {
            MockUserPrincipal user = MockUserPrincipal.builder()
                .permission("*")
                .build();
            
            assertTrue(user.hasPermission("anything"));
            assertTrue(user.hasPermission("deeply.nested.permission"));
        }
        
        @Test
        @DisplayName("hasAnyPermission should return true if any matches")
        void hasAnyPermissionShouldReturnTrueIfAnyMatches() {
            MockUserPrincipal user = MockUserPrincipal.builder()
                .permission("quests.read")
                .build();
            
            assertTrue(user.hasAnyPermission("quests.read", "quests.write"));
            assertFalse(user.hasAnyPermission("admin.read", "admin.write"));
        }
        
        @Test
        @DisplayName("hasAllPermissions should return true only if all match")
        void hasAllPermissionsShouldReturnTrueOnlyIfAllMatch() {
            MockUserPrincipal user = MockUserPrincipal.builder()
                .permission("quests.read")
                .permission("quests.write")
                .build();
            
            assertTrue(user.hasAllPermissions("quests.read", "quests.write"));
            assertFalse(user.hasAllPermissions("quests.read", "admin.write"));
        }
    }
    
    @Nested
    @DisplayName("Immutability")
    class ImmutabilityTests {
        
        @Test
        @DisplayName("permissions should be immutable")
        void permissionsShouldBeImmutable() {
            MockUserPrincipal user = MockUserPrincipal.builder()
                .permission("test")
                .build();
            
            assertThrows(UnsupportedOperationException.class,
                () -> user.getPermissions().add("new"));
        }
    }
}
