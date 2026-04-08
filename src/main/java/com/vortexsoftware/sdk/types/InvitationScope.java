package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a group associated with an invitation.
 * This matches the MemberGroups table structure from the API response.
 */
public class InvitationScope {
    /** Vortex internal UUID */
    @JsonProperty("id")
    private String id;

    /** Vortex account ID */
    @JsonProperty("accountId")
    private String accountId;

    /** Customer's group ID (the ID they provided to Vortex) */
    @JsonProperty("groupId")
    private String scope;

    /** Group type (e.g., "workspace", "team") */
    @JsonProperty("type")
    private String type;

    /** Group name */
    @JsonProperty("name")
    private String name;

    /** ISO 8601 timestamp when the group was created */
    @JsonProperty("createdAt")
    private String createdAt;

    public InvitationScope() {}

    public InvitationScope(String id, String accountId, String scope, String type, String name, String createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.scope = scope;
        this.type = type;
        this.name = name;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getGroupId() {
        return scope;
    }

    public void setGroupId(String scope) {
        this.scope = scope;
    }

    /** Preferred alias for getGroupId() */
    public String getScopeId() {
        return scope;
    }

    /** Preferred alias for setGroupId() */
    public void setScopeId(String scopeId) {
        this.scope = scopeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "InvitationScope{" +
                "id='" + id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", scope='" + scope + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}