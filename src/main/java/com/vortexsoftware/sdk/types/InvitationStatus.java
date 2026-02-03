package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Current status of an invitation
 */
public enum InvitationStatus {
    QUEUED("queued"),
    SENDING("sending"),
    SENT("sent"),
    DELIVERED("delivered"),
    ACCEPTED("accepted"),
    SHARED("shared"),
    UNFURLED("unfurled"),
    ACCEPTED_ELSEWHERE("accepted_elsewhere");

    private final String value;

    InvitationStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static InvitationStatus fromValue(String value) {
        for (InvitationStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown InvitationStatus: " + value);
    }
}
