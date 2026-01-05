package com.vortexsoftware.sdk.spring;

import com.vortexsoftware.sdk.VortexClient;
import com.vortexsoftware.sdk.VortexException;
import com.vortexsoftware.sdk.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring Boot controller providing Vortex API endpoints
 *
 * This controller provides the same route structure as the Express SDK,
 * ensuring compatibility with React providers and other frontend frameworks.
 */
@RestController
@RequestMapping("/api/vortex")
public class VortexController {

    private static final Logger logger = LoggerFactory.getLogger(VortexController.class);

    private final VortexClient vortexClient;
    private final VortexConfig config;

    public VortexController(VortexClient vortexClient, VortexConfig config) {
        this.vortexClient = vortexClient;
        this.config = config;
    }

    /**
     * Generate JWT for the authenticated user
     * POST /jwt
     */
    @PostMapping(VortexRoutes.JWT)
    public ResponseEntity<?> generateJWT() {
        try {
            VortexConfig.VortexUser user = config.authenticateUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required"));
            }

            if (!config.authorizeOperation("JWT", user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to generate JWT"));
            }

            // Build User object with adminScopes
            List<String> adminScopes = null;
            if (user.getUserIsAutojoinAdmin() != null && user.getUserIsAutojoinAdmin()) {
                adminScopes = List.of("autojoin");
            }

            User vortexUser = new User(user.getUserId(), user.getUserEmail(), adminScopes);

            logger.debug("Generating JWT for user {}", user.getUserId());

            // Build params map matching Node.js SDK pattern
            Map<String, Object> params = new HashMap<>();
            params.put("user", vortexUser);

            String jwt = vortexClient.generateJwt(params);

            return ResponseEntity.ok(Map.of("jwt", jwt));

        } catch (VortexException e) {
            logger.error("Failed to generate JWT", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate JWT"));
        }
    }

    /**
     * Get invitations by target
     * GET /invitations?targetType=email&amp;targetValue=user@example.com
     */
    @GetMapping(VortexRoutes.INVITATIONS)
    public ResponseEntity<?> getInvitationsByTarget(
            @RequestParam("targetType") String targetType,
            @RequestParam("targetValue") String targetValue) {
        try {
            VortexConfig.VortexUser user = config.authenticateUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required"));
            }

            if (!config.authorizeOperation("GET_INVITATIONS", user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to get invitations"));
            }

            List<InvitationResult> invitations = vortexClient.getInvitationsByTarget(targetType, targetValue);
            return ResponseEntity.ok(Map.of("invitations", invitations));

        } catch (VortexException e) {
            logger.error("Failed to get invitations by target", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get invitations"));
        }
    }

    /**
     * Get specific invitation by ID
     * GET /invitations/{invitationId}
     */
    @GetMapping(VortexRoutes.INVITATION)
    public ResponseEntity<?> getInvitation(@PathVariable("invitationId") String invitationId) {
        try {
            VortexConfig.VortexUser user = config.authenticateUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required"));
            }

            if (!config.authorizeOperation("GET_INVITATION", user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to get invitation"));
            }

            InvitationResult invitation = vortexClient.getInvitation(invitationId);
            return ResponseEntity.ok(invitation);

        } catch (VortexException e) {
            logger.error("Failed to get invitation", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Invitation not found"));
        }
    }

    /**
     * Revoke (delete) invitation
     * DELETE /invitations/{invitationId}
     */
    @DeleteMapping(VortexRoutes.INVITATION)
    public ResponseEntity<?> revokeInvitation(@PathVariable("invitationId") String invitationId) {
        try {
            VortexConfig.VortexUser user = config.authenticateUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required"));
            }

            if (!config.authorizeOperation("REVOKE_INVITATION", user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to revoke invitation"));
            }

            vortexClient.revokeInvitation(invitationId);
            return ResponseEntity.ok(Map.of("success", true));

        } catch (VortexException e) {
            logger.error("Failed to revoke invitation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to revoke invitation"));
        }
    }

    /**
     * Accept invitations
     * POST /invitations/accept
     */
    @PostMapping(VortexRoutes.INVITATIONS_ACCEPT)
    public ResponseEntity<?> acceptInvitations(@RequestBody AcceptInvitationRequest request) {
        try {
            VortexConfig.VortexUser user = config.authenticateUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required"));
            }

            if (!config.authorizeOperation("ACCEPT_INVITATIONS", user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to accept invitations"));
            }

            InvitationResult result = vortexClient.acceptInvitations(
                    request.getInvitationIds(),
                    request.getUser()
            );
            return ResponseEntity.ok(result);

        } catch (VortexException e) {
            logger.error("Failed to accept invitations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to accept invitations"));
        }
    }

    /**
     * Get invitations by group
     * GET /invitations/by-group/{groupType}/{groupId}
     */
    @GetMapping(VortexRoutes.INVITATIONS_BY_GROUP)
    public ResponseEntity<?> getInvitationsByGroup(
            @PathVariable("groupType") String groupType,
            @PathVariable("groupId") String groupId) {
        try {
            VortexConfig.VortexUser user = config.authenticateUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required"));
            }

            if (!config.authorizeOperation("GET_GROUP_INVITATIONS", user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to get group invitations"));
            }

            List<InvitationResult> invitations = vortexClient.getInvitationsByGroup(groupType, groupId);
            return ResponseEntity.ok(Map.of("invitations", invitations));

        } catch (VortexException e) {
            logger.error("Failed to get group invitations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get group invitations"));
        }
    }

    /**
     * Delete invitations by group
     * DELETE /invitations/by-group/{groupType}/{groupId}
     */
    @DeleteMapping(VortexRoutes.INVITATIONS_BY_GROUP)
    public ResponseEntity<?> deleteInvitationsByGroup(
            @PathVariable("groupType") String groupType,
            @PathVariable("groupId") String groupId) {
        try {
            VortexConfig.VortexUser user = config.authenticateUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required"));
            }

            if (!config.authorizeOperation("DELETE_GROUP_INVITATIONS", user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to delete group invitations"));
            }

            vortexClient.deleteInvitationsByGroup(groupType, groupId);
            return ResponseEntity.ok(Map.of("success", true));

        } catch (VortexException e) {
            logger.error("Failed to delete group invitations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete group invitations"));
        }
    }

    /**
     * Reinvite user
     * POST /invitations/{invitationId}/reinvite
     */
    @PostMapping(VortexRoutes.INVITATION_REINVITE)
    public ResponseEntity<?> reinvite(@PathVariable("invitationId") String invitationId) {
        try {
            VortexConfig.VortexUser user = config.authenticateUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required"));
            }

            if (!config.authorizeOperation("REINVITE", user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to reinvite"));
            }

            InvitationResult result = vortexClient.reinvite(invitationId);
            return ResponseEntity.ok(result);

        } catch (VortexException e) {
            logger.error("Failed to reinvite", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reinvite"));
        }
    }
}