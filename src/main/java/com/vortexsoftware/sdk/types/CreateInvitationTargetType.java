package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Target type for creating invitations
 */
public enum CreateInvitationTargetType {
    EMAIL("email"),
    PHONE("phone"),
    INTERNAL("internal");

    private final String value;

    CreateInvitationTargetType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static CreateInvitationTargetType fromValue(String value) {
        for (CreateInvitationTargetType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown CreateInvitationTargetType: " + value);
    }
}
