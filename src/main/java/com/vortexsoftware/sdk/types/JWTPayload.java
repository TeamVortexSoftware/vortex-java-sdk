package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * JWT payload for Vortex token generation
 *
 * Supports both new simplified format (userEmail, userIsAutoJoinAdmin) and
 * legacy format (identifiers, groups, role) for backward compatibility.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JWTPayload {
    @JsonProperty("userId")
    private String userId;

    // New simplified fields (preferred)
    @JsonProperty("userEmail")
    private String userEmail;

    @JsonProperty("userIsAutoJoinAdmin")
    private Boolean userIsAutoJoinAdmin;

    // Legacy fields (deprecated but still supported for backward compatibility)
    @JsonProperty("identifiers")
    private List<Identifier> identifiers;

    @JsonProperty("groups")
    private List<Group> groups;

    @JsonProperty("role")
    private String role;

    public JWTPayload() {}

    /**
     * Create a payload with new simplified format (recommended)
     */
    public JWTPayload(String userId, String userEmail, Boolean userIsAutoJoinAdmin) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userIsAutoJoinAdmin = userIsAutoJoinAdmin;
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

    public Boolean getUserIsAutoJoinAdmin() {
        return userIsAutoJoinAdmin;
    }

    public void setUserIsAutoJoinAdmin(Boolean userIsAutoJoinAdmin) {
        this.userIsAutoJoinAdmin = userIsAutoJoinAdmin;
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
                ", userIsAutoJoinAdmin=" + userIsAutoJoinAdmin +
                ", identifiers=" + identifiers +
                ", groups=" + groups +
                ", role='" + role + '\'' +
                '}';
    }
}