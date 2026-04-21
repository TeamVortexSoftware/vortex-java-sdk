package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an autojoin domain - users with matching email domains automatically join the scope
 */
public class AutojoinDomain {
    /** Unique identifier for this autojoin configuration */
    @JsonProperty("id")
    private String id;

    /** Email domain that triggers autojoin (e.g., "acme.com") */
    @JsonProperty("domain")
    private String domain;

    public AutojoinDomain() {}

    public AutojoinDomain(String id, String domain) {
        this.id = id;
        this.domain = domain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
