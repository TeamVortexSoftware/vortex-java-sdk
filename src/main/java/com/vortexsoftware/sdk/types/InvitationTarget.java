package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the target of an invitation (email, phone, share, internal)
 */
public class InvitationTarget {
    @JsonProperty("type")
    private InvitationTargetType type;

    @JsonProperty("value")
    private String value;

    public InvitationTarget() {}

    public InvitationTarget(InvitationTargetType type, String value) {
        this.type = type;
        this.value = value;
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

    @Override
    public String toString() {
        return "InvitationTarget{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
