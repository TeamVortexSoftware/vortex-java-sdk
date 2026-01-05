package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Request payload for accepting invitations
 * Supports both new User format (preferred) and legacy target format (deprecated)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AcceptInvitationRequest {
    @JsonProperty("invitationIds")
    private List<String> invitationIds;

    @JsonProperty("user")
    private AcceptUser user;

    public AcceptInvitationRequest() {}

    /**
     * Create request with new User format (preferred)
     */
    public AcceptInvitationRequest(List<String> invitationIds, AcceptUser user) {
        this.invitationIds = invitationIds;
        this.user = user;
    }

    public List<String> getInvitationIds() {
        return invitationIds;
    }

    public void setInvitationIds(List<String> invitationIds) {
        this.invitationIds = invitationIds;
    }

    public AcceptUser getUser() {
        return user;
    }

    public void setUser(AcceptUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "AcceptInvitationRequest{" +
                "invitationIds=" + invitationIds +
                ", user=" + user +
                '}';
    }
}