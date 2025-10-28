package com.vortexsoftware.sdk.spring;

import com.vortexsoftware.sdk.VortexClient;
import com.vortexsoftware.sdk.VortexException;
import com.vortexsoftware.sdk.types.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the Spring Boot controller integration
 */
@ExtendWith(MockitoExtension.class)
public class VortexControllerTest {

    @Mock
    private VortexClient mockClient;

    @Mock
    private VortexConfig mockConfig;

    private VortexController controller;
    private VortexConfig.VortexUser testUser;

    @BeforeEach
    void setUp() {
        controller = new VortexController(mockClient, mockConfig);

        // Create test user
        List<InvitationTarget> identifiers = Arrays.asList(
                new InvitationTarget("email", "test@example.com")
        );
        List<InvitationGroup> groups = Arrays.asList(
                new InvitationGroup("internal-uuid", "account-id", "team-1", "team", "Engineering", "2025-01-01T00:00:00Z")
        );
        testUser = new VortexConfig.VortexUser("user-123", identifiers, groups, "admin");
    }

    @Test
    void testGenerateJWT_Success() throws VortexException {
        // Mock authentication and authorization
        when(mockConfig.authenticateUser()).thenReturn(testUser);
        when(mockConfig.authorizeOperation("JWT", testUser)).thenReturn(true);
        when(mockClient.generateJWT(any(JWTPayload.class))).thenReturn("test-jwt-token");

        ResponseEntity<?> response = controller.generateJWT();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("test-jwt-token", body.get("jwt"));
    }

    @Test
    void testGenerateJWT_NotAuthenticated() {
        when(mockConfig.authenticateUser()).thenReturn(null);

        ResponseEntity<?> response = controller.generateJWT();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Authentication required", body.get("error"));
    }

    @Test
    void testGenerateJWT_NotAuthorized() {
        when(mockConfig.authenticateUser()).thenReturn(testUser);
        when(mockConfig.authorizeOperation("JWT", testUser)).thenReturn(false);

        ResponseEntity<?> response = controller.generateJWT();

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Not authorized to generate JWT", body.get("error"));
    }

    @Test
    void testGenerateJWT_VortexException() throws VortexException {
        when(mockConfig.authenticateUser()).thenReturn(testUser);
        when(mockConfig.authorizeOperation("JWT", testUser)).thenReturn(true);
        when(mockClient.generateJWT(any(JWTPayload.class))).thenThrow(new VortexException("JWT generation failed"));

        ResponseEntity<?> response = controller.generateJWT();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Failed to generate JWT", body.get("error"));
    }

    @Test
    void testGetInvitationsByTarget_Success() throws VortexException {
        InvitationResult invitation = new InvitationResult();
        invitation.setId("inv-123");
        invitation.setStatus("delivered");
        List<InvitationResult> invitations = Arrays.asList(invitation);

        when(mockConfig.authenticateUser()).thenReturn(testUser);
        when(mockConfig.authorizeOperation("GET_INVITATIONS", testUser)).thenReturn(true);
        when(mockClient.getInvitationsByTarget("email", "test@example.com")).thenReturn(invitations);

        ResponseEntity<?> response = controller.getInvitationsByTarget("email", "test@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        List<InvitationResult> returnedInvitations = (List<InvitationResult>) body.get("invitations");
        assertEquals(1, returnedInvitations.size());
        assertEquals("inv-123", returnedInvitations.get(0).getId());
    }

    @Test
    void testGetInvitation_Success() throws VortexException {
        InvitationResult invitation = new InvitationResult();
        invitation.setId("inv-123");
        invitation.setStatus("delivered");

        when(mockConfig.authenticateUser()).thenReturn(testUser);
        when(mockConfig.authorizeOperation("GET_INVITATION", testUser)).thenReturn(true);
        when(mockClient.getInvitation("inv-123")).thenReturn(invitation);

        ResponseEntity<?> response = controller.getInvitation("inv-123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        InvitationResult returnedInvitation = (InvitationResult) response.getBody();
        assertEquals("inv-123", returnedInvitation.getId());
        assertEquals("delivered", returnedInvitation.getStatus());
    }

    @Test
    void testGetInvitation_NotFound() throws VortexException {
        when(mockConfig.authenticateUser()).thenReturn(testUser);
        when(mockConfig.authorizeOperation("GET_INVITATION", testUser)).thenReturn(true);
        when(mockClient.getInvitation("nonexistent")).thenThrow(new VortexException("Not found"));

        ResponseEntity<?> response = controller.getInvitation("nonexistent");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Invitation not found", body.get("error"));
    }

    @Test
    void testRevokeInvitation_Success() throws VortexException {
        when(mockConfig.authenticateUser()).thenReturn(testUser);
        when(mockConfig.authorizeOperation("REVOKE_INVITATION", testUser)).thenReturn(true);
        doNothing().when(mockClient).revokeInvitation("inv-123");

        ResponseEntity<?> response = controller.revokeInvitation("inv-123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(true, body.get("success"));
    }

    @Test
    void testAcceptInvitations_Success() throws VortexException {
        AcceptInvitationRequest request = new AcceptInvitationRequest();
        request.setInvitationIds(Arrays.asList("inv-123", "inv-456"));
        request.setTarget(new InvitationTarget("email", "test@example.com"));

        InvitationResult result = new InvitationResult();
        result.setId("inv-123");
        result.setStatus("accepted");

        when(mockConfig.authenticateUser()).thenReturn(testUser);
        when(mockConfig.authorizeOperation("ACCEPT_INVITATIONS", testUser)).thenReturn(true);
        when(mockClient.acceptInvitations(request.getInvitationIds(), request.getTarget())).thenReturn(result);

        ResponseEntity<?> response = controller.acceptInvitations(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        InvitationResult returnedResult = (InvitationResult) response.getBody();
        assertEquals("inv-123", returnedResult.getId());
        assertEquals("accepted", returnedResult.getStatus());
    }

    @Test
    void testGetInvitationsByGroup_Success() throws VortexException {
        InvitationResult invitation = new InvitationResult();
        invitation.setId("inv-123");
        List<InvitationResult> invitations = Arrays.asList(invitation);

        when(mockConfig.authenticateUser()).thenReturn(testUser);
        when(mockConfig.authorizeOperation("GET_GROUP_INVITATIONS", testUser)).thenReturn(true);
        when(mockClient.getInvitationsByGroup("team", "team-123")).thenReturn(invitations);

        ResponseEntity<?> response = controller.getInvitationsByGroup("team", "team-123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        List<InvitationResult> returnedInvitations = (List<InvitationResult>) body.get("invitations");
        assertEquals(1, returnedInvitations.size());
    }

    @Test
    void testDeleteInvitationsByGroup_Success() throws VortexException {
        when(mockConfig.authenticateUser()).thenReturn(testUser);
        when(mockConfig.authorizeOperation("DELETE_GROUP_INVITATIONS", testUser)).thenReturn(true);
        doNothing().when(mockClient).deleteInvitationsByGroup("team", "team-123");

        ResponseEntity<?> response = controller.deleteInvitationsByGroup("team", "team-123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(true, body.get("success"));
    }

    @Test
    void testReinvite_Success() throws VortexException {
        InvitationResult result = new InvitationResult();
        result.setId("inv-123");
        result.setStatus("queued");

        when(mockConfig.authenticateUser()).thenReturn(testUser);
        when(mockConfig.authorizeOperation("REINVITE", testUser)).thenReturn(true);
        when(mockClient.reinvite("inv-123")).thenReturn(result);

        ResponseEntity<?> response = controller.reinvite("inv-123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        InvitationResult returnedResult = (InvitationResult) response.getBody();
        assertEquals("inv-123", returnedResult.getId());
        assertEquals("queued", returnedResult.getStatus());
    }
}