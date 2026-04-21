package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User data for accepting invitations
 *
 * Required fields: At least one of email or phone must be provided
 *
 * Optional fields:
 * - email: User's email address
 * - phone: User's phone number
 * - name: User's display name
 * - isExisting: Whether user was already registered before accepting
 *
 * Example:
 * <pre>{@code
 * AcceptUser user = new AcceptUser();
 * user.setEmail("user@example.com");
 * user.setName("John Doe");
 * user.setIsExisting(false); // New user signup
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AcceptUser {
    /**
     * Email address of the user accepting the invitation.
     * At least one of email or phone is required.
     */
    @JsonProperty("email")
    private String email;

    /**
     * Phone number of the user accepting the invitation.
     * At least one of email or phone is required. Include country code (e.g., "+1555123456").
     */
    @JsonProperty("phone")
    private String phone;

    /**
     * Display name of the accepting user.
     * Used in notifications to the inviter (e.g., "John Doe accepted your invitation").
     */
    @JsonProperty("name")
    private String name;

    /**
     * Whether the accepting user was already registered in your system.
     * Set to true for existing users, false for new signups, null if unknown.
     * Used for analytics to track new vs existing user conversion rates.
     */
    @JsonProperty("isExisting")
    private Boolean isExisting;

    /**
     * Default constructor for Jackson deserialization
     */
    public AcceptUser() {}

    /**
     * Create a new AcceptUser with email
     *
     * @param email User's email address
     */
    public AcceptUser(String email) {
        this.email = email;
    }

    /**
     * Create a new AcceptUser with email and name
     *
     * @param email User's email address
     * @param name User's display name
     */
    public AcceptUser(String email, String name) {
        this.email = email;
        this.name = name;
    }

    /**
     * Create a new AcceptUser with all fields
     *
     * @param email User's email address
     * @param phone User's phone number
     * @param name User's display name
     */
    public AcceptUser(String email, String phone, String name) {
        this.email = email;
        this.phone = phone;
        this.name = name;
    }

    /**
     * Create a new AcceptUser with all fields including isExisting
     *
     * @param email User's email address
     * @param phone User's phone number
     * @param name User's display name
     * @param isExisting Whether user was already registered
     */
    public AcceptUser(String email, String phone, String name, Boolean isExisting) {
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.isExisting = isExisting;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsExisting() {
        return isExisting;
    }

    public void setIsExisting(Boolean isExisting) {
        this.isExisting = isExisting;
    }

    @Override
    public String toString() {
        return "AcceptUser{" +
                "email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", isExisting=" + isExisting +
                '}';
    }
}
