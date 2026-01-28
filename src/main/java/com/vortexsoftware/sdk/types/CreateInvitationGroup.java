package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Group information for creating invitations.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateInvitationGroup {

    @JsonProperty("type")
    private String type;

    @JsonProperty("groupId")
    private String groupId;

    @JsonProperty("name")
    private String name;

    public CreateInvitationGroup() {
    }

    /**
     * @param type Group type (e.g., "team", "organization")
     * @param groupId Your internal group ID
     * @param name Display name of the group
     */
    public CreateInvitationGroup(String type, String groupId, String name) {
        this.type = type;
        this.groupId = groupId;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
