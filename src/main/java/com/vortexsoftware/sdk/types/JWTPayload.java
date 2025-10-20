package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * JWT payload for Vortex token generation
 */
public class JWTPayload {
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("identifiers")
    private List<InvitationTarget> identifiers;

    @JsonProperty("groups")
    private List<InvitationGroup> groups;

    @JsonProperty("role")
    private String role;

    public JWTPayload() {}

    public JWTPayload(String userId, List<InvitationTarget> identifiers, List<InvitationGroup> groups, String role) {
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

    public List<InvitationTarget> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<InvitationTarget> identifiers) {
        this.identifiers = identifiers;
    }

    public List<InvitationGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<InvitationGroup> groups) {
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
                ", identifiers=" + identifiers +
                ", groups=" + groups +
                ", role='" + role + '\'' +
                '}';
    }
}