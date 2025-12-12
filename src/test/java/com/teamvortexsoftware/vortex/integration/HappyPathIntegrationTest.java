package com.teamvortexsoftware.vortex;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import com.vortexsoftware.sdk.VortexClient;
import com.vortexsoftware.sdk.types.InvitationResult;
import com.vortexsoftware.sdk.types.InvitationTarget;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "TEST_INTEGRATION_SDKS_VORTEX_API_KEY", matches = ".+")
public class HappyPathIntegrationTest {

    private String apiKey;
    private String clientApiUrl;
    private String publicApiUrl;
    private String sessionId;
    private VortexClient publicClient;
    private String invitationId;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        // Validate required environment variables
        apiKey = System.getenv("TEST_INTEGRATION_SDKS_VORTEX_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Missing required environment variable: TEST_INTEGRATION_SDKS_VORTEX_API_KEY");
        }

        clientApiUrl = System.getenv("TEST_INTEGRATION_SDKS_VORTEX_CLIENT_API_URL");
        if (clientApiUrl == null || clientApiUrl.isEmpty()) {
            throw new IllegalStateException("Missing required environment variable: TEST_INTEGRATION_SDKS_VORTEX_CLIENT_API_URL");
        }

        publicApiUrl = System.getenv("TEST_INTEGRATION_SDKS_VORTEX_PUBLIC_API_URL");
        if (publicApiUrl == null || publicApiUrl.isEmpty()) {
            throw new IllegalStateException("Missing required environment variable: TEST_INTEGRATION_SDKS_VORTEX_PUBLIC_API_URL");
        }

        sessionId = System.getenv("TEST_INTEGRATION_SDKS_VORTEX_SESSION_ID");
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalStateException("Missing required environment variable: TEST_INTEGRATION_SDKS_VORTEX_SESSION_ID");
        }

        publicClient = new VortexClient(apiKey, publicApiUrl);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testFullInvitationFlow() throws Exception {
        System.out.println("\n--- Starting Java SDK Integration Test ---");

        long timestamp = System.currentTimeMillis() / 1000;

        String userEmail = System.getenv("TEST_INTEGRATION_SDKS_USER_EMAIL");
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalStateException("Missing required environment variable: TEST_INTEGRATION_SDKS_USER_EMAIL");
        }
        userEmail = userEmail.replace("{timestamp}", String.valueOf(timestamp));

        String componentId = System.getenv("TEST_INTEGRATION_SDKS_VORTEX_COMPONENT_ID");
        if (componentId == null || componentId.isEmpty()) {
            throw new IllegalStateException("Missing required environment variable: TEST_INTEGRATION_SDKS_VORTEX_COMPONENT_ID");
        }

        String groupType = System.getenv("TEST_INTEGRATION_SDKS_GROUP_TYPE");
        if (groupType == null || groupType.isEmpty()) {
            throw new IllegalStateException("Missing required environment variable: TEST_INTEGRATION_SDKS_GROUP_TYPE");
        }

        // TEST_INTEGRATION_SDKS_GROUP_ID is dynamic - generated from timestamp
        String groupId = "test-group-" + timestamp;

        String groupName = System.getenv("TEST_INTEGRATION_SDKS_GROUP_NAME");
        if (groupName == null || groupName.isEmpty()) {
            throw new IllegalStateException("Missing required environment variable: TEST_INTEGRATION_SDKS_GROUP_NAME");
        }

        // Step 1: Create invitation
        System.out.println("Step 1: Creating invitation...");
        invitationId = createInvitation(userEmail, componentId, groupType, groupId, groupName);
        assertNotNull(invitationId, "Failed to create invitation");
        System.out.println("✓ Created invitation: " + invitationId);

        // Step 2a: Get invitation by ID
        System.out.println("Step 2a: Getting invitation by ID...");
        InvitationResult invitation = publicClient.getInvitation(invitationId);
        assertNotNull(invitation, "Failed to get invitation by ID");
        assertEquals(invitationId, invitation.getId(), "Retrieved invitation ID does not match expected ID");
        System.out.println("✓ Retrieved invitation by ID successfully");

        // Step 2b: Get invitations by target
        System.out.println("Step 2b: Getting invitations by target...");
        List<InvitationResult> invitations = publicClient.getInvitationsByTarget("email", userEmail);
        assertFalse(invitations.isEmpty(), "No invitations found");
        // Verify the single invitation is in the list
        boolean foundInList = invitations.stream().anyMatch(inv -> inv.getId().equals(invitationId));
        assertTrue(foundInList, "Invitation not found in list returned by target");
        System.out.println("✓ Retrieved invitations by target successfully and verified invitation is in list");

        // Step 3: Accept invitation
        System.out.println("Step 3: Accepting invitation...");
        InvitationTarget target = new InvitationTarget("email", userEmail);

        List<InvitationResult> results = publicClient.acceptInvitations(List.of(invitationId), target);
        assertNotNull(results, "Failed to accept invitation");
        assertFalse(results.isEmpty(), "No results returned from accept invitation");
        System.out.println("✓ Accepted invitation successfully");

        System.out.println("--- Java SDK Integration Test Complete ---\n");
    }

    private String createInvitation(String userEmail, String componentId,
                                   String groupType, String groupId, String groupName) throws Exception {
        // Generate JWT for authentication
        VortexClient jwtClient = new VortexClient(apiKey, clientApiUrl);
        // Extract timestamp from userEmail to keep it consistent
        String timestamp = userEmail.contains("sdktest") ? userEmail.split("sdktest")[1].split("@")[0] : String.valueOf(System.currentTimeMillis() / 1000);
        String userId = System.getenv("TEST_INTEGRATION_SDKS_USER_ID");
        if (userId == null || userId.isEmpty()) {
            throw new IllegalStateException("Missing required environment variable: TEST_INTEGRATION_SDKS_USER_ID");
        }
        userId = userId.replace("{timestamp}", timestamp);

        Map<String, Object> jwtParams = new HashMap<>();
        jwtParams.put("user", new com.vortexsoftware.sdk.types.User(userId, userEmail));

        String jwt = jwtClient.generateJwt(jwtParams);

        System.out.println("DEBUG - User ID: " + userId);
        System.out.println("DEBUG - User Email: " + userEmail);
        System.out.println("DEBUG - Full JWT: " + jwt);

        // Step 1: Fetch widget configuration to get the widget configuration ID and sessionAttestation
        String widgetConfigId;
        String sessionAttestation;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String widgetUrl = clientApiUrl + "/api/v1/widgets/" + componentId + "?templateVariables=lzstr:N4Ig5gTg9grgDgfQHYEMC2BTEAuEBlAEQGkACAFQwGcAXEgcWnhABoQBLJANzeowmXRZcBCCQBqUCLwAeLcI0SY0AIz4IAxrCTUcIAMxzNaOCiQBPAZl0SpGaSQCSSdQDoQAXyA";
            HttpGet getWidget = new HttpGet(widgetUrl);
            getWidget.setHeader("Content-Type", "application/json");
            getWidget.setHeader("Authorization", "Bearer " + jwt);
            getWidget.setHeader("x-session-id", sessionId);

            try (CloseableHttpResponse widgetResponse = client.execute(getWidget)) {
                int statusCode = widgetResponse.getCode();
                String responseBody = new String(widgetResponse.getEntity().getContent().readAllBytes());

                if (statusCode != 200) {
                    throw new RuntimeException("Failed to fetch widget configuration: " + statusCode + " - " + responseBody);
                }

                JsonNode widgetData = objectMapper.readTree(responseBody);
                widgetConfigId = widgetData.get("data").get("widgetConfiguration").get("id").asText();
                sessionAttestation = widgetData.get("data").get("sessionAttestation").asText();
                System.out.println("Using widget configuration ID: " + widgetConfigId);
                System.out.println("Received sessionAttestation from widget");
            }
        }

        // Step 2: Create invitation with the widget configuration ID
        String url = clientApiUrl + "/api/v1/invitations";

        Map<String, Object> emailData = new HashMap<>();
        emailData.put("value", userEmail);
        emailData.put("type", "email");
        emailData.put("role", "member");

        Map<String, Object> payload = new HashMap<>();
        payload.put("emails", emailData);

        Map<String, String> group = new HashMap<>();
        group.put("type", groupType);
        group.put("groupId", groupId);
        group.put("name", groupName);

        Map<String, String> templateVariables = new HashMap<>();
        templateVariables.put("group_name", "SDK Test Group");
        templateVariables.put("inviter_name", "Dr Vortex");
        templateVariables.put("group_member_count", "3");
        templateVariables.put("company_name", "Vortex Inc.");

        Map<String, Object> data = new HashMap<>();
        data.put("payload", payload);
        data.put("group", group);
        data.put("source", "email");
        data.put("widgetConfigurationId", widgetConfigId);
        data.put("templateVariables", templateVariables);

        String jsonBody = objectMapper.writeValueAsString(data);
        System.out.println("DEBUG - Invitation JSON payload: " + jsonBody);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + jwt);
            request.setHeader("x-session-id", sessionId);
            request.setHeader("x-session-attestation", sessionAttestation);
            request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            System.out.println("DEBUG - Request URL: " + url);
            System.out.println("DEBUG - Authorization header length: " + jwt.length());
            System.out.println("DEBUG - Session attestation header length: " + sessionAttestation.length());

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getCode();
                String responseBody = new String(response.getEntity().getContent().readAllBytes());

                if (statusCode != 200 && statusCode != 201) {
                    System.err.println("ERROR - Full response body: " + responseBody);
                    System.err.println("ERROR - Status code: " + statusCode);
                    throw new RuntimeException("Create invitation failed: " + statusCode + " - " + responseBody);
                }

                JsonNode result = objectMapper.readTree(responseBody);

                // The API returns invitationEntries at data.invitationEntries
                String invitationId = null;
                JsonNode dataNode = result.get("data");
                if (dataNode != null) {
                    JsonNode entriesNode = dataNode.get("invitationEntries");
                    if (entriesNode != null && entriesNode.isArray() && entriesNode.size() > 0) {
                        JsonNode idNode = entriesNode.get(0).get("id");
                        if (idNode != null) {
                            invitationId = idNode.asText();
                        }
                    }
                }

                // Fallback to top-level id
                if (invitationId == null && result.has("id")) {
                    invitationId = result.get("id").asText();
                }

                if (invitationId == null) {
                    throw new RuntimeException("Failed to extract invitation ID from response");
                }

                return invitationId;
            }
        }
    }

    private String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }
}
