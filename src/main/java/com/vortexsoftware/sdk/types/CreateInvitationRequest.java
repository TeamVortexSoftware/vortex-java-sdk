package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Request body for creating an invitation via the public API (backend SDK use).
 *
 * <p>This allows creating invitations programmatically using your API key,
 * without requiring a user JWT token. Useful for server-side invitation creation,
 * such as "People You May Know" flows or admin-initiated invitations.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * CreateInvitationRequest request = new CreateInvitationRequest(
 *     "widget-config-123",
 *     new CreateInvitationTarget("email", "invitee@example.com"),
 *     new Inviter("user-456", "inviter@example.com", "John Doe", null)
 * );
 * request.setGroups(Arrays.asList(
 *     new CreateInvitationGroup("team", "team-789", "Engineering")
 * ));
 * CreateInvitationResponse response = client.createInvitation(request);
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateInvitationRequest {

    @JsonProperty("widgetConfigurationId")
    private String widgetConfigurationId;

    @JsonProperty("target")
    private CreateInvitationTarget target;

    @JsonProperty("inviter")
    private Inviter inviter;

    @JsonProperty("groups")
    private List<CreateInvitationGroup> groups;

    @JsonProperty("source")
    private String source;

    /** Customer-defined subtype for analytics segmentation (e.g., "pymk", "find-friends") */
    @JsonProperty("subtype")
    private String subtype;

    @JsonProperty("templateVariables")
    private Map<String, String> templateVariables;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    @JsonProperty("unfurlConfig")
    private UnfurlConfig unfurlConfig;

    public CreateInvitationRequest() {
    }

    public CreateInvitationRequest(String widgetConfigurationId, CreateInvitationTarget target, Inviter inviter) {
        this.widgetConfigurationId = widgetConfigurationId;
        this.target = target;
        this.inviter = inviter;
    }

    // Getters and Setters

    public String getWidgetConfigurationId() {
        return widgetConfigurationId;
    }

    public void setWidgetConfigurationId(String widgetConfigurationId) {
        this.widgetConfigurationId = widgetConfigurationId;
    }

    public CreateInvitationTarget getTarget() {
        return target;
    }

    public void setTarget(CreateInvitationTarget target) {
        this.target = target;
    }

    public Inviter getInviter() {
        return inviter;
    }

    public void setInviter(Inviter inviter) {
        this.inviter = inviter;
    }

    public List<CreateInvitationGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<CreateInvitationGroup> groups) {
        this.groups = groups;
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

    public Map<String, String> getTemplateVariables() {
        return templateVariables;
    }

    public void setTemplateVariables(Map<String, String> templateVariables) {
        this.templateVariables = templateVariables;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public UnfurlConfig getUnfurlConfig() {
        return unfurlConfig;
    }

    public void setUnfurlConfig(UnfurlConfig unfurlConfig) {
        this.unfurlConfig = unfurlConfig;
    }
}
