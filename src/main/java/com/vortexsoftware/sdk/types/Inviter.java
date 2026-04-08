package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Information about the user creating the invitation (the inviter).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Inviter {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("userEmail")
    private String userEmail;

    /** Display name of the inviter (preferred) */
    @JsonProperty("name")
    private String name;

    /** Avatar URL of the inviter (preferred) */
    @JsonProperty("avatarUrl")
    private String avatarUrl;

    /** @deprecated Use name instead */
    @JsonProperty("userName")
    @Deprecated
    private String userName;

    /** @deprecated Use avatarUrl instead */
    @JsonProperty("userAvatarUrl")
    @Deprecated
    private String userAvatarUrl;

    public Inviter() {
    }

    /**
     * @param userId Required: Your internal user ID for the inviter
     */
    public Inviter(String userId) {
        this.userId = userId;
    }

    /**
     * @param userId Required: Your internal user ID for the inviter
     * @param userEmail Optional: Email of the inviter
     * @param name Optional: Display name of the inviter
     * @param avatarUrl Optional: Avatar URL of the inviter
     */
    public Inviter(String userId, String userEmail, String name, String avatarUrl) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    /** @deprecated Use getName() instead */
    @Deprecated
    public String getUserName() {
        return userName;
    }

    /** @deprecated Use setName() instead */
    @Deprecated
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /** @deprecated Use getAvatarUrl() instead */
    @Deprecated
    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    /** @deprecated Use setAvatarUrl() instead */
    @Deprecated
    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }
}
