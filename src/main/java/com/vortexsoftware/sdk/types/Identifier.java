package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an identifier for a user (email, sms, etc.)
 * Used for JWT generation.
 */
public class Identifier {
    @JsonProperty("type")
    private String type;

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
