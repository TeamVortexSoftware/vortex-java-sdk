package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Response from syncing an internal invitation action
 */
public class SyncInternalInvitationResponse {

    @JsonProperty("processed")
    private int processed;

    @JsonProperty("invitationIds")
    private List<String> invitationIds;

    public SyncInternalInvitationResponse() {}

    public int getProcessed() { return processed; }
    public void setProcessed(int processed) { this.processed = processed; }

    public List<String> getInvitationIds() { return invitationIds; }
    public void setInvitationIds(List<String> invitationIds) { this.invitationIds = invitationIds; }
}
