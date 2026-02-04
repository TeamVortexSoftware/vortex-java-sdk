package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the target of an invitation (email, phone, share, internal)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvitationTarget {
    @JsonProperty("type")
    private InvitationTargetType type;

    @JsonProperty("value")
    private String value;

    /** Display name of the person being invited */
    @JsonProperty("name")
    private String name;

    /** Avatar URL for the person being invited (for display in invitation lists) */
    @JsonProperty("avatarUrl")
    private String avatarUrl;

    public InvitationTarget() {}

    public InvitationTarget(InvitationTargetType type, String value) {
        this.type = type;
        this.value = value;
    }

    public InvitationTarget(InvitationTargetType type, String value, String name, String avatarUrl) {
        this.type = type;
        this.value = value;
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    public static InvitationTarget email(String value) {
        return new InvitationTarget(InvitationTargetType.EMAIL, value);
    }

    public static InvitationTarget phone(String value) {
        return new InvitationTarget(InvitationTargetType.PHONE, value);
    }

    public InvitationTargetType getType() {
        return type;
    }

    public void setType(InvitationTargetType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "InvitationTarget{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", name='" + name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
