package com.argonathsystems.framework.webserver.mock;

import com.argonathsystems.framework.webserver.UserPrincipal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Mock implementation of {@link UserPrincipal} for testing.
 * 
 * <p>This class provides a configurable user principal that can be used
 * in unit tests without requiring actual authentication.
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * MockUserPrincipal user = MockUserPrincipal.builder()
 *     .name("TestPlayer")
 *     .isPlayer(true)
 *     .permission("quests.read")
 *     .permission("quests.write")
 *     .build();
 * 
 * assertTrue(user.hasPermission("quests.read"));
 * assertFalse(user.hasPermission("admin.delete"));
 * }</pre>
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public final class MockUserPrincipal implements UserPrincipal {
    
    private final String name;
    private final Set<String> permissions;
    private final boolean isPlayer;
    
    private MockUserPrincipal(Builder builder) {
        this.name = builder.name;
        this.permissions = Collections.unmodifiableSet(new HashSet<>(builder.permissions));
        this.isPlayer = builder.isPlayer;
    }
    
    /**
     * Creates a builder for MockUserPrincipal.
     * 
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Creates a simple player principal with the given name.
     * 
     * @param name the player name
     * @return a new MockUserPrincipal
     */
    public static MockUserPrincipal player(String name) {
        return builder().name(name).isPlayer(true).build();
    }
    
    /**
     * Creates a service account principal with the given name.
     * 
     * @param name the service account name
     * @return a new MockUserPrincipal
     */
    public static MockUserPrincipal serviceAccount(String name) {
        return builder().name(name).isPlayer(false).build();
    }
    
    /**
     * Creates an admin player with all common permissions.
     * 
     * @param name the admin name
     * @return a new MockUserPrincipal with admin permissions
     */
    public static MockUserPrincipal admin(String name) {
        return builder()
            .name(name)
            .isPlayer(true)
            .permission("*")
            .build();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public boolean hasPermission(String permission) {
        if (permissions.contains("*")) {
            return true;
        }
        
        // Check exact match
        if (permissions.contains(permission)) {
            return true;
        }
        
        // Check wildcard permissions (e.g., "quests.*" matches "quests.read")
        for (String perm : permissions) {
            if (perm.endsWith(".*")) {
                String prefix = perm.substring(0, perm.length() - 1);
                if (permission.startsWith(prefix)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public Set<String> getPermissions() {
        return permissions;
    }
    
    @Override
    public boolean isPlayer() {
        return isPlayer;
    }
    
    @Override
    public String toString() {
        return "MockUserPrincipal{" +
            "name='" + name + '\'' +
            ", isPlayer=" + isPlayer +
            ", permissions=" + permissions +
            '}';
    }
    
    /**
     * Builder for {@link MockUserPrincipal}.
     */
    public static final class Builder {
        private String name = "TestUser";
        private final Set<String> permissions = new HashSet<>();
        private boolean isPlayer = true;
        
        private Builder() {}
        
        /**
         * Sets the user name.
         * 
         * @param name the user name
         * @return this builder
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        /**
         * Adds a permission to this user.
         * 
         * @param permission the permission to add
         * @return this builder
         */
        public Builder permission(String permission) {
            this.permissions.add(permission);
            return this;
        }
        
        /**
         * Adds multiple permissions to this user.
         * 
         * @param permissions the permissions to add
         * @return this builder
         */
        public Builder permissions(Set<String> permissions) {
            this.permissions.addAll(permissions);
            return this;
        }
        
        /**
         * Sets whether this is a player account.
         * 
         * @param isPlayer true for player, false for service account
         * @return this builder
         */
        public Builder isPlayer(boolean isPlayer) {
            this.isPlayer = isPlayer;
            return this;
        }
        
        /**
         * Builds the MockUserPrincipal.
         * 
         * @return a new MockUserPrincipal instance
         */
        public MockUserPrincipal build() {
            return new MockUserPrincipal(this);
        }
    }
}
