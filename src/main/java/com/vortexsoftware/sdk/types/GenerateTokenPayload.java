package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Payload for generateToken() - used to generate secure tokens for Vortex components
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenerateTokenPayload {
    /**
     * The authenticated user who will be using the Vortex component.
     * Required for most operations to attribute invitations correctly.
     */
    @JsonProperty("user")
    private TokenUser user;

    /**
     * Component ID to generate token for (from your Vortex dashboard).
     * If not specified, uses the default component for your account.
     */
    @JsonProperty("component")
    private String component;

    /**
     * Trigger context for the invitation (e.g., "signup", "share-button", "referral-page").
     * Used for analytics to track which UI elements drive the most invitations.
     */
    @JsonProperty("trigger")
    private String trigger;

    /**
     * Embed mode identifier for embedded invitation widgets.
     * Determines how the component renders in your UI.
     */
    @JsonProperty("embed")
    private String embed;

    /**
     * Scope identifier to restrict invitations to a specific team/org/workspace.
     * Format: "scopeType:scopeId" (e.g., "team:team-123").
     */
    @JsonProperty("scope")
    private String scope;

    /**
     * Custom variables to pass to the component for template rendering.
     * These can be used in email templates and invitation messages.
     */
    @JsonProperty("vars")
    private Map<String, Object> vars;

    /** Additional properties for forward compatibility */
    private Map<String, Object> additionalProperties;

    public GenerateTokenPayload() {}

    public GenerateTokenPayload(TokenUser user) {
        this.user = user;
    }

    public TokenUser getUser() { return user; }
    public GenerateTokenPayload setUser(TokenUser user) { this.user = user; return this; }

    public String getComponent() { return component; }
    public GenerateTokenPayload setComponent(String component) { this.component = component; return this; }

    public String getTrigger() { return trigger; }
    public GenerateTokenPayload setTrigger(String trigger) { this.trigger = trigger; return this; }

    public String getEmbed() { return embed; }
    public GenerateTokenPayload setEmbed(String embed) { this.embed = embed; return this; }

    public String getScope() { return scope; }
    public GenerateTokenPayload setScope(String scope) { this.scope = scope; return this; }

    public Map<String, Object> getVars() { return vars; }
    public GenerateTokenPayload setVars(Map<String, Object> vars) { this.vars = vars; return this; }

    public Map<String, Object> getAdditionalProperties() { return additionalProperties; }
    public GenerateTokenPayload setAdditionalProperties(Map<String, Object> additionalProperties) { this.additionalProperties = additionalProperties; return this; }

    // Builder pattern
    public GenerateTokenPayload withUser(TokenUser user) {
        this.user = user;
        return this;
    }

    public GenerateTokenPayload withComponent(String component) {
        this.component = component;
        return this;
    }

    public GenerateTokenPayload withTrigger(String trigger) {
        this.trigger = trigger;
        return this;
    }

    public GenerateTokenPayload withEmbed(String embed) {
        this.embed = embed;
        return this;
    }

    public GenerateTokenPayload withScope(String scope) {
        this.scope = scope;
        return this;
    }

    public GenerateTokenPayload withVars(Map<String, Object> vars) {
        this.vars = vars;
        return this;
    }

    public GenerateTokenPayload withAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
        return this;
    }
}
