package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for syncing an internal invitation action
 */
public class SyncInternalInvitationRequest {

    @JsonProperty("creatorId")
    private String creatorId;

    @JsonProperty("targetValue")
    private String targetValue;

    @JsonProperty("action")
    private String action;

    @JsonProperty("componentId")
    private String componentId;

    public SyncInternalInvitationRequest() {}

    public SyncInternalInvitationRequest(String creatorId, String targetValue, String action, String componentId) {
        this.creatorId = creatorId;
        this.targetValue = targetValue;
        this.action = action;
        this.componentId = componentId;
    }

    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }

    public String getTargetValue() { return targetValue; }
    public void setTargetValue(String targetValue) { this.targetValue = targetValue; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getComponentId() { return componentId; }
    public void setComponentId(String componentId) { this.componentId = componentId; }
}
