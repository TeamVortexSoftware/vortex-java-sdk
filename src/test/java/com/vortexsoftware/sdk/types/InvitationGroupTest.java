package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for InvitationGroup deserialization to ensure all 6 fields
 * from the API response (MemberGroups table) are properly captured
 */
public class InvitationGroupTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testInvitationGroupDeserialization() throws Exception {
        // This is the actual structure returned by the API (MemberGroups table)
        String apiResponse = """
            {
                "id": "550e8400-e29b-41d4-a716-446655440000",
                "accountId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
                "groupId": "workspace-123",
                "type": "workspace",
                "name": "My Workspace",
                "createdAt": "2025-01-27T12:00:00.000Z"
            }
            """;

        InvitationGroup group = objectMapper.readValue(apiResponse, InvitationGroup.class);

        // Verify all 6 fields are present and correct
        assertEquals("550e8400-e29b-41d4-a716-446655440000", group.getId(),
                "ID should match");
        assertEquals("6ba7b810-9dad-11d1-80b4-00c04fd430c8", group.getAccountId(),
                "Account ID should match");
        assertEquals("workspace-123", group.getGroupId(),
                "Group ID (customer's ID) should match");
        assertEquals("workspace", group.getType(),
                "Type should match");
        assertEquals("My Workspace", group.getName(),
                "Name should match");
        assertEquals("2025-01-27T12:00:00.000Z", group.getCreatedAt(),
                "CreatedAt timestamp should match");
    }

    @Test
    public void testInvitationResultWithGroups() throws Exception {
        String apiResponse = """
            {
                "id": "inv-123",
                "accountId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
                "clickThroughs": 5,
                "configurationAttributes": {},
                "attributes": {},
                "createdAt": "2025-01-27T12:00:00.000Z",
                "deactivated": false,
                "deliveryCount": 1,
                "deliveryTypes": ["email"],
                "foreignCreatorId": "user-123",
                "invitationType": "single_use",
                "modifiedAt": null,
                "status": "delivered",
                "target": [{"type": "email", "value": "test@example.com"}],
                "views": 10,
                "widgetConfigurationId": "widget-123",
                "projectId": "project-123",
                "groups": [
                    {
                        "id": "550e8400-e29b-41d4-a716-446655440000",
                        "accountId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
                        "groupId": "workspace-123",
                        "type": "workspace",
                        "name": "My Workspace",
                        "createdAt": "2025-01-27T12:00:00.000Z"
                    }
                ],
                "accepts": []
            }
            """;

        InvitationResult invitation = objectMapper.readValue(apiResponse, InvitationResult.class);

        assertNotNull(invitation.getGroups(), "Groups should not be null");
        assertEquals(1, invitation.getGroups().size(), "Should have 1 group");

        InvitationGroup group = invitation.getGroups().get(0);
        assertEquals("workspace-123", group.getGroupId(),
                "Customer's group ID should be accessible");
        assertEquals("6ba7b810-9dad-11d1-80b4-00c04fd430c8", group.getAccountId(),
                "Account ID should be accessible");
        assertEquals("550e8400-e29b-41d4-a716-446655440000", group.getId(),
                "Vortex internal ID should be accessible");
    }

    @Test
    public void testInvitationGroupSerialization() throws Exception {
        InvitationGroup group = new InvitationGroup(
                "550e8400-e29b-41d4-a716-446655440000",
                "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
                "workspace-123",
                "workspace",
                "My Workspace",
                "2025-01-27T12:00:00.000Z"
        );

        String json = objectMapper.writeValueAsString(group);

        assertTrue(json.contains("\"id\":\"550e8400-e29b-41d4-a716-446655440000\""),
                "JSON should contain id field");
        assertTrue(json.contains("\"accountId\":\"6ba7b810-9dad-11d1-80b4-00c04fd430c8\""),
                "JSON should contain accountId field");
        assertTrue(json.contains("\"groupId\":\"workspace-123\""),
                "JSON should contain groupId field");
        assertTrue(json.contains("\"type\":\"workspace\""),
                "JSON should contain type field");
        assertTrue(json.contains("\"name\":\"My Workspace\""),
                "JSON should contain name field");
        assertTrue(json.contains("\"createdAt\":\"2025-01-27T12:00:00.000Z\""),
                "JSON should contain createdAt field");
    }

    @Test
    public void testGroupIdAccessibility() throws Exception {
        // This test specifically verifies the fix for the reported issue
        // where customers couldn't access groupId
        String apiResponse = """
            {
                "id": "internal-uuid",
                "accountId": "account-uuid",
                "groupId": "customer-workspace-id",
                "type": "workspace",
                "name": "Customer Workspace",
                "createdAt": "2025-01-27T12:00:00.000Z"
            }
            """;

        InvitationGroup group = objectMapper.readValue(apiResponse, InvitationGroup.class);

        // This is the critical assertion - customers need access to THEIR group ID
        assertNotNull(group.getGroupId(),
                "GroupID must not be null - customers need this to identify their groups");
        assertEquals("customer-workspace-id", group.getGroupId(),
                "Customer's group ID must be accessible and match what they provided");
    }
}
