package com.vortexsoftware.sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vortexsoftware.sdk.types.*;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * Main Vortex SDK client for Java applications
 *
 * Provides JWT generation and Vortex API integration with the same functionality
 * as the Node.js SDK, ensuring compatibility with React providers.
 */
public class VortexClient {
    private static final Logger logger = LoggerFactory.getLogger(VortexClient.class);

    private static final String DEFAULT_BASE_URL = "https://api.vortexsoftware.com";
    private static final String USER_AGENT = "vortex-java-sdk/1.0.0";

    private final String apiKey;
    private final String baseUrl;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Create a new Vortex client with the given API key
     */
    public VortexClient(String apiKey) {
        this(apiKey, System.getenv().getOrDefault("VORTEX_API_BASE_URL", DEFAULT_BASE_URL));
    }

    /**
     * Create a new Vortex client with custom base URL
     */
    public VortexClient(String apiKey, String baseUrl) {
        this.apiKey = Objects.requireNonNull(apiKey, "API key cannot be null");
        this.baseUrl = baseUrl != null ? baseUrl : DEFAULT_BASE_URL;
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    /**
     * Generate a JWT using the same algorithm as the Node.js SDK
     *
     * This replicates the exact JWT generation process from vortex.ts to ensure
     * complete compatibility with React providers.
     */
    public String generateJWT(JWTPayload payload) throws VortexException {
        try {
            // Step 1: Parse API key (same format as Node.js: VRTX.encodedId.key)
            String[] parts = apiKey.split("\\.");
            if (parts.length != 3 || !"VRTX".equals(parts[0])) {
                throw new VortexException("Invalid API key format");
            }

            String encodedId = parts[1];
            String key = parts[2];

            // Step 2: Decode the ID from base64url (same as Node.js uuid.stringify)
            byte[] idBytes = Base64.getUrlDecoder().decode(encodedId);
            String id = bytesToUUID(idBytes);

            // Step 3: Calculate expiration (1 hour from now, same as Node.js)
            long now = Instant.now().getEpochSecond();
            long expires = now + 3600; // 1 hour

            // Step 4: Derive signing key from API key + ID (same HMAC process as Node.js)
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(keySpec);
            byte[] signingKey = hmacSha256.doFinal(id.getBytes(StandardCharsets.UTF_8));

            // Step 5: Build header and payload (same structure as Node.js)
            // CRITICAL: Use LinkedHashMap to preserve property order for signature compatibility
            Map<String, Object> header = new LinkedHashMap<>();
            header.put("iat", now);
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            header.put("kid", id);

            Map<String, Object> jwtPayload = new LinkedHashMap<>();
            jwtPayload.put("userId", payload.getUserId());
            jwtPayload.put("groups", payload.getGroups());
            jwtPayload.put("role", payload.getRole());
            jwtPayload.put("expires", expires);
            jwtPayload.put("identifiers", payload.getIdentifiers());

            // Step 6: Base64URL encode header and payload (same as Node.js)
            String headerJson = objectMapper.writeValueAsString(header);
            String payloadJson = objectMapper.writeValueAsString(jwtPayload);

            String headerB64 = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
            String payloadB64 = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Step 7: Sign the JWT (same HMAC process as Node.js)
            String toSign = headerB64 + "." + payloadB64;
            Mac signer = Mac.getInstance("HmacSHA256");
            SecretKeySpec signingKeySpec = new SecretKeySpec(signingKey, "HmacSHA256");
            signer.init(signingKeySpec);
            byte[] signature = signer.doFinal(toSign.getBytes(StandardCharsets.UTF_8));

            String signatureB64 = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(signature);

            return toSign + "." + signatureB64;

        } catch (Exception e) {
            throw new VortexException("Failed to generate JWT", e);
        }
    }

    /**
     * Make an API request to the Vortex service
     */
    private <T> T apiRequest(String method, String path, Object body, Map<String, String> queryParams, TypeReference<T> responseType) throws VortexException {
        try {
            // Build URL
            String url = baseUrl + path;
            ClassicRequestBuilder requestBuilder = ClassicRequestBuilder.create(method)
                    .setUri(url);

            // Add query parameters
            if (queryParams != null) {
                queryParams.forEach(requestBuilder::addParameter);
            }

            // Add headers
            requestBuilder
                    .addHeader("Content-Type", "application/json")
                    .addHeader("User-Agent", USER_AGENT)
                    .addHeader("x-api-key", apiKey);

            // Add body for POST/PUT requests
            if (body != null && ("POST".equals(method) || "PUT".equals(method))) {
                String bodyJson = objectMapper.writeValueAsString(body);
                requestBuilder.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));
            }

            // Execute request
            try (CloseableHttpResponse response = httpClient.execute(requestBuilder.build())) {
                String responseBody = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);

                if (response.getCode() >= 400) {
                    throw new VortexException("API request failed: " + response.getCode() + " " + response.getReasonPhrase() + " - " + responseBody);
                }

                // Handle empty responses
                if (responseBody.trim().isEmpty()) {
                    return null;
                }

                // Parse JSON response
                return objectMapper.readValue(responseBody, responseType);
            }
        } catch (VortexException e) {
            throw e;
        } catch (Exception e) {
            throw new VortexException("Failed to make API request", e);
        }
    }

    /**
     * Get invitations by target (email, username, phoneNumber)
     */
    public List<InvitationResult> getInvitationsByTarget(String targetType, String targetValue) throws VortexException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("targetType", targetType);
        queryParams.put("targetValue", targetValue);

        InvitationResponse response = apiRequest("GET", "/api/v1/invitations", null, queryParams, new TypeReference<InvitationResponse>() {});
        return response != null ? response.getInvitations() : new ArrayList<>();
    }

    /**
     * Get a specific invitation by ID
     */
    public InvitationResult getInvitation(String invitationId) throws VortexException {
        return apiRequest("GET", "/api/v1/invitations/" + invitationId, null, null, new TypeReference<InvitationResult>() {});
    }

    /**
     * Revoke (delete) an invitation
     */
    public void revokeInvitation(String invitationId) throws VortexException {
        apiRequest("DELETE", "/api/v1/invitations/" + invitationId, null, null, new TypeReference<Void>() {});
    }

    /**
     * Accept multiple invitations for a target
     */
    public InvitationResult acceptInvitations(List<String> invitationIds, InvitationTarget target) throws VortexException {
        AcceptInvitationRequest request = new AcceptInvitationRequest(invitationIds, target);
        return apiRequest("POST", "/api/v1/invitations/accept", request, null, new TypeReference<InvitationResult>() {});
    }

    /**
     * Delete all invitations for a specific group
     */
    public void deleteInvitationsByGroup(String groupType, String groupId) throws VortexException {
        apiRequest("DELETE", "/api/v1/invitations/by-group/" + groupType + "/" + groupId, null, null, new TypeReference<Void>() {});
    }

    /**
     * Get all invitations for a specific group
     */
    public List<InvitationResult> getInvitationsByGroup(String groupType, String groupId) throws VortexException {
        InvitationResponse response = apiRequest("GET", "/api/v1/invitations/by-group/" + groupType + "/" + groupId, null, null, new TypeReference<InvitationResponse>() {});
        return response != null ? response.getInvitations() : new ArrayList<>();
    }

    /**
     * Reinvite a user (send invitation again)
     */
    public InvitationResult reinvite(String invitationId) throws VortexException {
        return apiRequest("POST", "/api/v1/invitations/" + invitationId + "/reinvite", null, null, new TypeReference<InvitationResult>() {});
    }

    /**
     * Close the HTTP client when done
     */
    public void close() {
        try {
            httpClient.close();
        } catch (Exception e) {
            logger.warn("Error closing HTTP client", e);
        }
    }

    /**
     * Convert byte array to UUID string format
     * This replicates the uuid.stringify functionality from Node.js
     */
    private String bytesToUUID(byte[] bytes) {
        if (bytes.length != 16) {
            throw new IllegalArgumentException("UUID bytes must be 16 bytes long");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            if (i == 4 || i == 6 || i == 8 || i == 10) {
                sb.append('-');
            }
            sb.append(String.format("%02x", bytes[i]));
        }
        return sb.toString();
    }
}