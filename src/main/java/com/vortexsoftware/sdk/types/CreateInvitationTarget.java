package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Target for creating an invitation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateInvitationTarget {

    @JsonProperty("type")
    private String type;

    @JsonProperty("value")
    private String value;

    public CreateInvitationTarget() {
    }

    /**
     * @param type Target type: "email", "phone", or "internal"
     * @param value Target value: email address, phone number, or internal user ID
     */
    public CreateInvitationTarget(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public static CreateInvitationTarget email(String email) {
        return new CreateInvitationTarget("email", email);
    }

    public static CreateInvitationTarget sms(String phone) {
        return new CreateInvitationTarget("phone", phone);
    }

    public static CreateInvitationTarget internal(String internalId) {
        return new CreateInvitationTarget("internal", internalId);
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
}
