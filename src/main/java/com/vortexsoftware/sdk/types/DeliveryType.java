package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Delivery type for invitations
 */
public enum DeliveryType {
    EMAIL("email"),
    PHONE("phone"),
    SHARE("share"),
    INTERNAL("internal");

    private final String value;

    DeliveryType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static DeliveryType fromValue(String value) {
        for (DeliveryType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown DeliveryType: " + value);
    }
}
