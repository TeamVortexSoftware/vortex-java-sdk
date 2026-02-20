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
    private static final String SDK_NAME = "vortex-java-sdk";
    private static final String SDK_VERSION = loadSdkVersion();
    private static final String USER_AGENT = SDK_NAME + "/" + SDK_VERSION;

    private static String loadSdkVersion() {
        String version = VortexClient.class.getPackage().getImplementationVersion();
        if (version != null) {
            return version;
        }
        try (java.io.InputStream is = VortexClient.class.getResourceAsStream("/vortex-sdk.properties")) {
            if (is != null) {
                java.util.Properties props = new java.util.Properties();
                props.load(is);
                return props.getProperty("version", "unknown");
            }
        } catch (Exception ignored) {}
        return "unknown";
    }

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
     * <p>This replicates the exact JWT generation process from vortex.ts to ensure
     * complete compatibility with React providers.</p>
     *
     * <p>Simple usage:</p>
     * <pre>{@code
     * Map<String, Object> params = new HashMap<>();
     * User user = new User("user-123", "user@example.com");
     * user.setName("Jane Doe");                                      // Optional: user's display name
     * user.setAvatarUrl("https://example.com/avatars/jane.jpg");    // Optional: user's avatar URL
     * user.setAdminScopes(Arrays.asList("autojoin"));               // Optional: grants admin privileges
     * params.put("user", user);
     * String jwt = client.generateJwt(params);
     * }</pre>
     *
     * <p>With additional properties:</p>
     * <pre>{@code
     * Map<String, Object> params = new HashMap<>();
     * User user = new User("user-123", "user@example.com");
     * params.put("user", user);
     * params.put("role", "admin");
     * params.put("department", "Engineering");
     * String jwt = client.generateJwt(params);
     * }</pre>
     *
     * @param params Map containing "user" key with User object and optional additional properties
     * @return JWT token
     * @throws VortexException if JWT generation fails
     */
    public String generateJwt(Map<String, Object> params) throws VortexException {
        try {
            // Extract user from params
            if (params == null || !params.containsKey("user")) {
                throw new VortexException("params must contain 'user' key");
            }

            Object userObj = params.get("user");
            User user;
            if (userObj instanceof User) {
                user = (User) userObj;
            } else {
                throw new VortexException("'user' must be a User object");
            }

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

            // Build payload - start with required fields
            Map<String, Object> jwtPayload = new LinkedHashMap<>();
            jwtPayload.put("userId", user.getId());
            jwtPayload.put("userEmail", user.getEmail());
            jwtPayload.put("expires", expires);

            // Add name if present
            if (user.getUserName() != null) {
                jwtPayload.put("userName", user.getUserName());
            }

            // Add userAvatarUrl if present
            if (user.getUserAvatarUrl() != null) {
                jwtPayload.put("userAvatarUrl", user.getUserAvatarUrl());
            }

            // Add adminScopes if present
            if (user.getAdminScopes() != null) {
                jwtPayload.put("adminScopes", user.getAdminScopes());
            }

            // Add allowedEmailDomains if present (for domain-restricted invitations)
            if (user.getAllowedEmailDomains() != null && !user.getAllowedEmailDomains().isEmpty()) {
                jwtPayload.put("allowedEmailDomains", user.getAllowedEmailDomains());
            }

            // Add any additional properties from params (excluding 'user')
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (!"user".equals(entry.getKey())) {
                    jwtPayload.put(entry.getKey(), entry.getValue());
                }
            }

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
                    .addHeader("x-api-key", apiKey)
                    .addHeader("x-vortex-sdk-name", SDK_NAME)
                    .addHeader("x-vortex-sdk-version", SDK_VERSION);

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
     * Accept multiple invitations using the new User format (preferred)
     *
     * <p>Example:</p>
     * <pre>{@code
     * AcceptUser user = new AcceptUser();
     * user.setEmail("user@example.com");
     * user.setName("John Doe");
     * List<InvitationResult> results = client.acceptInvitations(invitationIds, user);
     * }</pre>
     *
     * @param invitationIds List of invitation IDs to accept
     * @param user User object with email or phone (and optional name)
     * @return List of accepted invitation results
     * @throws VortexException if the API request fails
     */
    public InvitationResult acceptInvitations(List<String> invitationIds, AcceptUser user) throws VortexException {
        // Validate that either email or phone is provided
        if ((user.getEmail() == null || user.getEmail().isEmpty()) &&
            (user.getPhone() == null || user.getPhone().isEmpty())) {
            throw new VortexException("User must have either email or phone");
        }

        AcceptInvitationRequest request = new AcceptInvitationRequest(invitationIds, user);
        InvitationResponse response = apiRequest("POST", "/api/v1/invitations/accept", request, null, new TypeReference<InvitationResponse>() {});

        // Return the first invitation from the response
        if (response.getInvitations() != null && !response.getInvitations().isEmpty()) {
            return response.getInvitations().get(0);
        }
        throw new VortexException("No invitations returned from accept endpoint");
    }

    /**
     * Accept multiple invitations using legacy target format (deprecated)
     *
     * @deprecated Use {@link #acceptInvitations(List, AcceptUser)} instead.
     *             This method is maintained for backward compatibility but will be removed in a future version.
     *             Use the new User format which supports email, phone, and name.
     *
     * <p>Example migration:</p>
     * <pre>{@code
     * // Old way (deprecated):
     * InvitationTarget target = new InvitationTarget("email", "user@example.com");
     * List<InvitationResult> results = client.acceptInvitations(invitationIds, target);
     *
     * // New way (preferred):
     * AcceptUser user = new AcceptUser("user@example.com");
     * InvitationResult result = client.acceptInvitations(invitationIds, user);
     * }</pre>
     *
     * @param invitationIds List of invitation IDs to accept
     * @param target Legacy target object with type and value
     * @return Accepted invitation result
     * @throws VortexException if the API request fails
     */
    @Deprecated
    public InvitationResult acceptInvitations(List<String> invitationIds, InvitationTarget target) throws VortexException {
        logger.warn("[Vortex SDK] DEPRECATED: Passing an InvitationTarget is deprecated. Use the AcceptUser format instead: acceptInvitations(invitationIds, new AcceptUser(email))");

        // Convert target to User format
        AcceptUser user = new AcceptUser();
        if ("email".equals(target.getType())) {
            user.setEmail(target.getValue());
        } else if ("phone".equals(target.getType()) || "phoneNumber".equals(target.getType())) {
            user.setPhone(target.getValue());
        } else {
            // For other types (like 'username'), try to use as email
            user.setEmail(target.getValue());
        }

        // Call the new method
        return acceptInvitations(invitationIds, user);
    }

    /**
     * Accept multiple invitations for multiple targets (deprecated)
     *
     * @deprecated Use {@link #acceptInvitations(List, AcceptUser)} instead and call once per user.
     *             This method calls the API once per target and is maintained only for backward compatibility.
     *
     * <p>Example migration:</p>
     * <pre>{@code
     * // Old way (deprecated):
     * List<InvitationTarget> targets = Arrays.asList(
     *     new InvitationTarget("email", "user1@example.com"),
     *     new InvitationTarget("email", "user2@example.com")
     * );
     * InvitationResult result = client.acceptInvitations(invitationIds, targets);
     *
     * // New way (preferred) - call once per user:
     * for (InvitationTarget target : targets) {
     *     AcceptUser user = new AcceptUser(target.getValue());
     *     InvitationResult result = client.acceptInvitations(invitationIds, user);
     * }
     * }</pre>
     *
     * @param invitationIds List of invitation IDs to accept
     * @param targets List of legacy target objects
     * @return Last accepted invitation result
     * @throws VortexException if the API request fails
     */
    @Deprecated
    public InvitationResult acceptInvitations(List<String> invitationIds, List<InvitationTarget> targets) throws VortexException {
        logger.warn("[Vortex SDK] DEPRECATED: Passing a list of InvitationTarget is deprecated. Use the AcceptUser format and call once per user instead.");

        if (targets == null || targets.isEmpty()) {
            throw new VortexException("No targets provided");
        }

        InvitationResult lastResult = null;
        VortexException lastException = null;

        // Call the endpoint once per target
        for (InvitationTarget target : targets) {
            try {
                lastResult = acceptInvitations(invitationIds, target);
            } catch (VortexException e) {
                lastException = e;
            }
        }

        if (lastException != null) {
            throw lastException;
        }

        return lastResult;
    }

    /**
     * Accept a single invitation (recommended method)
     *
     * <p>This is the recommended method for accepting invitations.</p>
     *
     * @param invitationId Single invitation ID to accept
     * @param user User object with email and/or phone
     * @return InvitationResult The accepted invitation result
     * @throws VortexException If the request fails
     *
     * @example
     * AcceptUser user = new AcceptUser("user@example.com");
     * InvitationResult result = client.acceptInvitation("inv-123", user);
     */
    public InvitationResult acceptInvitation(String invitationId, AcceptUser user) throws VortexException {
        return acceptInvitations(List.of(invitationId), user);
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
     * Create an invitation from your backend
     *
     * <p>This method allows you to create invitations programmatically using your API key,
     * without requiring a user JWT token. Useful for server-side invitation creation,
     * such as "People You May Know" flows or admin-initiated invitations.</p>
     *
     * <p>Target types:</p>
     * <ul>
     *   <li><code>email</code>: Send an email invitation</li>
     *   <li><code>sms</code>: Create an SMS invitation (short link returned for you to send)</li>
     *   <li><code>internal</code>: Create an internal invitation for PYMK flows (no email sent)</li>
     * </ul>
     *
     * <p>Example - Email invitation:</p>
     * <pre>{@code
     * CreateInvitationRequest request = new CreateInvitationRequest(
     *     "widget-config-123",
     *     CreateInvitationTarget.email("invitee@example.com"),
     *     new Inviter("user-456", "inviter@example.com", "John Doe", null)
     * );
     * request.setGroups(Arrays.asList(
     *     new CreateInvitationGroup("team", "team-789", "Engineering")
     * ));
     * CreateInvitationResponse response = client.createInvitation(request);
     * System.out.println("Invitation created: " + response.getId());
     * System.out.println("Short link: " + response.getShortLink());
     * }</pre>
     *
     * <p>Example - Internal invitation (PYMK flow):</p>
     * <pre>{@code
     * CreateInvitationRequest request = new CreateInvitationRequest(
     *     "widget-config-123",
     *     CreateInvitationTarget.internal("internal-user-abc"),
     *     new Inviter("user-456")
     * );
     * request.setSource("pymk");
     * CreateInvitationResponse response = client.createInvitation(request);
     * }</pre>
     *
     * @param request The create invitation request
     * @return CreateInvitationResponse with id, shortLink, status, and createdAt
     * @throws VortexException if the API request fails
     */
    public CreateInvitationResponse createInvitation(CreateInvitationRequest request) throws VortexException {
        if (request == null) {
            throw new VortexException("Request cannot be null");
        }
        if (request.getWidgetConfigurationId() == null || request.getWidgetConfigurationId().isEmpty()) {
            throw new VortexException("widgetConfigurationId is required");
        }
        if (request.getTarget() == null || request.getTarget().getValue() == null || request.getTarget().getValue().isEmpty()) {
            throw new VortexException("target with value is required");
        }
        if (request.getInviter() == null || request.getInviter().getUserId() == null || request.getInviter().getUserId().isEmpty()) {
            throw new VortexException("inviter with userId is required");
        }

        return apiRequest("POST", "/api/v1/invitations", request, null, new TypeReference<CreateInvitationResponse>() {});
    }

    /**
     * Get autojoin domains configured for a specific scope
     *
     * <p>Example:</p>
     * <pre>{@code
     * AutojoinDomainsResponse response = client.getAutojoinDomains("organization", "acme-org");
     * for (AutojoinDomain domain : response.getAutojoinDomains()) {
     *     System.out.println("Domain: " + domain.getDomain());
     * }
     * }</pre>
     *
     * @param scopeType The type of scope (e.g., "organization", "team", "project")
     * @param scope The scope identifier (customer's group ID)
     * @return AutojoinDomainsResponse with autojoin domains and associated invitation
     * @throws VortexException if the API request fails
     */
    public AutojoinDomainsResponse getAutojoinDomains(String scopeType, String scope) throws VortexException {
        String encodedScopeType = java.net.URLEncoder.encode(scopeType, StandardCharsets.UTF_8);
        String encodedScope = java.net.URLEncoder.encode(scope, StandardCharsets.UTF_8);
        String path = "/api/v1/invitations/by-scope/" + encodedScopeType + "/" + encodedScope + "/autojoin";
        return apiRequest("GET", path, null, null, new TypeReference<AutojoinDomainsResponse>() {});
    }

    /**
     * Configure autojoin domains for a specific scope
     *
     * <p>This endpoint syncs autojoin domains - it will add new domains, remove domains
     * not in the provided list, and deactivate the autojoin invitation if all domains
     * are removed (empty array).</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     * ConfigureAutojoinRequest request = new ConfigureAutojoinRequest(
     *     "acme-org",
     *     "organization",
     *     Arrays.asList("acme.com", "acme.org"),
     *     "widget-123"
     * );
     * request.setScopeName("Acme Corporation");
     * AutojoinDomainsResponse response = client.configureAutojoin(request);
     * }</pre>
     *
     * @param request The configure autojoin request
     * @return AutojoinDomainsResponse with updated autojoin domains and associated invitation
     * @throws VortexException if the API request fails
     */
    public AutojoinDomainsResponse configureAutojoin(ConfigureAutojoinRequest request) throws VortexException {
        if (request == null) {
            throw new VortexException("Request cannot be null");
        }
        if (request.getScope() == null || request.getScope().isEmpty()) {
            throw new VortexException("scope is required");
        }
        if (request.getScopeType() == null || request.getScopeType().isEmpty()) {
            throw new VortexException("scopeType is required");
        }
        if (request.getWidgetId() == null || request.getWidgetId().isEmpty()) {
            throw new VortexException("widgetId is required");
        }

        return apiRequest("POST", "/api/v1/invitations/autojoin", request, null, new TypeReference<AutojoinDomainsResponse>() {});
    }

    /**
     * Sync an internal invitation action (accept or decline)
     *
     * <p>This method notifies Vortex that an internal invitation was accepted or declined
     * within your application, so Vortex can update the invitation status accordingly.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     * SyncInternalInvitationRequest request = new SyncInternalInvitationRequest(
     *     "user-123",      // creatorId
     *     "user-456",      // targetValue
     *     "accepted",      // action
     *     "component-uuid" // componentId
     * );
     * SyncInternalInvitationResponse response = client.syncInternalInvitation(request);
     * System.out.println("Processed: " + response.getProcessed());
     * }</pre>
     *
     * @param request The sync internal invitation request
     * @return SyncInternalInvitationResponse with processed count and invitationIds
     * @throws VortexException if the API request fails
     */
    public SyncInternalInvitationResponse syncInternalInvitation(SyncInternalInvitationRequest request) throws VortexException {
        if (request == null) {
            throw new VortexException("Request cannot be null");
        }

        return apiRequest("POST", "/api/v1/invitation-actions/sync-internal-invitation", request, null, new TypeReference<SyncInternalInvitationResponse>() {});
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