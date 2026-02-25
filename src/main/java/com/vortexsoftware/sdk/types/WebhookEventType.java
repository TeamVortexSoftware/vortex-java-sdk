package com.vortexsoftware.sdk.types;

/**
 * Webhook event type constants for Vortex state changes.
 */
public final class WebhookEventType {
    private WebhookEventType() {}

    // Invitation Lifecycle
    public static final String INVITATION_CREATED = "invitation.created";
    public static final String INVITATION_ACCEPTED = "invitation.accepted";
    public static final String INVITATION_DEACTIVATED = "invitation.deactivated";
    public static final String INVITATION_EMAIL_DELIVERED = "invitation.email.delivered";
    public static final String INVITATION_EMAIL_BOUNCED = "invitation.email.bounced";
    public static final String INVITATION_EMAIL_OPENED = "invitation.email.opened";
    public static final String INVITATION_LINK_CLICKED = "invitation.link.clicked";
    public static final String INVITATION_REMINDER_SENT = "invitation.reminder.sent";

    // Deployment Lifecycle
    public static final String DEPLOYMENT_CREATED = "deployment.created";
    public static final String DEPLOYMENT_DEACTIVATED = "deployment.deactivated";

    // A/B Testing
    public static final String ABTEST_STARTED = "abtest.started";
    public static final String ABTEST_WINNER_DECLARED = "abtest.winner_declared";

    // Member/Group
    public static final String MEMBER_CREATED = "member.created";
    public static final String GROUP_MEMBER_ADDED = "group.member.added";

    // Email
    public static final String EMAIL_COMPLAINED = "email.complained";
}
