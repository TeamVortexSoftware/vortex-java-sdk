package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Request payload for accepting invitations
 */
public class AcceptInvitationRequest {
    @JsonProperty("invitationIds")
    private List<String> invitationIds;

    @JsonProperty("target")
    private InvitationTarget target;

    public AcceptInvitationRequest() {}

    public AcceptInvitationRequest(List<String> invitationIds, InvitationTarget target) {
        this.invitationIds = invitationIds;
        this.target = target;
    }

    public List<String> getInvitationIds() {
        return invitationIds;
    }

    public void setInvitationIds(List<String> invitationIds) {
        this.invitationIds = invitationIds;
    }

    public InvitationTarget getTarget() {
        return target;
    }

    public void setTarget(InvitationTarget target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "AcceptInvitationRequest{" +
                "invitationIds=" + invitationIds +
                ", target=" + target +
                '}';
    }
}