package com.argonathsystems.framework.webserver.auth;

import java.math.BigInteger;
import java.util.EnumSet;
import java.util.Set;

/**
 * Permission flags for the Argonath Designer portal.
 * Uses bitfield permissions for efficient storage and checking.
 * 
 * <p>MUST stay in sync with packages/api-client/src/auth/permissions.ts
 * 
 * <p>Each permission is a unique power of 2 for bitwise operations,
 * allowing multiple permissions to be stored in a single BigInteger.
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public enum DesignerPermission {
    
    // Base permissions
    VIEW_DASHBOARD(0),
    
    // Quest permissions
    VIEW_QUESTS(1),
    EDIT_QUESTS(2),
    DELETE_QUESTS(3),
    PUBLISH_QUESTS(4),
    
    // NPC permissions
    VIEW_NPCS(5),
    EDIT_NPCS(6),
    DELETE_NPCS(7),
    PUBLISH_NPCS(8),
    
    // Dialogue permissions
    VIEW_DIALOGUES(9),
    EDIT_DIALOGUES(10),
    DELETE_DIALOGUES(11),
    PUBLISH_DIALOGUES(12),
    
    // Prefab permissions (separate from worldgen)
    VIEW_PREFABS(13),
    EDIT_PREFABS(14),
    DELETE_PREFABS(15),
    PUBLISH_PREFABS(16),
    
    // Worldgen permissions (separate from prefab)
    VIEW_WORLDGEN(17),
    EDIT_WORLDGEN(18),
    DELETE_WORLDGEN(19),
    PUBLISH_WORLDGEN(20),
    
    // UI permissions
    VIEW_UI(21),
    EDIT_UI(22),
    DELETE_UI(23),
    PUBLISH_UI(24),
    
    // Admin permissions
    MANAGE_USERS(30),
    MANAGE_ROLES(31),
    ADMIN(32);

    private final int bitPosition;
    private final BigInteger flag;

    /**
     * Create a permission with a specific bit position.
     * 
     * @param bitPosition The position of this permission's bit (0-indexed)
     */
    DesignerPermission(int bitPosition) {
        this.bitPosition = bitPosition;
        this.flag = BigInteger.ONE.shiftLeft(bitPosition);
    }

    /**
     * Get the bit position of this permission.
     * 
     * @return The bit position (0-indexed)
     */
    public int getBitPosition() {
        return bitPosition;
    }

    /**
     * Get the flag value for this permission.
     * 
     * @return The BigInteger flag (2^bitPosition)
     */
    public BigInteger getFlag() {
        return flag;
    }

    /**
     * Combine multiple permissions into a single bitfield.
     * 
     * @param permissions The permissions to combine
     * @return Combined permissions as BigInteger
     */
    public static BigInteger combine(DesignerPermission... permissions) {
        BigInteger result = BigInteger.ZERO;
        for (DesignerPermission perm : permissions) {
            result = result.or(perm.getFlag());
        }
        return result;
    }

    /**
     * Combine a set of permissions into a single bitfield.
     * 
     * @param permissions The set of permissions to combine
     * @return Combined permissions as BigInteger
     */
    public static BigInteger combine(Set<DesignerPermission> permissions) {
        BigInteger result = BigInteger.ZERO;
        for (DesignerPermission perm : permissions) {
            result = result.or(perm.getFlag());
        }
        return result;
    }

    /**
     * Check if a permission bitfield contains a specific permission.
     * 
     * @param userPermissions The user's permission bitfield
     * @param permission      The permission to check for
     * @return true if the permission is present
     */
    public static boolean hasPermission(BigInteger userPermissions, DesignerPermission permission) {
        return userPermissions.and(permission.getFlag()).equals(permission.getFlag());
    }

    /**
     * Convert a permission bitfield to an EnumSet of permissions.
     * 
     * @param permissions The permission bitfield
     * @return EnumSet containing all permissions in the bitfield
     */
    public static EnumSet<DesignerPermission> fromBigInteger(BigInteger permissions) {
        EnumSet<DesignerPermission> result = EnumSet.noneOf(DesignerPermission.class);
        for (DesignerPermission perm : values()) {
            if (hasPermission(permissions, perm)) {
                result.add(perm);
            }
        }
        return result;
    }
}
