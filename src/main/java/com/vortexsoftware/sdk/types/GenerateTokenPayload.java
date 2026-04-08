package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Payload for generateToken()
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenerateTokenPayload {
    @JsonProperty("user")
    private TokenUser user;

    @JsonProperty("component")
    private String component;

    @JsonProperty("trigger")
    private String trigger;

    @JsonProperty("embed")
    private String embed;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("vars")
    private Map<String, Object> vars;

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
