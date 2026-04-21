package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a scope/group for JWT generation - used to define user's team/org membership in tokens
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group {
    /** Scope type (e.g., "team", "organization", "workspace") */
    @JsonProperty("type")
    private String type;

    /** Legacy scope identifier. Use scopeId instead. */
    @JsonProperty("id")
    private String id;

    /** Your internal scope/group identifier (preferred over id) */
    @JsonProperty("scope")
    private String scope;

    /** Display name for the scope (e.g., "Engineering Team") */
    @JsonProperty("name")
    private String name;

    public Group() {}

    public Group(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public Group(String type, String name, String id, String scope) {
        this.type = type;
        this.name = name;
        this.id = id;
        this.scope = scope;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return scope;
    }

    public void setGroupId(String scope) {
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Group{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", scope='" + scope + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
