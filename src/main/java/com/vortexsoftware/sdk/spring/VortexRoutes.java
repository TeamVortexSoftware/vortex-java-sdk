package com.vortexsoftware.sdk.spring;

/**
 * Constants defining the Vortex API route structure
 *
 * These routes match exactly with the Express SDK and React provider
 * to ensure complete compatibility across different backend implementations.
 */
public final class VortexRoutes {

    /**
     * Base routes that match the React provider's API calls
     */
    public static final String JWT = "/jwt";
    public static final String INVITATIONS = "/invitations";
    public static final String INVITATION = "/invitations/{invitationId}";
    public static final String INVITATIONS_ACCEPT = "/invitations/accept";
    public static final String INVITATIONS_BY_GROUP = "/invitations/by-group/{groupType}/{groupId}";
    public static final String INVITATION_REINVITE = "/invitations/{invitationId}/reinvite";

    private VortexRoutes() {
        // Utility class - no instantiation
    }

    /**
     * Create the full API path based on base URL
     */
    public static String createVortexApiPath(String baseUrl, String route) {
        String cleanBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return cleanBaseUrl + route;
    }
}