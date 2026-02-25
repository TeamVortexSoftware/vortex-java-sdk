package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * An analytics event representing client-side behavioral telemetry.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VortexAnalyticsEvent {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("organizationId")
    private String organizationId;

    @JsonProperty("projectId")
    private String projectId;

    @JsonProperty("environmentId")
    private String environmentId;

    @JsonProperty("deploymentId")
    private String deploymentId;

    @JsonProperty("widgetConfigurationId")
    private String widgetConfigurationId;

    @JsonProperty("foreignUserId")
    private String foreignUserId;

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("payload")
    private Map<String, Object> payload;

    @JsonProperty("platform")
    private String platform;

    @JsonProperty("segmentation")
    private String segmentation;

    @JsonProperty("timestamp")
    private String timestamp;

    public VortexAnalyticsEvent() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getEnvironmentId() { return environmentId; }
    public void setEnvironmentId(String environmentId) { this.environmentId = environmentId; }

    public String getDeploymentId() { return deploymentId; }
    public void setDeploymentId(String deploymentId) { this.deploymentId = deploymentId; }

    public String getWidgetConfigurationId() { return widgetConfigurationId; }
    public void setWidgetConfigurationId(String widgetConfigurationId) { this.widgetConfigurationId = widgetConfigurationId; }

    public String getForeignUserId() { return foreignUserId; }
    public void setForeignUserId(String foreignUserId) { this.foreignUserId = foreignUserId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getSegmentation() { return segmentation; }
    public void setSegmentation(String segmentation) { this.segmentation = segmentation; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "VortexAnalyticsEvent{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", accountId='" + accountId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
