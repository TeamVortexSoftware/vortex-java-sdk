package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Target type for invitation responses
 */
public enum InvitationTargetType {
    EMAIL("email"),
    PHONE("phone"),
    SHARE("share"),
    INTERNAL("internal");

    private final String value;

    InvitationTargetType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static InvitationTargetType fromValue(String value) {
        for (InvitationTargetType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown InvitationTargetType: " + value);
    }
}
