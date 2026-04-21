package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Scope/group information for creating invitations - defines what team/org the invitee will join
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateInvitationScope {

    /** Scope type (e.g., "team", "organization", "workspace") */
    @JsonProperty("type")
    private String type;

    /** Your internal scope/group identifier */
    @JsonProperty("groupId")
    private String scope;

    /** Display name shown to invitees (e.g., "Engineering Team") */
    @JsonProperty("name")
    private String name;

    public CreateInvitationScope() {
    }

    /**
     * @param type Group type (e.g., "team", "organization")
     * @param scope Your internal group ID
     * @param name Display name of the group
     */
    public CreateInvitationScope(String type, String scope, String name) {
        this.type = type;
        this.scope = scope;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupId() {
        return scope;
    }

    public void setGroupId(String scope) {
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
