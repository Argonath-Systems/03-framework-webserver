package com.argonathsystems.framework.webserver.controller;

import com.argonathsystems.framework.webserver.HttpMethod;
import com.argonathsystems.framework.webserver.HttpRequest;
import com.argonathsystems.framework.webserver.HttpResponse;
import com.argonathsystems.framework.webserver.RouteHandler;
import com.argonathsystems.framework.webserver.WebServerAccessor;
import com.argonathsystems.framework.webserver.auth.*;
import com.argonathsystems.framework.webserver.dto.auth.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * REST controller for authentication endpoints.
 * 
 * <p>Provides endpoints for:
 * <ul>
 *   <li>Discord OAuth2 callback - Exchange code for tokens</li>
 *   <li>Token refresh - Get new access token</li>
 *   <li>Current user - Get authenticated user info</li>
 *   <li>Logout - Invalidate session (client clears tokens)</li>
 *   <li>Link code generation - Create code for in-game linking</li>
 *   <li>Link status - Check if account is linked</li>
 *   <li>Unlink - Remove in-game account link</li>
 * </ul>
 * 
 * <p><strong>Note:</strong> This implementation uses in-memory user storage.
 * Replace with database integration for production use.
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public class AuthController {

    private final DiscordOAuthService discordService;
    private final JwtService jwtService;
    private final LinkCodeManager linkCodeManager;
    private final Gson gson;
    
    // In-memory user store (replace with database in production)
    private final Map<String, DesignerUser> usersByDiscordId = new ConcurrentHashMap<>();
    private final Map<String, DesignerUser> usersById = new ConcurrentHashMap<>();
    
    // Bot token for guild member lookup
    private final String botToken;

    /**
     * Create an auth controller.
     * 
     * @param discordService  Discord OAuth service
     * @param jwtService      JWT token service
     * @param linkCodeManager Link code manager
     * @param botToken        Discord bot token for guild lookups
     */
    public AuthController(DiscordOAuthService discordService, JwtService jwtService,
                          LinkCodeManager linkCodeManager, String botToken) {
        this.discordService = discordService;
        this.jwtService = jwtService;
        this.linkCodeManager = linkCodeManager;
        this.botToken = botToken;
        this.gson = new GsonBuilder()
            .serializeNulls()
            .create();
    }

    /**
     * Register all auth routes with the web server.
     * 
     * @param webServer The web server accessor to register routes with
     */
    public void registerRoutes(WebServerAccessor webServer) {
        webServer.registerRoute("/api/v1/auth/discord/callback", HttpMethod.POST, 
            this::handleDiscordCallback);
        webServer.registerRoute("/api/v1/auth/refresh", HttpMethod.POST, 
            this::handleRefresh);
        webServer.registerRoute("/api/v1/auth/me", HttpMethod.GET, 
            this::handleGetMe);
        webServer.registerRoute("/api/v1/auth/logout", HttpMethod.POST, 
            this::handleLogout);
        webServer.registerRoute("/api/v1/auth/link/generate", HttpMethod.POST, 
            this::handleGenerateLinkCode);
        webServer.registerRoute("/api/v1/auth/link/status", HttpMethod.GET, 
            this::handleLinkStatus);
        webServer.registerRoute("/api/v1/auth/link/unlink", HttpMethod.POST, 
            this::handleUnlink);
    }

    /**
     * POST /api/v1/auth/discord/callback
     * Exchange Discord OAuth code for tokens.
     */
    private void handleDiscordCallback(HttpRequest req, HttpResponse res) {
        try {
            DiscordCallbackRequest request = gson.fromJson(req.getBody(), DiscordCallbackRequest.class);
            
            if (request == null || request.code() == null || request.redirectUri() == null) {
                writeError(res, 400, "Missing required fields: code, redirectUri");
                return;
            }
            
            // Exchange code for Discord tokens
            var discordTokens = discordService.exchangeCode(request.code(), request.redirectUri());
            
            // Get Discord user info
            var discordUser = discordService.getUser(discordTokens.accessToken());
            
            // Check guild membership and roles
            Set<DesignerRole> roles = discordService.getDesignerRoles(discordUser.id(), botToken);
            
            if (roles.isEmpty()) {
                writeErrorResponse(res, 403, 
                    "You are not authorized to access the Designer portal. " +
                    "Please contact an administrator if you believe this is an error.");
                return;
            }

            // Find or create user
            DesignerUser user = usersByDiscordId.computeIfAbsent(discordUser.id(), id -> {
                String newId = UUID.randomUUID().toString();
                DesignerUser newUser = new DesignerUser(
                    newId, discordUser.id(), discordUser.username(),
                    discordUser.avatar(), roles, Instant.now()
                );
                usersById.put(newId, newUser);
                return newUser;
            });

            // Update user info
            user.setDiscordUsername(discordUser.username());
            user.setDiscordAvatar(discordUser.avatar());
            user.setRoles(roles);
            user.setLastLogin(Instant.now());

            // Generate JWT tokens
            AuthTokens tokens = jwtService.generateTokens(
                user.getId(), 
                user.getDiscordId(),
                user.getPermissions().toString()
            );

            AuthUser authUser = toAuthUser(user);
            writeSuccess(res, new AuthResponse(authUser, tokens));

        } catch (Exception e) {
            writeError(res, 400, "Authentication failed: " + e.getMessage());
        }
    }

    /**
     * POST /api/v1/auth/refresh
     * Refresh access token.
     */
    private void handleRefresh(HttpRequest req, HttpResponse res) {
        try {
            RefreshRequest request = gson.fromJson(req.getBody(), RefreshRequest.class);
            
            if (request == null || request.refreshToken() == null) {
                writeError(res, 400, "Missing refreshToken");
                return;
            }
            
            var claims = jwtService.validateRefreshToken(request.refreshToken());
            String userId = claims.getSubject();

            DesignerUser user = usersById.get(userId);
            if (user == null) {
                writeError(res, 401, "User not found");
                return;
            }

            AuthTokens tokens = jwtService.generateTokens(
                user.getId(),
                user.getDiscordId(),
                user.getPermissions().toString()
            );

            writeSuccess(res, tokens);

        } catch (Exception e) {
            writeError(res, 401, "Invalid refresh token");
        }
    }

    /**
     * GET /api/v1/auth/me
     * Get current user info.
     */
    private void handleGetMe(HttpRequest req, HttpResponse res) {
        getAuthenticatedUser(req, res, user -> {
            writeSuccess(res, toAuthUser(user));
        });
    }

    /**
     * POST /api/v1/auth/logout
     * Logout (client should clear tokens).
     */
    private void handleLogout(HttpRequest req, HttpResponse res) {
        // In a production system, you might want to blacklist the token
        writeSuccess(res, Map.of("message", "Logged out successfully"));
    }

    /**
     * POST /api/v1/auth/link/generate
     * Generate a code for in-game linking.
     */
    private void handleGenerateLinkCode(HttpRequest req, HttpResponse res) {
        getAuthenticatedUser(req, res, user -> {
            String code = linkCodeManager.generateCode(user.getId(), user.getDiscordId());
            long expiresAt = linkCodeManager.getCodeExpiryTime(code);

            writeSuccess(res, new LinkCodeResponse(code, expiresAt));
        });
    }

    /**
     * GET /api/v1/auth/link/status
     * Get current link status.
     */
    private void handleLinkStatus(HttpRequest req, HttpResponse res) {
        getAuthenticatedUser(req, res, user -> {
            writeSuccess(res, new LinkStatusResponse(
                user.isLinked(),
                user.getPlayerUuid(),
                user.getPlayerName()
            ));
        });
    }

    /**
     * POST /api/v1/auth/link/unlink
     * Unlink in-game account.
     */
    private void handleUnlink(HttpRequest req, HttpResponse res) {
        getAuthenticatedUser(req, res, user -> {
            user.setPlayerUuid(null);
            user.setPlayerName(null);
            writeSuccess(res, Map.of("message", "Account unlinked"));
        });
    }

    /**
     * Link a player account (called from in-game command).
     * 
     * @param code       The link code entered by the player
     * @param playerUuid The player's UUID
     * @param playerName The player's name
     * @return true if linking succeeded, false if code is invalid/expired
     */
    public boolean linkPlayer(String code, UUID playerUuid, String playerName) {
        var pendingLink = linkCodeManager.consumeCode(code);
        if (pendingLink.isEmpty()) {
            return false;
        }

        DesignerUser user = usersById.get(pendingLink.get().userId());
        if (user == null) {
            return false;
        }

        user.setPlayerUuid(playerUuid);
        user.setPlayerName(playerName);
        return true;
    }

    /**
     * Get a user by their Discord ID.
     * 
     * @param discordId The Discord user ID
     * @return The user, or empty if not found
     */
    public Optional<DesignerUser> getUserByDiscordId(String discordId) {
        return Optional.ofNullable(usersByDiscordId.get(discordId));
    }

    /**
     * Get a user by their internal ID.
     * 
     * @param userId The user ID
     * @return The user, or empty if not found
     */
    public Optional<DesignerUser> getUserById(String userId) {
        return Optional.ofNullable(usersById.get(userId));
    }

    /**
     * Extract authenticated user from request and call handler if valid.
     */
    private void getAuthenticatedUser(HttpRequest req, HttpResponse res, Consumer<DesignerUser> handler) {
        var authHeader = req.getHeader("Authorization");
        if (authHeader.isEmpty() || !authHeader.get().startsWith("Bearer ")) {
            writeError(res, 401, "Missing or invalid Authorization header");
            return;
        }

        try {
            String token = authHeader.get().substring(7);
            String userId = jwtService.getUserIdFromToken(token);
            DesignerUser user = usersById.get(userId);
            
            if (user == null) {
                writeError(res, 401, "User not found");
                return;
            }
            
            handler.accept(user);
        } catch (Exception e) {
            writeError(res, 401, "Invalid token");
        }
    }

    private AuthUser toAuthUser(DesignerUser user) {
        return new AuthUser(
            user.getId(),
            user.getDiscordId(),
            user.getDiscordUsername(),
            user.getDiscordAvatar(),
            user.getPlayerUuid(),
            user.getPlayerName(),
            user.getPermissions().toString(),
            user.getRoles().stream().map(Enum::name).toList(),
            user.getCreatedAt().toString(),
            user.getLastLogin().toString()
        );
    }

    private void writeSuccess(HttpResponse res, Object data) {
        res.setStatus(200);
        res.writeJson(gson.toJson(Map.of("success", true, "data", data)));
    }

    private void writeError(HttpResponse res, int status, String message) {
        res.setStatus(status);
        res.writeJson(gson.toJson(Map.of("success", false, "error", message)));
    }

    private void writeErrorResponse(HttpResponse res, int status, String message) {
        writeError(res, status, message);
    }
}
