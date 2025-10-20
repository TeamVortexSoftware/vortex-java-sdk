package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the target of an invitation (email, SMS, etc.)
 */
public class InvitationTarget {
    @JsonProperty("type")
    private String type;

    @JsonProperty("value")
    private String value;

    public InvitationTarget() {}

    public InvitationTarget(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}