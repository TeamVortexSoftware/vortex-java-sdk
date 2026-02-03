package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Type of invitation
 */
public enum InvitationType {
    SINGLE_USE("single_use"),
    MULTI_USE("multi_use"),
    AUTOJOIN("autojoin");

    private final String value;

    InvitationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static InvitationType fromValue(String value) {
        for (InvitationType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown InvitationType: " + value);
    }
}
