package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Target for creating an invitation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateInvitationTarget {

    @JsonProperty("type")
    private CreateInvitationTargetType type;

    @JsonProperty("value")
    private String value;

    public CreateInvitationTarget() {
    }

    public CreateInvitationTarget(CreateInvitationTargetType type, String value) {
        this.type = type;
        this.value = value;
    }

    public static CreateInvitationTarget email(String email) {
        return new CreateInvitationTarget(CreateInvitationTargetType.EMAIL, email);
    }

    public static CreateInvitationTarget phone(String phone) {
        return new CreateInvitationTarget(CreateInvitationTargetType.PHONE, phone);
    }

    /** Alias for phone (backward compatibility) */
    public static CreateInvitationTarget sms(String phone) {
        return phone(phone);
    }

    public static CreateInvitationTarget internal(String internalId) {
        return new CreateInvitationTarget(CreateInvitationTargetType.INTERNAL, internalId);
    }

    public CreateInvitationTargetType getType() {
        return type;
    }

    public void setType(CreateInvitationTargetType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
