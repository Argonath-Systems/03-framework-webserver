package com.argonathsystems.framework.webserver.auth;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Discord OAuth2 integration service.
 * 
 * <p>Handles the Discord OAuth2 flow for the Designer portal:
 * <ol>
 *   <li>Exchange authorization code for Discord tokens</li>
 *   <li>Fetch Discord user information</li>
 *   <li>Check guild membership and roles</li>
 *   <li>Map Discord roles to Designer roles</li>
 * </ol>
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public class DiscordOAuthService {

    private static final String DISCORD_API = "https://discord.com/api/v10";
    private static final String TOKEN_URL = DISCORD_API + "/oauth2/token";
    private static final String USER_URL = DISCORD_API + "/users/@me";
    private static final String GUILD_MEMBER_URL = DISCORD_API + "/guilds/%s/members/%s";

    private final HttpClient httpClient;
    private final Gson gson;
    private final String clientId;
    private final String clientSecret;
    private final String guildId;
    private final Function<String, DesignerRole> roleMapper;

    /**
     * Discord OAuth tokens returned from token exchange.
     * 
     * @param accessToken  Access token for API requests
     * @param refreshToken Refresh token for obtaining new access tokens
     * @param expiresIn    Token validity in seconds
     */
    public record DiscordTokens(String accessToken, String refreshToken, int expiresIn) {}
    
    /**
     * Discord user information.
     * 
     * @param id         Discord user ID (snowflake)
     * @param username   Discord username
     * @param globalName Display name (may be null)
     * @param avatar     Avatar hash (may be null for default avatar)
     */
    public record DiscordUser(String id, String username, String globalName, String avatar) {}

    /**
     * Create a Discord OAuth service.
     * 
     * @param clientId     Discord application client ID
     * @param clientSecret Discord application client secret
     * @param guildId      The Discord server (guild) ID to check membership
     * @param roleMapper   Function to map Discord role IDs to DesignerRoles
     */
    public DiscordOAuthService(String clientId, String clientSecret, 
                                String guildId, Function<String, DesignerRole> roleMapper) {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.guildId = guildId;
        this.roleMapper = roleMapper;
    }

    /**
     * Create a Discord OAuth service with a map-based role mapper.
     * 
     * @param clientId        Discord application client ID
     * @param clientSecret    Discord application client secret
     * @param guildId         The Discord server (guild) ID
     * @param discordRoleMap  Map of Discord role IDs to DesignerRoles
     */
    public DiscordOAuthService(String clientId, String clientSecret,
                                String guildId, Map<String, DesignerRole> discordRoleMap) {
        this(clientId, clientSecret, guildId, discordRoleMap::get);
    }

    /**
     * Exchange Discord authorization code for tokens.
     * 
     * @param code        The authorization code from OAuth callback
     * @param redirectUri The redirect URI used in the authorization request
     * @return Discord token pair
     * @throws IOException          If network request fails
     * @throws InterruptedException If request is interrupted
     */
    public DiscordTokens exchangeCode(String code, String redirectUri) 
            throws IOException, InterruptedException {
        String body = String.format(
            "client_id=%s&client_secret=%s&grant_type=authorization_code&code=%s&redirect_uri=%s",
            URLEncoder.encode(clientId, StandardCharsets.UTF_8),
            URLEncoder.encode(clientSecret, StandardCharsets.UTF_8),
            URLEncoder.encode(code, StandardCharsets.UTF_8),
            URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
        );

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(TOKEN_URL))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("Discord token exchange failed: " + response.body());
        }

        JsonObject json = gson.fromJson(response.body(), JsonObject.class);
        return new DiscordTokens(
            json.get("access_token").getAsString(),
            json.get("refresh_token").getAsString(),
            json.get("expires_in").getAsInt()
        );
    }

    /**
     * Get Discord user information using an access token.
     * 
     * @param accessToken Discord OAuth access token
     * @return Discord user information
     * @throws IOException          If network request fails
     * @throws InterruptedException If request is interrupted
     */
    public DiscordUser getUser(String accessToken) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(USER_URL))
            .header("Authorization", "Bearer " + accessToken)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("Discord user fetch failed: " + response.body());
        }

        JsonObject json = gson.fromJson(response.body(), JsonObject.class);
        return new DiscordUser(
            json.get("id").getAsString(),
            json.get("username").getAsString(),
            json.has("global_name") && !json.get("global_name").isJsonNull() 
                ? json.get("global_name").getAsString() : null,
            json.has("avatar") && !json.get("avatar").isJsonNull() 
                ? json.get("avatar").getAsString() : null
        );
    }

    /**
     * Check if user is a member of the guild and get their Designer roles.
     * 
     * <p>This method uses a Discord bot token to fetch guild member information,
     * then maps the user's Discord roles to Designer roles using the configured
     * role mapper.
     * 
     * @param discordUserId The Discord user ID to check
     * @param botToken      Discord bot token with guilds.members.read permission
     * @return Set of DesignerRoles the user has (empty if not authorized)
     * @throws IOException          If network request fails
     * @throws InterruptedException If request is interrupted
     */
    public Set<DesignerRole> getDesignerRoles(String discordUserId, String botToken) 
            throws IOException, InterruptedException {
        String url = String.format(GUILD_MEMBER_URL, guildId, discordUserId);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bot " + botToken)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 404) {
            // User is not in the guild
            return Set.of();
        }
        
        if (response.statusCode() != 200) {
            throw new IOException("Discord guild member fetch failed: " + response.body());
        }

        JsonObject json = gson.fromJson(response.body(), JsonObject.class);
        JsonArray roles = json.getAsJsonArray("roles");
        
        Set<DesignerRole> designerRoles = new HashSet<>();
        
        for (var roleElement : roles) {
            String roleId = roleElement.getAsString();
            DesignerRole designerRole = roleMapper.apply(roleId);
            if (designerRole != null) {
                designerRoles.add(designerRole);
            }
        }
        
        return designerRoles;
    }

    /**
     * Get the Discord avatar URL for a user.
     * 
     * @param discordId  Discord user ID
     * @param avatarHash Avatar hash from user info (may be null)
     * @param size       Image size (power of 2, 16-4096)
     * @return Avatar URL or null if no custom avatar
     */
    public static String getAvatarUrl(String discordId, String avatarHash, int size) {
        if (avatarHash == null) {
            return null;
        }
        return String.format("https://cdn.discordapp.com/avatars/%s/%s.png?size=%d",
            discordId, avatarHash, size);
    }
}
