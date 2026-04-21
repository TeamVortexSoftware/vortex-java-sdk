package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * A Vortex webhook event representing a server-side state change
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VortexWebhookEvent {

    /** Unique identifier for this webhook event */
    @JsonProperty("id")
    private String id;

    /** Event type (e.g., "invitation.accepted", "member.created") */
    @JsonProperty("type")
    private String type;

    /** ISO 8601 timestamp when the event occurred */
    @JsonProperty("timestamp")
    private String timestamp;

    /** Your Vortex account ID */
    @JsonProperty("accountId")
    private String accountId;

    /** Environment ID (production, staging, etc.) */
    @JsonProperty("environmentId")
    private String environmentId;

    /** Internal: database table that triggered this event */
    @JsonProperty("sourceTable")
    private String sourceTable;

    /** Database operation: "INSERT", "UPDATE", or "DELETE" */
    @JsonProperty("operation")
    private String operation;

    /** Event payload containing the relevant entity data */
    @JsonProperty("data")
    private Map<String, Object> data;

    public VortexWebhookEvent() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getEnvironmentId() { return environmentId; }
    public void setEnvironmentId(String environmentId) { this.environmentId = environmentId; }

    public String getSourceTable() { return sourceTable; }
    public void setSourceTable(String sourceTable) { this.sourceTable = sourceTable; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }

    @Override
    public String toString() {
        return "VortexWebhookEvent{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", accountId='" + accountId + '\'' +
                ", operation='" + operation + '\'' +
                '}';
    }
}
