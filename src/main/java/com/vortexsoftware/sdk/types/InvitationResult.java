package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Complete invitation result as returned by the Vortex API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvitationResult {
    /** Unique identifier for this invitation */
    @JsonProperty("id")
    private String id;

    /** Your Vortex account ID */
    @JsonProperty("accountId")
    private String accountId;

    /** Number of times the invitation link was clicked */
    @JsonProperty("clickThroughs")
    private int clickThroughs;

    /** Invitation form data submitted by the user, including email addresses of invitees and the values of any custom fields. */
    @JsonProperty("formSubmissionData")
    private Map<String, Object> formSubmissionData;

    /**
     * @deprecated Use {@link #formSubmissionData} instead. Contains the same data.
     */
    @Deprecated
    @JsonProperty("configurationAttributes")
    private Map<String, Object> configurationAttributes;

    /** Custom attributes attached to this invitation */
    @JsonProperty("attributes")
    private Map<String, Object> attributes;

    /** ISO 8601 timestamp when the invitation was created */
    @JsonProperty("createdAt")
    private String createdAt;

    /** Whether this invitation has been revoked or expired */
    @JsonProperty("deactivated")
    private boolean deactivated;

    /** Number of times the invitation was sent (including reminders) */
    @JsonProperty("deliveryCount")
    private int deliveryCount;

    /** Channels used to deliver this invitation (email, sms, share link) */
    @JsonProperty("deliveryTypes")
    private List<DeliveryType> deliveryTypes;

    /** Your internal user ID for the person who created this invitation */
    @JsonProperty("foreignCreatorId")
    private String foreignCreatorId;

    /** Type of invitation: PERSONAL (1:1) or BROADCAST (1:many) */
    @JsonProperty("invitationType")
    private InvitationType invitationType;

    /** ISO 8601 timestamp of last modification */
    @JsonProperty("modifiedAt")
    private String modifiedAt;

    /** Current status: CREATED, DELIVERED, CLICKED, ACCEPTED, or EXPIRED */
    @JsonProperty("status")
    private InvitationStatus status;

    /** List of invitation recipients with their contact info and status */
    @JsonProperty("target")
    private List<InvitationTarget> target;

    /** Number of times the invitation page was viewed */
    @JsonProperty("views")
    private int views;

    /** Widget configuration ID used for this invitation */
    @JsonProperty("widgetConfigurationId")
    private String widgetConfigurationId;

    /** Deployment ID this invitation belongs to */
    @JsonProperty("deploymentId")
    private String deploymentId;

    /** Scopes (teams/orgs) this invitation grants access to */
    @JsonProperty("groups")
    private List<InvitationScope> groups;

    /** List of acceptance records if the invitation was accepted (optional, may be null) */
    @JsonProperty("accepts")
    private List<InvitationAcceptance> accepts;

    /** Primary scope identifier (e.g., "team-123") */
    @JsonProperty("scope")
    private String scope;

    /** Type of the primary scope (e.g., "team", "organization") */
    @JsonProperty("scopeType")
    private String scopeType;

    /** Whether this invitation has passed its expiration date */
    @JsonProperty("expired")
    private boolean expired;

    /** ISO 8601 timestamp when this invitation expires */
    @JsonProperty("expires")
    private String expires;

    /** Custom metadata attached to this invitation */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    /** Pass-through data returned unchanged in webhooks and callbacks */
    @JsonProperty("passThrough")
    private String passThrough;

    /** Source identifier for tracking (e.g., "ios-app", "web-dashboard") */
    @JsonProperty("source")
    private String source;

    /** Subtype for analytics segmentation (e.g., "pymk", "find-friends") */
    @JsonProperty("subtype")
    private String subtype;

    /** Display name of the user who created this invitation */
    @JsonProperty("creatorName")
    private String creatorName;

    /** Avatar URL of the user who created this invitation */
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

    public Map<String, Object> getFormSubmissionData() {
        return formSubmissionData;
    }

    public void setFormSubmissionData(Map<String, Object> formSubmissionData) {
        this.formSubmissionData = formSubmissionData;
    }

    /**
     * @deprecated Use {@link #getFormSubmissionData()} instead.
     */
    @Deprecated
    public Map<String, Object> getConfigurationAttributes() {
        return configurationAttributes;
    }

    /**
     * @deprecated Use {@link #setFormSubmissionData(Map)} instead.
     */
    @Deprecated
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

    public List<InvitationScope> getGroups() {
        return groups;
    }

    public void setGroups(List<InvitationScope> groups) {
        this.groups = groups;
    }

    /** Preferred alias for getGroups(). Returns the same list. */
    public List<InvitationScope> getScopes() {
        return groups;
    }

    /** Preferred alias for setGroups(). */
    public void setScopes(List<InvitationScope> scopes) {
        this.groups = scopes;
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

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
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
                ", scope='" + scope + '\'' +
                ", scopeType='" + scopeType + '\'' +
                '}';
    }
}