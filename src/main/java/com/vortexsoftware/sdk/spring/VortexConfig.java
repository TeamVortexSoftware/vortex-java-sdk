package com.vortexsoftware.sdk.spring;

import com.vortexsoftware.sdk.types.InvitationGroup;
import com.vortexsoftware.sdk.types.InvitationTarget;

import java.util.List;

/**
 * Configuration interface for Vortex Spring integration
 *
 * This interface allows applications to define how users are authenticated
 * and authorized for Vortex operations, matching the pattern from Express SDK.
 */
public interface VortexConfig {

    /**
     * Convert the current request context to a Vortex user
     *
     * This should extract user information from the current HTTP request
     * and return it in the format expected by Vortex JWT generation.
     *
     * @return VortexUser representation, or null if not authenticated
     */
    VortexUser authenticateUser();

    /**
     * Check if the current user can perform a specific operation
     *
     * @param operation The operation being performed (JWT, GET_INVITATIONS, etc.)
     * @param user The authenticated user
     * @return true if authorized, false otherwise
     */
    boolean authorizeOperation(String operation, VortexUser user);

    /**
     * Represents a user in the Vortex system
     */
    class VortexUser {
        private String userId;
        private List<InvitationTarget> identifiers;
        private List<InvitationGroup> groups;
        private String role;

        public VortexUser() {}

        public VortexUser(String userId, List<InvitationTarget> identifiers, List<InvitationGroup> groups, String role) {
            this.userId = userId;
            this.identifiers = identifiers;
            this.groups = groups;
            this.role = role;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public List<InvitationTarget> getIdentifiers() {
            return identifiers;
        }

        public void setIdentifiers(List<InvitationTarget> identifiers) {
            this.identifiers = identifiers;
        }

        public List<InvitationGroup> getGroups() {
            return groups;
        }

        public void setGroups(List<InvitationGroup> groups) {
            this.groups = groups;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        @Override
        public String toString() {
            return "VortexUser{" +
                    "userId='" + userId + '\'' +
                    ", identifiers=" + identifiers +
                    ", groups=" + groups +
                    ", role='" + role + '\'' +
                    '}';
        }
    }
}