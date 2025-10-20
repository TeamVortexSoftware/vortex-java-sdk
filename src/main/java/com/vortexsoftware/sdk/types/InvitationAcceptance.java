package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an accepted invitation
 */
public class InvitationAcceptance {
    @JsonProperty("id")
    private String id;

    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("projectId")
    private String projectId;

    @JsonProperty("acceptedAt")
    private String acceptedAt;

    @JsonProperty("target")
    private InvitationTarget target;

    public InvitationAcceptance() {}

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

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(String acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public InvitationTarget getTarget() {
        return target;
    }

    public void setTarget(InvitationTarget target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "InvitationAcceptance{" +
                "id='" + id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", acceptedAt='" + acceptedAt + '\'' +
                ", target=" + target +
                '}';
    }
}