package com.vortexsoftware.sdk.types;

/**
 * @deprecated Use {@link InvitationScope} instead. This class is kept for backward compatibility.
 */
@Deprecated
public class InvitationGroup extends InvitationScope {
    public InvitationGroup() {
        super();
    }

    public InvitationGroup(String id, String accountId, String scope, String type, String name, String createdAt) {
        super(id, accountId, scope, type, name, createdAt);
    }
}
