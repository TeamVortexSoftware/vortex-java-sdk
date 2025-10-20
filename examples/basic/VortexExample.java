package com.vortexsoftware.sdk.examples;

import com.vortexsoftware.sdk.VortexClient;
import com.vortexsoftware.sdk.VortexException;
import com.vortexsoftware.sdk.types.*;

import java.util.Arrays;
import java.util.List;

/**
 * Basic example showing how to use the Vortex Java SDK
 *
 * This example demonstrates:
 * - Creating a VortexClient
 * - Generating JWTs
 * - Managing invitations
 * - Group operations
 */
public class VortexExample {

    private static final String API_KEY = "VRTX.your-encoded-id.your-signing-key";

    public static void main(String[] args) {
        // Create the Vortex client
        try (VortexClient client = new VortexClient(API_KEY)) {

            System.out.println("🚀 Vortex Java SDK Example");
            System.out.println("==========================");

            // Example 1: Generate a JWT
            demonstrateJWTGeneration(client);

            // Example 2: Get invitations by target
            demonstrateGetInvitations(client);

            // Example 3: Invitation operations
            demonstrateInvitationOperations(client);

            // Example 4: Group operations
            demonstrateGroupOperations(client);

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void demonstrateJWTGeneration(VortexClient client) throws VortexException {
        System.out.println("\n🔐 JWT Generation Example");
        System.out.println("--------------------------");

        // Create user identifiers
        List<InvitationTarget> identifiers = Arrays.asList(
                new InvitationTarget("email", "admin@example.com"),
                new InvitationTarget("sms", "+1234567890")
        );

        // Create user groups
        List<InvitationGroup> groups = Arrays.asList(
                new InvitationGroup("team-1", "team", "Engineering"),
                new InvitationGroup("org-1", "organization", "Acme Corp")
        );

        // Create JWT payload
        JWTPayload payload = new JWTPayload("user-123", identifiers, groups, "admin");

        // Generate JWT
        String jwt = client.generateJWT(payload);
        System.out.println("✅ Generated JWT: " + jwt.substring(0, Math.min(jwt.length(), 50)) + "...");
        System.out.println("   JWT Length: " + jwt.length() + " characters");
        System.out.println("   JWT Parts: " + jwt.split("\\.").length);
    }

    private static void demonstrateGetInvitations(VortexClient client) throws VortexException {
        System.out.println("\n📧 Get Invitations Example");
        System.out.println("---------------------------");

        try {
            // Get invitations by email
            List<InvitationResult> emailInvitations = client.getInvitationsByTarget(
                    "email",
                    "user@example.com"
            );
            System.out.println("✅ Found " + emailInvitations.size() + " invitations for user@example.com");

            // Get invitations by username
            List<InvitationResult> usernameInvitations = client.getInvitationsByTarget(
                    "username",
                    "john_doe"
            );
            System.out.println("✅ Found " + usernameInvitations.size() + " invitations for username: john_doe");

            // Print details of first invitation if available
            if (!emailInvitations.isEmpty()) {
                InvitationResult first = emailInvitations.get(0);
                System.out.println("   First invitation ID: " + first.getId());
                System.out.println("   Status: " + first.getStatus());
                System.out.println("   Delivery Count: " + first.getDeliveryCount());
                System.out.println("   Views: " + first.getViews());
            }

        } catch (VortexException e) {
            System.out.println("ℹ️  No invitations found or API error (this is normal for demo): " + e.getMessage());
        }
    }

    private static void demonstrateInvitationOperations(VortexClient client) throws VortexException {
        System.out.println("\n🎯 Invitation Operations Example");
        System.out.println("---------------------------------");

        try {
            // Try to get a specific invitation
            InvitationResult invitation = client.getInvitation("test-invitation-id");
            System.out.println("✅ Retrieved invitation: " + invitation.getId());

            // Try to reinvite
            InvitationResult reinvited = client.reinvite("test-invitation-id");
            System.out.println("✅ Reinvited successfully, new status: " + reinvited.getStatus());

            // Try to accept invitations
            InvitationTarget target = new InvitationTarget("email", "newuser@example.com");
            InvitationResult accepted = client.acceptInvitations(
                    Arrays.asList("inv-1", "inv-2"),
                    target
            );
            System.out.println("✅ Accepted invitations, result: " + accepted.getId());

            // Try to revoke an invitation
            client.revokeInvitation("test-invitation-id");
            System.out.println("✅ Revoked invitation successfully");

        } catch (VortexException e) {
            System.out.println("ℹ️  Invitation operation failed (this is normal for demo): " + e.getMessage());
        }
    }

    private static void demonstrateGroupOperations(VortexClient client) throws VortexException {
        System.out.println("\n👥 Group Operations Example");
        System.out.println("---------------------------");

        try {
            // Get invitations for a specific group
            List<InvitationResult> groupInvitations = client.getInvitationsByGroup(
                    "team",
                    "engineering-team"
            );
            System.out.println("✅ Found " + groupInvitations.size() + " invitations for engineering team");

            // Get invitations for an organization
            List<InvitationResult> orgInvitations = client.getInvitationsByGroup(
                    "organization",
                    "acme-corp"
            );
            System.out.println("✅ Found " + orgInvitations.size() + " invitations for Acme Corp");

            // Delete all invitations for a group (be careful!)
            // client.deleteInvitationsByGroup("team", "old-team");
            // System.out.println("✅ Deleted all invitations for old team");

        } catch (VortexException e) {
            System.out.println("ℹ️  Group operation failed (this is normal for demo): " + e.getMessage());
        }
    }
}

/**
 * Spring Boot example showing how to integrate with web applications
 */
class SpringBootExample {

    // This would be in a real Spring Boot application

    /*
    @RestController
    @RequestMapping("/api/vortex")
    public class VortexDemoController {

        private final VortexClient vortexClient;

        public VortexDemoController(VortexClient vortexClient) {
            this.vortexClient = vortexClient;
        }

        @PostMapping("/jwt")
        public ResponseEntity<Map<String, String>> generateJWT(@RequestBody JWTRequest request) {
            try {
                JWTPayload payload = new JWTPayload(
                    request.getUserId(),
                    request.getIdentifiers(),
                    request.getGroups(),
                    request.getRole()
                );

                String jwt = vortexClient.generateJWT(payload);
                return ResponseEntity.ok(Map.of("jwt", jwt));

            } catch (VortexException e) {
                logger.error("Failed to generate JWT", e);
                return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to generate JWT"));
            }
        }

        @GetMapping("/invitations")
        public ResponseEntity<?> getInvitations(
                @RequestParam String targetType,
                @RequestParam String targetValue) {
            try {
                List<InvitationResult> invitations = vortexClient.getInvitationsByTarget(
                    targetType,
                    targetValue
                );
                return ResponseEntity.ok(Map.of("invitations", invitations));

            } catch (VortexException e) {
                logger.error("Failed to get invitations", e);
                return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to get invitations"));
            }
        }
    }

    // Configuration class
    @Configuration
    public class VortexConfiguration {

        @Value("${vortex.api.key}")
        private String apiKey;

        @Bean
        public VortexClient vortexClient() {
            return new VortexClient(apiKey);
        }
    }
    */
}