package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * An analytics event representing client-side behavioral telemetry from Vortex widgets
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VortexAnalyticsEvent {

    /** Unique identifier for this analytics event */
    @JsonProperty("id")
    private String id;

    /** Event name (e.g., "widget.opened", "invite.sent", "link.clicked") */
    @JsonProperty("name")
    private String name;

    /** Your Vortex account ID */
    @JsonProperty("accountId")
    private String accountId;

    /** Organization ID if using multi-org setup */
    @JsonProperty("organizationId")
    private String organizationId;

    /** Project ID the event belongs to */
    @JsonProperty("projectId")
    private String projectId;

    /** Environment ID (production, staging, etc.) */
    @JsonProperty("environmentId")
    private String environmentId;

    /** Deployment ID the event is associated with */
    @JsonProperty("deploymentId")
    private String deploymentId;

    /** Widget configuration ID that generated this event */
    @JsonProperty("widgetConfigurationId")
    private String widgetConfigurationId;

    /** Your internal user ID who triggered the event */
    @JsonProperty("foreignUserId")
    private String foreignUserId;

    /** Client session ID for grouping related events */
    @JsonProperty("sessionId")
    private String sessionId;

    /** Event-specific payload data */
    @JsonProperty("payload")
    private Map<String, Object> payload;

    /** Platform: "web", "ios", "android", "react-native" */
    @JsonProperty("platform")
    private String platform;

    /** A/B test segmentation identifier */
    @JsonProperty("segmentation")
    private String segmentation;

    /** ISO 8601 timestamp when the event occurred */
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
