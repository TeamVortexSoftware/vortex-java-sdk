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

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("userAvatarUrl")
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
     * @param userName Optional: Display name of the inviter
     * @param userAvatarUrl Optional: Avatar URL of the inviter
     */
    public Inviter(String userId, String userEmail, String userName, String userAvatarUrl) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userAvatarUrl = userAvatarUrl;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }
}
