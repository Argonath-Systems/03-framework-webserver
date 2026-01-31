package com.argonathsystems.framework.webserver.auth;

import java.math.BigInteger;

import static com.argonathsystems.framework.webserver.auth.DesignerPermission.*;

/**
 * Role presets for the Argonath Designer portal.
 * 
 * <p>Roles are predefined combinations of permissions for common use cases.
 * Key design decisions:
 * <ul>
 *   <li>Quest designers can edit NPCs (as specified in requirements)</li>
 *   <li>Prefab and Worldgen are SEPARATE permissions</li>
 *   <li>Senior Builder has all edit permissions but not admin</li>
 * </ul>
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public enum DesignerRole {
    
    /**
     * Quest Designer: Can edit quests + NPCs + dialogues.
     * Quest designers can edit NPCs as specified in requirements.
     */
    QUEST_DESIGNER(combine(
        VIEW_DASHBOARD,
        VIEW_QUESTS, EDIT_QUESTS, DELETE_QUESTS,
        VIEW_NPCS, EDIT_NPCS,  // Quest designers can edit NPCs
        VIEW_DIALOGUES, EDIT_DIALOGUES
    )),

    /**
     * Prefab Builder: Prefabs only (separate from worldgen).
     */
    PREFAB_BUILDER(combine(
        VIEW_DASHBOARD,
        VIEW_PREFABS, EDIT_PREFABS, DELETE_PREFABS
    )),

    /**
     * Worldgen Builder: Worldgen only (separate from prefabs).
     */
    WORLDGEN_BUILDER(combine(
        VIEW_DASHBOARD,
        VIEW_WORLDGEN, EDIT_WORLDGEN, DELETE_WORLDGEN
    )),

    /**
     * NPC Designer: NPCs + Dialogues.
     */
    NPC_DESIGNER(combine(
        VIEW_DASHBOARD,
        VIEW_NPCS, EDIT_NPCS, DELETE_NPCS,
        VIEW_DIALOGUES, EDIT_DIALOGUES, DELETE_DIALOGUES
    )),

    /**
     * UI Designer: UI components only.
     */
    UI_DESIGNER(combine(
        VIEW_DASHBOARD,
        VIEW_UI, EDIT_UI, DELETE_UI
    )),

    /**
     * Senior Builder: All edit permissions including publish.
     */
    SENIOR_BUILDER(combine(
        VIEW_DASHBOARD,
        VIEW_QUESTS, EDIT_QUESTS, DELETE_QUESTS, PUBLISH_QUESTS,
        VIEW_NPCS, EDIT_NPCS, DELETE_NPCS, PUBLISH_NPCS,
        VIEW_DIALOGUES, EDIT_DIALOGUES, DELETE_DIALOGUES, PUBLISH_DIALOGUES,
        VIEW_PREFABS, EDIT_PREFABS, DELETE_PREFABS, PUBLISH_PREFABS,
        VIEW_WORLDGEN, EDIT_WORLDGEN, DELETE_WORLDGEN, PUBLISH_WORLDGEN,
        VIEW_UI, EDIT_UI, DELETE_UI, PUBLISH_UI
    )),

    /**
     * Admin: Full access including user/role management.
     */
    ADMIN(combine(
        VIEW_DASHBOARD,
        VIEW_QUESTS, EDIT_QUESTS, DELETE_QUESTS, PUBLISH_QUESTS,
        VIEW_NPCS, EDIT_NPCS, DELETE_NPCS, PUBLISH_NPCS,
        VIEW_DIALOGUES, EDIT_DIALOGUES, DELETE_DIALOGUES, PUBLISH_DIALOGUES,
        VIEW_PREFABS, EDIT_PREFABS, DELETE_PREFABS, PUBLISH_PREFABS,
        VIEW_WORLDGEN, EDIT_WORLDGEN, DELETE_WORLDGEN, PUBLISH_WORLDGEN,
        VIEW_UI, EDIT_UI, DELETE_UI, PUBLISH_UI,
        MANAGE_USERS, MANAGE_ROLES, DesignerPermission.ADMIN
    ));

    private final BigInteger permissions;

    /**
     * Create a role with predefined permissions.
     * 
     * @param permissions The combined permission bitfield for this role
     */
    DesignerRole(BigInteger permissions) {
        this.permissions = permissions;
    }

    /**
     * Get the combined permissions for this role.
     * 
     * @return The permission bitfield
     */
    public BigInteger getPermissions() {
        return permissions;
    }

    /**
     * Check if this role has a specific permission.
     * 
     * @param permission The permission to check
     * @return true if the role includes this permission
     */
    public boolean hasPermission(DesignerPermission permission) {
        return DesignerPermission.hasPermission(this.permissions, permission);
    }

    /**
     * Combine multiple roles into a single permission bitfield.
     * Used when a user has multiple roles.
     * 
     * @param roles The roles to combine
     * @return Combined permissions from all roles
     */
    public static BigInteger combineRoles(DesignerRole... roles) {
        BigInteger result = BigInteger.ZERO;
        for (DesignerRole role : roles) {
            result = result.or(role.getPermissions());
        }
        return result;
    }
}
