package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * A Vortex webhook event representing a server-side state change.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VortexWebhookEvent {

    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("environmentId")
    private String environmentId;

    @JsonProperty("sourceTable")
    private String sourceTable;

    @JsonProperty("operation")
    private String operation;

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
