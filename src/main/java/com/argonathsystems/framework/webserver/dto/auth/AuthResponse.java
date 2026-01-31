package com.argonathsystems.framework.webserver.dto.auth;

/**
 * Complete authentication response containing user info and tokens.
 *
 * @param user   The authenticated user details
 * @param tokens The JWT token pair (access and refresh)
 */
public record AuthResponse(
    AuthUser user,
    AuthTokens tokens
) {}
