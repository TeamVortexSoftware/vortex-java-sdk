package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * User data for token generation
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenUser {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("avatarUrl")
    private String avatarUrl;

    @JsonProperty("adminScopes")
    private List<String> adminScopes;

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
