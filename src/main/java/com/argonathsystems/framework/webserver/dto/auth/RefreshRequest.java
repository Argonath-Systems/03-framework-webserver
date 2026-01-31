package com.argonathsystems.framework.webserver.dto.auth;

/**
 * Request DTO for token refresh.
 *
 * @param refreshToken The refresh token to exchange for new tokens
 */
public record RefreshRequest(String refreshToken) {}
