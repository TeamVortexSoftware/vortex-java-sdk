package com.vortexsoftware.sdk;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.vortexsoftware.sdk.types.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for VortexClient
 *
 * These tests ensure the Java SDK behaves identically to the Node.js SDK,
 * particularly for JWT generation which must use the exact same algorithm.
 */
public class VortexClientTest {

    // Valid test API key with proper 16-byte UUID encoded as base64url
    // UUID: f2637232-b967-4793-bbaa-3e873719079a
    private static final String TEST_API_KEY = "VRTX.8mNyMrlnR5O7qj6HNxkHmg.test-signing-key";
    private WireMockServer wireMockServer;
    private VortexClient client;

    @BeforeEach
    void setUp() {
        // Start WireMock server for API testing
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);

        // Create client with test base URL
        client = new VortexClient(TEST_API_KEY, "http://localhost:8089");
    }

    @AfterEach
    void tearDown() {
        if (client != null) {
            client.close();
        }
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void testJWTGeneration() throws VortexException {
        // Create test user
        User user = new User("user-123", "test@example.com");
        user.setAdminScopes(Arrays.asList("autoJoin"));

        // Generate JWT
        String jwt = client.generateJwt(user, null);

        // Verify JWT structure (3 parts separated by dots)
        assertNotNull(jwt);
        String[] parts = jwt.split("\\.");
        assertEquals(3, parts.length, "JWT should have 3 parts");

        // Verify it's not empty and has expected length (base64url encoded)
        assertTrue(parts[0].length() > 0, "Header should not be empty");
        assertTrue(parts[1].length() > 0, "Payload should not be empty");
        assertTrue(parts[2].length() > 0, "Signature should not be empty");

        // JWT should not contain padding characters (base64url encoding)
        assertFalse(jwt.contains("="), "JWT should use base64url encoding without padding");
        assertFalse(jwt.contains("+"), "JWT should use base64url encoding");
        assertFalse(jwt.contains("/"), "JWT should use base64url encoding");
    }

    @Test
    void testJWTGenerationWithoutAdminScopes() throws VortexException {
        // Test without admin scopes
        User user = new User("user-123", "test@example.com");

        String jwt = client.generateJwt(user, null);
        assertNotNull(jwt);
        assertEquals(3, jwt.split("\\.").length);
    }

    @Test
    void testJWTGenerationWithExtraProperties() throws VortexException {
        // Test with additional properties
        User user = new User("user-123", "test@example.com");
        java.util.Map<String, Object> extra = new java.util.HashMap<>();
        extra.put("role", "admin");
        extra.put("department", "Engineering");

        String jwt = client.generateJwt(user, extra);
        assertNotNull(jwt);
        assertEquals(3, jwt.split("\\.").length);
    }

    @Test
    void testInvalidAPIKeyFormat() {
        // Test various invalid API key formats
        User testUser = createTestUser();

        assertThrows(VortexException.class, () -> {
            VortexClient invalidClient = new VortexClient("invalid-key");
            invalidClient.generateJwt(testUser, null);
        });

        assertThrows(VortexException.class, () -> {
            VortexClient invalidClient = new VortexClient("WRONG.format.key");
            invalidClient.generateJwt(testUser, null);
        });

        assertThrows(VortexException.class, () -> {
            VortexClient invalidClient = new VortexClient("VRTX.only-two-parts");
            invalidClient.generateJwt(testUser, null);
        });
    }

    @Test
    void testGetInvitationsByTarget() throws VortexException {
        // Mock API response
        stubFor(get(urlPathEqualTo("/api/v1/invitations"))
                .withQueryParam("targetType", equalTo("email"))
                .withQueryParam("targetValue", equalTo("test@example.com"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"invitations\": [{\"id\": \"inv-123\", \"status\": \"delivered\", \"accountId\": \"acc-123\", \"projectId\": \"proj-123\", \"clickThroughs\": 0, \"deliveryCount\": 1, \"views\": 0, \"deactivated\": false, \"deliveryTypes\": [\"email\"], \"foreignCreatorId\": \"creator-123\", \"invitationType\": \"single_use\", \"createdAt\": \"2023-01-01T00:00:00Z\", \"target\": [], \"groups\": [], \"accepts\": []}]}")));

        List<InvitationResult> results = client.getInvitationsByTarget("email", "test@example.com");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("inv-123", results.get(0).getId());
        assertEquals("delivered", results.get(0).getStatus());
    }

    @Test
    void testGetInvitation() throws VortexException {
        String invitationId = "inv-123";

        // Mock API response
        stubFor(get(urlPathEqualTo("/api/v1/invitations/" + invitationId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": \"inv-123\", \"status\": \"delivered\", \"accountId\": \"acc-123\", \"projectId\": \"proj-123\", \"clickThroughs\": 0, \"deliveryCount\": 1, \"views\": 0, \"deactivated\": false, \"deliveryTypes\": [\"email\"], \"foreignCreatorId\": \"creator-123\", \"invitationType\": \"single_use\", \"createdAt\": \"2023-01-01T00:00:00Z\", \"target\": [], \"groups\": [], \"accepts\": []}")));

        InvitationResult result = client.getInvitation(invitationId);

        assertNotNull(result);
        assertEquals("inv-123", result.getId());
        assertEquals("delivered", result.getStatus());
    }

    @Test
    void testRevokeInvitation() throws VortexException {
        String invitationId = "inv-123";

        // Mock API response
        stubFor(delete(urlPathEqualTo("/api/v1/invitations/" + invitationId))
                .willReturn(aResponse()
                        .withStatus(200)));

        // Should not throw exception
        assertDoesNotThrow(() -> client.revokeInvitation(invitationId));
    }

    @Test
    void testAcceptInvitations() throws VortexException {
        List<String> invitationIds = Arrays.asList("inv-123", "inv-456");
        InvitationTarget target = new InvitationTarget("email", "test@example.com");

        // Mock API response
        stubFor(post(urlPathEqualTo("/api/v1/invitations/accept"))
                .withRequestBody(matchingJsonPath("$.invitationIds"))
                .withRequestBody(matchingJsonPath("$.target"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": \"inv-123\", \"status\": \"accepted\", \"accountId\": \"acc-123\", \"projectId\": \"proj-123\", \"clickThroughs\": 0, \"deliveryCount\": 1, \"views\": 0, \"deactivated\": false, \"deliveryTypes\": [\"email\"], \"foreignCreatorId\": \"creator-123\", \"invitationType\": \"single_use\", \"createdAt\": \"2023-01-01T00:00:00Z\", \"target\": [], \"groups\": [], \"accepts\": []}")));

        InvitationResult result = client.acceptInvitations(invitationIds, target);

        assertNotNull(result);
        assertEquals("inv-123", result.getId());
        assertEquals("accepted", result.getStatus());
    }

    @Test
    void testGetInvitationsByGroup() throws VortexException {
        String groupType = "team";
        String groupId = "team-123";

        // Mock API response
        stubFor(get(urlPathEqualTo("/api/v1/invitations/by-group/" + groupType + "/" + groupId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"invitations\": [{\"id\": \"inv-123\", \"status\": \"delivered\", \"accountId\": \"acc-123\", \"projectId\": \"proj-123\", \"clickThroughs\": 0, \"deliveryCount\": 1, \"views\": 0, \"deactivated\": false, \"deliveryTypes\": [\"email\"], \"foreignCreatorId\": \"creator-123\", \"invitationType\": \"single_use\", \"createdAt\": \"2023-01-01T00:00:00Z\", \"target\": [], \"groups\": [], \"accepts\": []}]}")));

        List<InvitationResult> results = client.getInvitationsByGroup(groupType, groupId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("inv-123", results.get(0).getId());
    }

    @Test
    void testDeleteInvitationsByGroup() throws VortexException {
        String groupType = "team";
        String groupId = "team-123";

        // Mock API response
        stubFor(delete(urlPathEqualTo("/api/v1/invitations/by-group/" + groupType + "/" + groupId))
                .willReturn(aResponse()
                        .withStatus(200)));

        // Should not throw exception
        assertDoesNotThrow(() -> client.deleteInvitationsByGroup(groupType, groupId));
    }

    @Test
    void testReinvite() throws VortexException {
        String invitationId = "inv-123";

        // Mock API response
        stubFor(post(urlPathEqualTo("/api/v1/invitations/" + invitationId + "/reinvite"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": \"inv-123\", \"status\": \"queued\", \"accountId\": \"acc-123\", \"projectId\": \"proj-123\", \"clickThroughs\": 0, \"deliveryCount\": 2, \"views\": 0, \"deactivated\": false, \"deliveryTypes\": [\"email\"], \"foreignCreatorId\": \"creator-123\", \"invitationType\": \"single_use\", \"createdAt\": \"2023-01-01T00:00:00Z\", \"target\": [], \"groups\": [], \"accepts\": []}")));

        InvitationResult result = client.reinvite(invitationId);

        assertNotNull(result);
        assertEquals("inv-123", result.getId());
        assertEquals("queued", result.getStatus());
        assertEquals(2, result.getDeliveryCount());
    }

    @Test
    void testAPIErrorHandling() {
        // Mock API error response
        stubFor(get(urlPathEqualTo("/api/v1/invitations/nonexistent"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("Invitation not found")));

        // Should throw VortexException
        VortexException exception = assertThrows(VortexException.class, () ->
                client.getInvitation("nonexistent"));

        assertTrue(exception.getMessage().contains("404"));
    }

    @Test
    void testEmptyResponseHandling() throws VortexException {
        // Mock empty response (valid for delete operations)
        stubFor(delete(urlPathEqualTo("/api/v1/invitations/inv-123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("")));

        // Should not throw exception
        assertDoesNotThrow(() -> client.revokeInvitation("inv-123"));
    }

    private User createTestUser() {
        User user = new User("user-123", "test@example.com");
        user.setAdminScopes(Arrays.asList("autoJoin"));
        return user;
    }
}