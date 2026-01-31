package com.argonathsystems.framework.webserver.dto.auth;

import java.util.List;
import java.util.UUID;

/**
 * Authenticated user details for the Designer portal.
 *
 * @param id              Internal user ID
 * @param discordId       Discord user ID
 * @param discordUsername Discord username
 * @param discordAvatar   Discord avatar hash (null if no custom avatar)
 * @param playerUuid      Linked in-game player UUID (null if not linked)
 * @param playerName      Linked in-game player name (null if not linked)
 * @param permissions     Bitfield permissions as string (BigInt serialization)
 * @param roles           List of role names assigned to the user
 * @param createdAt       ISO-8601 timestamp of account creation
 * @param lastLogin       ISO-8601 timestamp of last login
 */
public record AuthUser(
    String id,
    String discordId,
    String discordUsername,
    String discordAvatar,
    UUID playerUuid,
    String playerName,
    String permissions,  // BigInt as string for JSON serialization
    List<String> roles,
    String createdAt,
    String lastLogin
) {}
