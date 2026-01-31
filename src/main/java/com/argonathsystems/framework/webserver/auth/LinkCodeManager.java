package com.argonathsystems.framework.webserver.auth;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages temporary link codes for in-game account linking.
 * 
 * <p>The flow for account linking:
 * <ol>
 *   <li>User logs into Designer portal with Discord</li>
 *   <li>User requests a link code from the portal</li>
 *   <li>User enters the code in-game with {@code /designer link <code>}</li>
 *   <li>Server validates code and links accounts</li>
 * </ol>
 * 
 * <p>Link codes are:
 * <ul>
 *   <li>6 characters using unambiguous characters (no I, O, 0, 1)</li>
 *   <li>Valid for 5 minutes</li>
 *   <li>Single-use (consumed on successful link)</li>
 *   <li>One code per user at a time</li>
 * </ul>
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public class LinkCodeManager {

    /** Code length in characters */
    private static final int CODE_LENGTH = 6;
    
    /** Characters used in codes (no ambiguous chars: I, O, 0, 1) */
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    
    /** Default code validity: 5 minutes */
    private static final long DEFAULT_CODE_VALIDITY_MS = 5 * 60 * 1000L;

    private final SecureRandom random = new SecureRandom();
    private final Map<String, PendingLink> pendingLinks = new ConcurrentHashMap<>();
    private final Map<String, String> userToCode = new ConcurrentHashMap<>();
    private final long codeValidityMs;

    /**
     * Represents a pending account link.
     * 
     * @param userId    The Designer user ID
     * @param discordId The Discord user ID
     * @param expiresAt When the code expires
     */
    public record PendingLink(String userId, String discordId, Instant expiresAt) {
        /**
         * Check if this link code has expired.
         * @return true if expired
         */
        public boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    /**
     * Create a link code manager with default validity period (5 minutes).
     */
    public LinkCodeManager() {
        this(DEFAULT_CODE_VALIDITY_MS);
    }

    /**
     * Create a link code manager with custom validity period.
     * 
     * @param codeValidityMs Code validity in milliseconds
     */
    public LinkCodeManager(long codeValidityMs) {
        this.codeValidityMs = codeValidityMs;
    }

    /**
     * Generate a new link code for a user.
     * 
     * <p>If the user already has a pending code, it is invalidated
     * and replaced with a new one.
     * 
     * @param userId    The Designer user ID
     * @param discordId The user's Discord ID
     * @return The generated 6-character code
     */
    public String generateCode(String userId, String discordId) {
        // Invalidate any existing code for this user
        String existingCode = userToCode.remove(userId);
        if (existingCode != null) {
            pendingLinks.remove(existingCode);
        }

        // Generate new unique code
        String code;
        do {
            code = generateRandomCode();
        } while (pendingLinks.containsKey(code));

        Instant expiresAt = Instant.now().plusMillis(codeValidityMs);
        pendingLinks.put(code, new PendingLink(userId, discordId, expiresAt));
        userToCode.put(userId, code);

        return code;
    }

    /**
     * Validate and consume a link code.
     * 
     * <p>If the code is valid and not expired, it is consumed (removed)
     * and the pending link info is returned. Codes are case-insensitive.
     * 
     * @param code The code to validate
     * @return The pending link info if valid, empty if invalid or expired
     */
    public Optional<PendingLink> consumeCode(String code) {
        if (code == null || code.length() != CODE_LENGTH) {
            return Optional.empty();
        }
        
        PendingLink link = pendingLinks.remove(code.toUpperCase());
        if (link == null || link.isExpired()) {
            return Optional.empty();
        }
        userToCode.remove(link.userId());
        return Optional.of(link);
    }

    /**
     * Check if a code is valid without consuming it.
     * 
     * @param code The code to check
     * @return true if valid and not expired
     */
    public boolean isCodeValid(String code) {
        if (code == null || code.length() != CODE_LENGTH) {
            return false;
        }
        PendingLink link = pendingLinks.get(code.toUpperCase());
        return link != null && !link.isExpired();
    }

    /**
     * Get the expiry time for a code.
     * 
     * @param code The code to check
     * @return Expiry timestamp in milliseconds, or 0 if code not found
     */
    public long getCodeExpiryTime(String code) {
        if (code == null) {
            return 0;
        }
        PendingLink link = pendingLinks.get(code.toUpperCase());
        return link != null ? link.expiresAt().toEpochMilli() : 0;
    }

    /**
     * Get the pending link for a user.
     * 
     * @param userId The user ID to check
     * @return The pending link if exists and not expired, empty otherwise
     */
    public Optional<PendingLink> getPendingLink(String userId) {
        String code = userToCode.get(userId);
        if (code == null) {
            return Optional.empty();
        }
        PendingLink link = pendingLinks.get(code);
        if (link == null || link.isExpired()) {
            // Cleanup expired
            userToCode.remove(userId);
            pendingLinks.remove(code);
            return Optional.empty();
        }
        return Optional.of(link);
    }

    /**
     * Clean up all expired codes.
     * 
     * <p>This should be called periodically (e.g., every minute)
     * to prevent memory buildup from abandoned codes.
     * 
     * @return Number of expired codes removed
     */
    public int cleanupExpired() {
        int removed = 0;
        var iterator = pendingLinks.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
                userToCode.remove(entry.getValue().userId());
                removed++;
            }
        }
        return removed;
    }

    /**
     * Get the number of pending (non-expired) codes.
     * 
     * @return Count of pending codes
     */
    public int getPendingCount() {
        return (int) pendingLinks.values().stream()
            .filter(link -> !link.isExpired())
            .count();
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }
}
