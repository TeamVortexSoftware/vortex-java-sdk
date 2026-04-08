package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * User data for JWT generation
 *
 * Required fields:
 * - id: User's unique identifier in your system
 * - email: User's email address
 *
 * Optional fields:
 * - name: User's display name (max 200 characters)
 * - avatarUrl: User's avatar URL (must be HTTPS, max 2000 characters)
 * - adminScopes: List of admin scopes (e.g., ["autojoin"])
 * - allowedEmailDomains: List of allowed email domains for invitation restrictions (e.g., ["acme.com", "acme.org"])
 *
 * Example:
 * <pre>{@code
 * User user = new User("user-123", "user@example.com");
 * user.setName("Jane Doe");
 * user.setAvatarUrl("https://example.com/avatars/jane.jpg");
 * user.setAdminScopes(Arrays.asList("autojoin"));
 * user.setAllowedEmailDomains(Arrays.asList("acme.com", "acme.org"));
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @JsonProperty("id")
    private String id;

    @JsonProperty("email")
    private String email;

    /** User's display name (preferred) */
    @JsonProperty("name")
    private String name;

    /** User's avatar URL (preferred) */
    @JsonProperty("avatarUrl")
    private String avatarUrl;

    /** @deprecated Use name instead */
    @JsonProperty("userName")
    @Deprecated
    private String userName;

    /** @deprecated Use avatarUrl instead */
    @JsonProperty("userAvatarUrl")
    @Deprecated
    private String userAvatarUrl;

    @JsonProperty("adminScopes")
    private List<String> adminScopes;

    @JsonProperty("allowedEmailDomains")
    private List<String> allowedEmailDomains;

    /**
     * Default constructor for Jackson deserialization
     */
    public User() {}

    /**
     * Create a new User with required fields
     *
     * @param id User's unique identifier
     * @param email User's email address
     */
    public User(String id, String email) {
        this.id = id;
        this.email = email;
    }

    /**
     * Create a new User with all fields
     *
     * @param id User's unique identifier
     * @param email User's email address
     * @param adminScopes List of admin scopes (e.g., ["autojoin"])
     */
    public User(String id, String email, List<String> adminScopes) {
        this.id = id;
        this.email = email;
        this.adminScopes = adminScopes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getAdminScopes() {
        return adminScopes;
    }

    public void setAdminScopes(List<String> adminScopes) {
        this.adminScopes = adminScopes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /** @deprecated Use getName() instead */
    @Deprecated
    public String getUserName() {
        return userName;
    }

    /** @deprecated Use setName() instead */
    @Deprecated
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /** @deprecated Use getAvatarUrl() instead */
    @Deprecated
    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    /** @deprecated Use setAvatarUrl() instead */
    @Deprecated
    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public List<String> getAllowedEmailDomains() {
        return allowedEmailDomains;
    }

    public void setAllowedEmailDomains(List<String> allowedEmailDomains) {
        this.allowedEmailDomains = allowedEmailDomains;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", adminScopes=" + adminScopes +
                ", allowedEmailDomains=" + allowedEmailDomains +
                '}';
    }
}
