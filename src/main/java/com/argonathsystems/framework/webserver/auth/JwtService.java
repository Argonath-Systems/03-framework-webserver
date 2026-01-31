package com.argonathsystems.framework.webserver.auth;

import com.argonathsystems.framework.webserver.dto.auth.AuthTokens;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * JWT token generation and validation service.
 * 
 * <p>Handles creation and validation of access and refresh tokens
 * for the Designer portal authentication system.
 * 
 * <p>Access tokens are short-lived (default 15 minutes) and contain
 * user permissions. Refresh tokens are long-lived (default 7 days)
 * and can be used to obtain new access tokens.
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public class JwtService {
    
    /** Default access token validity: 15 minutes */
    public static final long DEFAULT_ACCESS_TOKEN_VALIDITY_MS = 15 * 60 * 1000L;
    
    /** Default refresh token validity: 7 days */
    public static final long DEFAULT_REFRESH_TOKEN_VALIDITY_MS = 7 * 24 * 60 * 60 * 1000L;
    
    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final long accessTokenValidityMs;
    private final long refreshTokenValidityMs;

    /**
     * Create a JWT service with custom configuration.
     * 
     * @param accessSecret          Secret key for signing access tokens (min 32 chars)
     * @param refreshSecret         Secret key for signing refresh tokens (min 32 chars)
     * @param accessTokenValidityMs Access token validity in milliseconds
     * @param refreshTokenValidityMs Refresh token validity in milliseconds
     * @throws IllegalArgumentException if secrets are too short
     */
    public JwtService(String accessSecret, String refreshSecret, 
                      long accessTokenValidityMs, long refreshTokenValidityMs) {
        if (accessSecret.length() < 32) {
            throw new IllegalArgumentException("Access secret must be at least 32 characters");
        }
        if (refreshSecret.length() < 32) {
            throw new IllegalArgumentException("Refresh secret must be at least 32 characters");
        }
        
        this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityMs = accessTokenValidityMs;
        this.refreshTokenValidityMs = refreshTokenValidityMs;
    }

    /**
     * Create a JWT service with default token validity periods.
     * 
     * @param accessSecret  Secret key for signing access tokens
     * @param refreshSecret Secret key for signing refresh tokens
     */
    public JwtService(String accessSecret, String refreshSecret) {
        this(accessSecret, refreshSecret, 
             DEFAULT_ACCESS_TOKEN_VALIDITY_MS, DEFAULT_REFRESH_TOKEN_VALIDITY_MS);
    }

    /**
     * Generate a new token pair for a user.
     * 
     * @param userId      The user's unique ID
     * @param discordId   The user's Discord ID
     * @param permissions The user's permission bitfield as string
     * @return Token pair containing access and refresh tokens
     */
    public AuthTokens generateTokens(String userId, String discordId, String permissions) {
        Instant now = Instant.now();
        long expiresAt = now.toEpochMilli() + accessTokenValidityMs;

        String accessToken = Jwts.builder()
            .subject(userId)
            .claims(Map.of(
                "discordId", discordId,
                "permissions", permissions,
                "type", "access"
            ))
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(accessTokenValidityMs)))
            .signWith(accessKey)
            .compact();

        String refreshToken = Jwts.builder()
            .subject(userId)
            .claims(Map.of(
                "type", "refresh",
                "jti", UUID.randomUUID().toString()
            ))
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(refreshTokenValidityMs)))
            .signWith(refreshKey)
            .compact();

        return new AuthTokens(accessToken, refreshToken, expiresAt);
    }

    /**
     * Validate an access token and extract claims.
     * 
     * @param token The access token to validate
     * @return The token claims
     * @throws io.jsonwebtoken.JwtException if token is invalid or expired
     */
    public Claims validateAccessToken(String token) {
        return Jwts.parser()
            .verifyWith(accessKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Validate a refresh token and extract claims.
     * 
     * @param token The refresh token to validate
     * @return The token claims
     * @throws io.jsonwebtoken.JwtException if token is invalid or expired
     */
    public Claims validateRefreshToken(String token) {
        return Jwts.parser()
            .verifyWith(refreshKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Extract the user ID from an access token.
     * 
     * @param token The access token
     * @return The user ID (subject claim)
     * @throws io.jsonwebtoken.JwtException if token is invalid or expired
     */
    public String getUserIdFromToken(String token) {
        return validateAccessToken(token).getSubject();
    }

    /**
     * Extract the Discord ID from an access token.
     * 
     * @param token The access token
     * @return The Discord ID
     * @throws io.jsonwebtoken.JwtException if token is invalid or expired
     */
    public String getDiscordIdFromToken(String token) {
        return validateAccessToken(token).get("discordId", String.class);
    }

    /**
     * Extract permissions from an access token.
     * 
     * @param token The access token
     * @return The permissions as a BigInteger string
     * @throws io.jsonwebtoken.JwtException if token is invalid or expired
     */
    public String getPermissionsFromToken(String token) {
        return validateAccessToken(token).get("permissions", String.class);
    }
}
