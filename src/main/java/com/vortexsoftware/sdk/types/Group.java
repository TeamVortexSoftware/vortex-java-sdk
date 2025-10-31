package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a group for JWT generation (input).
 * This is a simpler structure than InvitationGroup, used for creating JWTs.
 * Supports both 'id' (legacy) and 'groupId' (preferred) for backward compatibility.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group {
    @JsonProperty("type")
    private String type;

    @JsonProperty("id")
    private String id;

    @JsonProperty("groupId")
    private String groupId;

    @JsonProperty("name")
    private String name;

    public Group() {}

    public Group(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public Group(String type, String name, String id, String groupId) {
        this.type = type;
        this.name = name;
        this.id = id;
        this.groupId = groupId;
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
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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
                ", groupId='" + groupId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
