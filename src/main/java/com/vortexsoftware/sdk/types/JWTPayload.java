package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * JWT payload for Vortex token generation - the claims encoded in the signed token
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JWTPayload {
    /** Your internal user ID (required - used for invitation attribution) */
    @JsonProperty("userId")
    private String userId;

    /** User's email address (preferred format for user identification) */
    @JsonProperty("userEmail")
    private String userEmail;

    /** Whether user can manage autojoin settings for their scopes */
    @JsonProperty("userIsAutojoinAdmin")
    private Boolean userIsAutojoinAdmin;

    /** Legacy: List of user identifiers. Use userEmail instead. */
    @JsonProperty("identifiers")
    private List<Identifier> identifiers;

    /** Legacy: List of groups/scopes. Use scope parameter in generateToken instead. */
    @JsonProperty("groups")
    private List<Group> groups;

    /** Legacy: User role. No longer used. */
    @JsonProperty("role")
    private String role;

    public JWTPayload() {}

    /**
     * Create a payload with new simplified format (recommended)
     */
    public JWTPayload(String userId, String userEmail, Boolean userIsAutojoinAdmin) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userIsAutojoinAdmin = userIsAutojoinAdmin;
    }

    /**
     * Create a payload with legacy format (deprecated)
     * @deprecated Use constructor with userEmail instead
     */
    @Deprecated
    public JWTPayload(String userId, List<Identifier> identifiers, List<Group> groups, String role) {
        this.userId = userId;
        this.identifiers = identifiers;
        this.groups = groups;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Boolean getUserIsAutojoinAdmin() {
        return userIsAutojoinAdmin;
    }

    public void setUserIsAutojoinAdmin(Boolean userIsAutojoinAdmin) {
        this.userIsAutojoinAdmin = userIsAutojoinAdmin;
    }

    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<Identifier> identifiers) {
        this.identifiers = identifiers;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "JWTPayload{" +
                "userId='" + userId + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userIsAutojoinAdmin=" + userIsAutojoinAdmin +
                ", identifiers=" + identifiers +
                ", groups=" + groups +
                ", role='" + role + '\'' +
                '}';
    }
}