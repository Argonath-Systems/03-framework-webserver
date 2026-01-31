package com.argonathsystems.framework.webserver.dto.auth;

/**
 * Response containing a link code for in-game account linking.
 *
 * @param code      The 6-character link code to enter in-game
 * @param expiresAt Unix timestamp (ms) when the code expires
 */
public record LinkCodeResponse(
    String code,
    long expiresAt
) {}
