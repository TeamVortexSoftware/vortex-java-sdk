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
 *
 * Example:
 * <pre>{@code
 * AcceptUser user = new AcceptUser();
 * user.setEmail("user@example.com");
 * user.setName("John Doe");
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AcceptUser {
    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("name")
    private String name;

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

    @Override
    public String toString() {
        return "AcceptUser{" +
                "email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
