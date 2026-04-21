package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * User data for token generation - represents the authenticated user sending invitations
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenUser {
    /**
     * Unique identifier for the user in your system.
     * Used to attribute invitations and track referral chains.
     */
    @JsonProperty("id")
    private String id;

    /**
     * Display name shown to invitation recipients (e.g., "John invited you").
     * If not provided, falls back to email or a generic message.
     */
    @JsonProperty("name")
    private String name;

    /**
     * User's email address. Used for reply-to in invitation emails
     * and shown to recipients so they know who invited them.
     */
    @JsonProperty("email")
    private String email;

    /**
     * URL to user's avatar image. Displayed in invitation emails
     * and widgets to personalize the invitation experience.
     */
    @JsonProperty("avatarUrl")
    private String avatarUrl;

    /**
     * List of scope IDs where this user has admin privileges.
     * Admins can manage invitations and view analytics for these scopes.
     * Format: ["scopeType:scopeId", ...] (e.g., ["team:team-123", "org:org-456"]).
     */
    @JsonProperty("adminScopes")
    private List<String> adminScopes;

    /**
     * Restrict invitations to specific email domains.
     * If set, users can only invite people with emails matching these domains.
     * Useful for enterprise accounts (e.g., ["acme.com", "acme.co.uk"]).
     */
    @JsonProperty("allowedEmailDomains")
    private List<String> allowedEmailDomains;

    public TokenUser() {}

    public TokenUser(String id) {
        this.id = id;
    }

    public TokenUser(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() { return id; }
    public TokenUser setId(String id) { this.id = id; return this; }

    public String getName() { return name; }
    public TokenUser setName(String name) { this.name = name; return this; }

    public String getEmail() { return email; }
    public TokenUser setEmail(String email) { this.email = email; return this; }

    public String getAvatarUrl() { return avatarUrl; }
    public TokenUser setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; return this; }

    public List<String> getAdminScopes() { return adminScopes; }
    public TokenUser setAdminScopes(List<String> adminScopes) { this.adminScopes = adminScopes; return this; }

    public List<String> getAllowedEmailDomains() { return allowedEmailDomains; }
    public TokenUser setAllowedEmailDomains(List<String> allowedEmailDomains) { this.allowedEmailDomains = allowedEmailDomains; return this; }

    // Builder pattern
    public TokenUser withName(String name) {
        this.name = name;
        return this;
    }

    public TokenUser withEmail(String email) {
        this.email = email;
        return this;
    }

    public TokenUser withAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public TokenUser withAdminScopes(List<String> adminScopes) {
        this.adminScopes = adminScopes;
        return this;
    }

    public TokenUser withAllowedEmailDomains(List<String> allowedEmailDomains) {
        this.allowedEmailDomains = allowedEmailDomains;
        return this;
    }
}
