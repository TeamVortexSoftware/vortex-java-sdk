package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * User data for JWT generation
 *
 * Required fields:
 * - id: User's unique identifier in your system
 * - email: User's email address
 *
 * Optional fields:
 * - adminScopes: List of admin scopes (e.g., ["autoJoin"])
 *
 * Example:
 * <pre>{@code
 * User user = new User("user-123", "user@example.com");
 * user.setAdminScopes(Arrays.asList("autoJoin"));
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @JsonProperty("id")
    private String id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("adminScopes")
    private List<String> adminScopes;

    /**
     * Default constructor for Jackson deserialization
     */
    public User() {}

    /**
     * Create a new User with required fields
     *
     * @param id User's unique identifier
     * @param email User's email address
     */
    public User(String id, String email) {
        this.id = id;
        this.email = email;
    }

    /**
     * Create a new User with all fields
     *
     * @param id User's unique identifier
     * @param email User's email address
     * @param adminScopes List of admin scopes (e.g., ["autoJoin"])
     */
    public User(String id, String email, List<String> adminScopes) {
        this.id = id;
        this.email = email;
        this.adminScopes = adminScopes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getAdminScopes() {
        return adminScopes;
    }

    public void setAdminScopes(List<String> adminScopes) {
        this.adminScopes = adminScopes;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", adminScopes=" + adminScopes +
                '}';
    }
}
