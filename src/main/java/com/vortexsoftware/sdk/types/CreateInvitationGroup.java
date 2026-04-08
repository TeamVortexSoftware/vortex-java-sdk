package com.vortexsoftware.sdk.types;

/**
 * @deprecated Use {@link CreateInvitationScope} instead. This class is kept for backward compatibility.
 */
@Deprecated
public class CreateInvitationGroup extends CreateInvitationScope {
    public CreateInvitationGroup() {
        super();
    }

    public CreateInvitationGroup(String type, String scope, String name) {
        super(type, scope, name);
    }
}
