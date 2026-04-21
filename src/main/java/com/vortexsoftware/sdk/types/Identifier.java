package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an identifier for a user - used in JWT generation to link user across channels
 */
public class Identifier {
    /** Identifier type: "email", "phone", "username", or custom type */
    @JsonProperty("type")
    private String type;

    /** The identifier value (email address, phone number, etc.) */
    @JsonProperty("value")
    private String value;

    public Identifier() {}

    public Identifier(String type, String value) {
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
        return "Identifier{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
