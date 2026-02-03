package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Complete invitation result as returned by the Vortex API
 */
public class InvitationResult {
    @JsonProperty("id")
    private String id;

    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("clickThroughs")
    private int clickThroughs;

    @JsonProperty("configurationAttributes")
    private Map<String, Object> configurationAttributes;

    @JsonProperty("attributes")
    private Map<String, Object> attributes;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("deactivated")
    private boolean deactivated;

    @JsonProperty("deliveryCount")
    private int deliveryCount;

    @JsonProperty("deliveryTypes")
    private List<DeliveryType> deliveryTypes;

    @JsonProperty("foreignCreatorId")
    private String foreignCreatorId;

    @JsonProperty("invitationType")
    private InvitationType invitationType;

    @JsonProperty("modifiedAt")
    private String modifiedAt;

    @JsonProperty("status")
    private InvitationStatus status;

    @JsonProperty("target")
    private List<InvitationTarget> target;

    @JsonProperty("views")
    private int views;

    @JsonProperty("widgetConfigurationId")
    private String widgetConfigurationId;

    @JsonProperty("deploymentId")
    private String deploymentId;

    @JsonProperty("projectId")
    private String projectId;

    @JsonProperty("groups")
    private List<InvitationGroup> groups;

    @JsonProperty("accepts")
    private List<InvitationAcceptance> accepts;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("scopeType")
    private String scopeType;

    @JsonProperty("expired")
    private boolean expired;

    @JsonProperty("expires")
    private String expires;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    @JsonProperty("passThrough")
    private String passThrough;

    @JsonProperty("source")
    private String source;

    @JsonProperty("creatorName")
    private String creatorName;

    @JsonProperty("creatorAvatarUrl")
    private String creatorAvatarUrl;

    public InvitationResult() {}

    // Getters and setters
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

    public int getClickThroughs() {
        return clickThroughs;
    }

    public void setClickThroughs(int clickThroughs) {
        this.clickThroughs = clickThroughs;
    }

    public Map<String, Object> getConfigurationAttributes() {
        return configurationAttributes;
    }

    public void setConfigurationAttributes(Map<String, Object> configurationAttributes) {
        this.configurationAttributes = configurationAttributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public int getDeliveryCount() {
        return deliveryCount;
    }

    public void setDeliveryCount(int deliveryCount) {
        this.deliveryCount = deliveryCount;
    }

    public List<DeliveryType> getDeliveryTypes() {
        return deliveryTypes;
    }

    public void setDeliveryTypes(List<DeliveryType> deliveryTypes) {
        this.deliveryTypes = deliveryTypes;
    }

    public String getForeignCreatorId() {
        return foreignCreatorId;
    }

    public void setForeignCreatorId(String foreignCreatorId) {
        this.foreignCreatorId = foreignCreatorId;
    }

    public InvitationType getInvitationType() {
        return invitationType;
    }

    public void setInvitationType(InvitationType invitationType) {
        this.invitationType = invitationType;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public List<InvitationTarget> getTarget() {
        return target;
    }

    public void setTarget(List<InvitationTarget> target) {
        this.target = target;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getWidgetConfigurationId() {
        return widgetConfigurationId;
    }

    public void setWidgetConfigurationId(String widgetConfigurationId) {
        this.widgetConfigurationId = widgetConfigurationId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<InvitationGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<InvitationGroup> groups) {
        this.groups = groups;
    }

    public List<InvitationAcceptance> getAccepts() {
        return accepts;
    }

    public void setAccepts(List<InvitationAcceptance> accepts) {
        this.accepts = accepts;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getPassThrough() {
        return passThrough;
    }

    public void setPassThrough(String passThrough) {
        this.passThrough = passThrough;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorAvatarUrl() {
        return creatorAvatarUrl;
    }

    public void setCreatorAvatarUrl(String creatorAvatarUrl) {
        this.creatorAvatarUrl = creatorAvatarUrl;
    }

    @Override
    public String toString() {
        return "InvitationResult{" +
                "id='" + id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", status='" + status + '\'' +
                ", invitationType='" + invitationType + '\'' +
                ", projectId='" + projectId + '\'' +
                ", scope='" + scope + '\'' +
                ", scopeType='" + scopeType + '\'' +
                '}';
    }
}