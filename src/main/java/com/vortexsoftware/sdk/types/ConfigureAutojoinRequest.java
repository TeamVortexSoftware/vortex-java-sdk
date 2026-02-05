package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Request body for configuring autojoin domains
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigureAutojoinRequest {
    @JsonProperty("scope")
    private String scope;

    @JsonProperty("scopeType")
    private String scopeType;

    @JsonProperty("scopeName")
    private String scopeName;

    @JsonProperty("domains")
    private List<String> domains;

    @JsonProperty("widgetId")
    private String widgetId;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    public ConfigureAutojoinRequest() {}

    public ConfigureAutojoinRequest(String scope, String scopeType, List<String> domains, String widgetId) {
        this.scope = scope;
        this.scopeType = scopeType;
        this.domains = domains;
        this.widgetId = widgetId;
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

    public String getScopeName() {
        return scopeName;
    }

    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public String getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(String widgetId) {
        this.widgetId = widgetId;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
