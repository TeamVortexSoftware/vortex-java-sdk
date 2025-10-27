package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a group associated with an invitation.
 * This matches the MemberGroups table structure from the API response.
 */
public class InvitationGroup {
    /** Vortex internal UUID */
    @JsonProperty("id")
    private String id;

    /** Vortex account ID */
    @JsonProperty("accountId")
    private String accountId;

    /** Customer's group ID (the ID they provided to Vortex) */
    @JsonProperty("groupId")
    private String groupId;

    /** Group type (e.g., "workspace", "team") */
    @JsonProperty("type")
    private String type;

    /** Group name */
    @JsonProperty("name")
    private String name;

    /** ISO 8601 timestamp when the group was created */
    @JsonProperty("createdAt")
    private String createdAt;

    public InvitationGroup() {}

    public InvitationGroup(String id, String accountId, String groupId, String type, String name, String createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.groupId = groupId;
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
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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
        return "InvitationGroup{" +
                "id='" + id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}