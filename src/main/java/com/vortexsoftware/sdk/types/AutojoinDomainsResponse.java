package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Response from autojoin API endpoints
 */
public class AutojoinDomainsResponse {
    @JsonProperty("autojoinDomains")
    private List<AutojoinDomain> autojoinDomains;

    @JsonProperty("invitation")
    private InvitationResult invitation;

    public AutojoinDomainsResponse() {}

    public List<AutojoinDomain> getAutojoinDomains() {
        return autojoinDomains;
    }

    public void setAutojoinDomains(List<AutojoinDomain> autojoinDomains) {
        this.autojoinDomains = autojoinDomains;
    }

    public InvitationResult getInvitation() {
        return invitation;
    }

    public void setInvitation(InvitationResult invitation) {
        this.invitation = invitation;
    }
}
