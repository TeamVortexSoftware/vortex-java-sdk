package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Represents a record of an invitation being accepted
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvitationAcceptance {
    /** Unique identifier for this acceptance record */
    @JsonProperty("id")
    private String id;

    /** Your Vortex account ID */
    @JsonProperty("accountId")
    private String accountId;

    /** ISO 8601 timestamp when the invitation was accepted */
    @JsonProperty("acceptedAt")
    private String acceptedAt;

    /** How the recipient was identified: "email" or "phone" */
    @JsonProperty("targetType")
    private String targetType;

    /** The email or phone number of the person who accepted */
    @JsonProperty("targetValue")
    private String targetValue;

    /** Additional identifiers for the accepting user (e.g., external IDs) */
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
                ", acceptedAt='" + acceptedAt + '\'' +
                ", targetType='" + targetType + '\'' +
                ", targetValue='" + targetValue + '\'' +
                '}';
    }
}