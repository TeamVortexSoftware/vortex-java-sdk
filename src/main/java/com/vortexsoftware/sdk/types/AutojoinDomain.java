package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an autojoin domain configuration
 */
public class AutojoinDomain {
    @JsonProperty("id")
    private String id;

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
