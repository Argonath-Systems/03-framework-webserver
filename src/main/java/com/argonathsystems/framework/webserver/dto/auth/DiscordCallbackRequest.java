package com.argonathsystems.framework.webserver.dto.auth;

/**
 * Request DTO for Discord OAuth2 callback.
 * Contains the authorization code from Discord OAuth flow.
 *
 * @param code        The authorization code from Discord
 * @param redirectUri The redirect URI used in the OAuth flow
 */
public record DiscordCallbackRequest(
    String code,
    String redirectUri
) {}
