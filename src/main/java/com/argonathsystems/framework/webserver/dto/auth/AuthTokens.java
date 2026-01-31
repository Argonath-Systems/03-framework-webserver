package com.argonathsystems.framework.webserver.dto.auth;

/**
 * JWT token pair for authentication.
 *
 * @param accessToken  Short-lived access token for API requests
 * @param refreshToken Long-lived refresh token for obtaining new access tokens
 * @param expiresAt    Unix timestamp (ms) when the access token expires
 */
public record AuthTokens(
    String accessToken,
    String refreshToken,
    long expiresAt
) {}
