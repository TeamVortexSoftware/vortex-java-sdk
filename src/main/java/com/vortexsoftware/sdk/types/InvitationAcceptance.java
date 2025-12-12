package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

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

    @JsonProperty("targetType")
    private String targetType;

    @JsonProperty("targetValue")
    private String targetValue;

    @JsonProperty("identifiers")
    private Map<String, String> identifiers;

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

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    public Map<String, String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(Map<String, String> identifiers) {
        this.identifiers = identifiers;
    }

    @Override
    public String toString() {
        return "InvitationAcceptance{" +
                "id='" + id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", acceptedAt='" + acceptedAt + '\'' +
                ", targetType='" + targetType + '\'' +
                ", targetValue='" + targetValue + '\'' +
                '}';
    }
}