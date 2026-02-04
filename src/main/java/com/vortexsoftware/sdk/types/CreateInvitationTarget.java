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

    /** Display name of the person being invited */
    @JsonProperty("name")
    private String name;

    /** Avatar URL for the person being invited (for display in invitation lists) */
    @JsonProperty("avatarUrl")
    private String avatarUrl;

    public CreateInvitationTarget() {
    }

    public CreateInvitationTarget(CreateInvitationTargetType type, String value) {
        this.type = type;
        this.value = value;
    }

    public CreateInvitationTarget(CreateInvitationTargetType type, String value, String name, String avatarUrl) {
        this.type = type;
        this.value = value;
        this.name = name;
        this.avatarUrl = avatarUrl;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
