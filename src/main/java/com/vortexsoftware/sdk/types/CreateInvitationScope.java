package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Group information for creating invitations.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateInvitationScope {

    @JsonProperty("type")
    private String type;

    @JsonProperty("groupId")
    private String scope;

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
