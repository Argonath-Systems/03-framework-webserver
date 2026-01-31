package com.argonathsystems.framework.webserver.auth;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a Designer portal user.
 * 
 * <p>A Designer user is associated with a Discord account and may optionally
 * be linked to an in-game player account. Permissions are derived from
 * the user's assigned roles.
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public class DesignerUser {
    
    private final String id;
    private final String discordId;
    private String discordUsername;
    private String discordAvatar;
    private UUID playerUuid;
    private String playerName;
    private BigInteger permissions;
    private Set<DesignerRole> roles;
    private final Instant createdAt;
    private Instant lastLogin;

    /**
     * Create a new Designer user.
     * 
     * @param id              Unique user ID
     * @param discordId       Discord user ID
     * @param discordUsername Discord username
     * @param discordAvatar   Discord avatar hash (may be null)
     * @param roles           Set of assigned roles
     * @param createdAt       Account creation timestamp
     */
    public DesignerUser(String id, String discordId, String discordUsername, String discordAvatar,
                        Set<DesignerRole> roles, Instant createdAt) {
        this.id = id;
        this.discordId = discordId;
        this.discordUsername = discordUsername;
        this.discordAvatar = discordAvatar;
        this.roles = roles;
        this.permissions = DesignerRole.combineRoles(roles.toArray(new DesignerRole[0]));
        this.createdAt = createdAt;
        this.lastLogin = createdAt;
    }

    // Getters
    
    /**
     * Get the unique user ID.
     * @return The user ID
     */
    public String getId() { 
        return id; 
    }
    
    /**
     * Get the Discord user ID.
     * @return The Discord ID (snowflake)
     */
    public String getDiscordId() { 
        return discordId; 
    }
    
    /**
     * Get the Discord username.
     * @return The username
     */
    public String getDiscordUsername() { 
        return discordUsername; 
    }
    
    /**
     * Get the Discord avatar hash.
     * @return The avatar hash, or null if using default avatar
     */
    public String getDiscordAvatar() { 
        return discordAvatar; 
    }
    
    /**
     * Get the linked player UUID.
     * @return The player UUID, or null if not linked
     */
    public UUID getPlayerUuid() { 
        return playerUuid; 
    }
    
    /**
     * Get the linked player name.
     * @return The player name, or null if not linked
     */
    public String getPlayerName() { 
        return playerName; 
    }
    
    /**
     * Get the combined permission bitfield.
     * @return The permissions as BigInteger
     */
    public BigInteger getPermissions() { 
        return permissions; 
    }
    
    /**
     * Get the assigned roles.
     * @return Set of roles
     */
    public Set<DesignerRole> getRoles() { 
        return roles; 
    }
    
    /**
     * Get the account creation timestamp.
     * @return Creation timestamp
     */
    public Instant getCreatedAt() { 
        return createdAt; 
    }
    
    /**
     * Get the last login timestamp.
     * @return Last login timestamp
     */
    public Instant getLastLogin() { 
        return lastLogin; 
    }

    // Setters
    
    /**
     * Update the Discord username.
     * @param discordUsername The new username
     */
    public void setDiscordUsername(String discordUsername) { 
        this.discordUsername = discordUsername; 
    }
    
    /**
     * Update the Discord avatar hash.
     * @param discordAvatar The new avatar hash
     */
    public void setDiscordAvatar(String discordAvatar) { 
        this.discordAvatar = discordAvatar; 
    }
    
    /**
     * Link a player account.
     * @param playerUuid The player UUID to link
     */
    public void setPlayerUuid(UUID playerUuid) { 
        this.playerUuid = playerUuid; 
    }
    
    /**
     * Set the linked player name.
     * @param playerName The player name
     */
    public void setPlayerName(String playerName) { 
        this.playerName = playerName; 
    }
    
    /**
     * Update the user's roles and recalculate permissions.
     * @param roles The new set of roles
     */
    public void setRoles(Set<DesignerRole> roles) {
        this.roles = roles;
        this.permissions = DesignerRole.combineRoles(roles.toArray(new DesignerRole[0]));
    }
    
    /**
     * Update the last login timestamp.
     * @param lastLogin The login timestamp
     */
    public void setLastLogin(Instant lastLogin) { 
        this.lastLogin = lastLogin; 
    }

    // Utility methods
    
    /**
     * Check if this user has a specific permission.
     * @param permission The permission to check
     * @return true if the user has the permission
     */
    public boolean hasPermission(DesignerPermission permission) {
        return DesignerPermission.hasPermission(this.permissions, permission);
    }

    /**
     * Check if this user's account is linked to an in-game player.
     * @return true if linked
     */
    public boolean isLinked() {
        return playerUuid != null;
    }

    @Override
    public String toString() {
        return String.format("DesignerUser[id=%s, discord=%s, player=%s, linked=%b]",
            id, discordUsername, playerName, isLinked());
    }
}
