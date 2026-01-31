package com.argonathsystems.framework.webserver.dto.auth;

import java.util.UUID;

/**
 * Response containing account link status.
 *
 * @param linked     Whether the account is linked to an in-game player
 * @param playerUuid The linked player's UUID (null if not linked)
 * @param playerName The linked player's name (null if not linked)
 */
public record LinkStatusResponse(
    boolean linked,
    UUID playerUuid,
    String playerName
) {}
