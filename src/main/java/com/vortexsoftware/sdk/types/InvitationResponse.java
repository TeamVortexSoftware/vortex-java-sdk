package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Response wrapper for invitation API calls that return multiple invitations
 */
public class InvitationResponse {
    @JsonProperty("invitations")
    private List<InvitationResult> invitations;

    public InvitationResponse() {}

    public InvitationResponse(List<InvitationResult> invitations) {
        this.invitations = invitations;
    }

    public List<InvitationResult> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<InvitationResult> invitations) {
        this.invitations = invitations;
    }

    @Override
    public String toString() {
        return "InvitationResponse{" +
                "invitations=" + invitations +
                '}';
    }
}