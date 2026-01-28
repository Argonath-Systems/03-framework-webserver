package com.argonathsystems.framework.webserver;

import java.util.Set;

/**
 * Represents an authenticated user in an HTTP request.
 * 
 * <p>This interface abstracts user authentication, providing access to:
 * <ul>
 *   <li>User identification (name, UUID)</li>
 *   <li>Permission checking</li>
 *   <li>User type (player vs service account)</li>
 * </ul>
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserPrincipal {
    
    /**
     * Gets the user's name.
     * 
     * <p>For players, this is the Hytale username.
     * For service accounts, this is the account name (e.g., "serviceaccount.hyquest").
     * 
     * @return the user name
     */
    String getName();
    
    /**
     * Checks if the user has a specific permission.
     * 
     * <p>Example: {@code user.hasPermission("argonath.quests.web.read")}
     * 
     * @param permission the permission to check
     * @return {@code true} if user has the permission, {@code false} otherwise
     */
    boolean hasPermission(String permission);
    
    /**
     * Checks if the user has ANY of the specified permissions.
     * 
     * @param permissions the permissions to check
     * @return {@code true} if user has at least one permission, {@code false} otherwise
     */
    default boolean hasAnyPermission(String... permissions) {
        for (String permission : permissions) {
            if (hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the user has ALL of the specified permissions.
     * 
     * @param permissions the permissions to check
     * @return {@code true} if user has all permissions, {@code false} otherwise
     */
    default boolean hasAllPermissions(String... permissions) {
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Gets all permissions assigned to this user.
     * 
     * @return set of permission strings
     */
    Set<String> getPermissions();
    
    /**
     * Checks if this is a player account.
     * 
     * @return {@code true} if this is a player, {@code false} if service account
     */
    boolean isPlayer();
    
    /**
     * Checks if this is a service account.
     * 
     * @return {@code true} if this is a service account, {@code false} if player
     */
    default boolean isServiceAccount() {
        return !isPlayer();
    }
}
