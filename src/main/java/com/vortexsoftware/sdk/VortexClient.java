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
    /**
     * Sign a user object for use with the widget signature prop.
     * @vortex.category authentication
     * @vortex.since 0.5.0
     * @param user Map with "id", "email", and optional user fields
     * @return Signature string in "kid:hexDigest" format
     * @throws VortexException if API key is invalid or signing fails
     */
    public String sign(Map<String, Object> user) throws VortexException {
        String[] parts = apiKey.split("\\.");
        if (parts.length != 3 || !"VRTX".equals(parts[0])) {
            throw new VortexException("Invalid API key format");
        }

        try {
            byte[] uuidBytes = Base64.getUrlDecoder().decode(parts[1]);
            UUID uuid = bytesToUuid(uuidBytes);
            String kid = uuid.toString();
            String key = parts[2];

            // Derive signing key
            Mac signingMac = Mac.getInstance("HmacSHA256");
            signingMac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signingKey = signingMac.doFinal(kid.getBytes(StandardCharsets.UTF_8));

            // Build canonical payload — include ALL user fields with key normalization
            TreeMap<String, Object> canonical = new TreeMap<>();
            for (Map.Entry<String, Object> entry : user.entrySet()) {
                String k = entry.getKey();
                if ("id".equals(k)) {
                    canonical.put("userId", entry.getValue());
                } else if ("email".equals(k)) {
                    canonical.put("userEmail", entry.getValue());
                } else {
                    canonical.put(k, entry.getValue());
                }
            }
            if (!canonical.containsKey("userId") || canonical.get("userId") == null) {
                throw new VortexException("userId (or id) is required for signing");
            }

            // TreeMap is already sorted; recursively canonicalize nested structures
            Object canonicalized = canonicalizeValue(canonical);
            String canonicalJson = objectMapper.writeValueAsString(canonicalized);

            // HMAC-SHA256
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signingKey, "HmacSHA256"));
            byte[] digestBytes = mac.doFinal(canonicalJson.getBytes(StandardCharsets.UTF_8));
            String digest = bytesToHex(digestBytes);

            return kid + ":" + digest;
        } catch (Exception e) {
            throw new VortexException("Failed to sign user data: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Object canonicalizeValue(Object value) {
        if (value instanceof Map) {
            TreeMap<String, Object> sorted = new TreeMap<>();
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                sorted.put(entry.getKey(), canonicalizeValue(entry.getValue()));
            }
            return sorted;
        }
        if (value instanceof java.util.List) {
            java.util.List<Object> list = (java.util.List<Object>) value;
            java.util.List<Object> result = new java.util.ArrayList<>();
            for (Object item : list) {
                result.add(canonicalizeValue(item));
            }
            return result;
        }
        return value;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static UUID bytesToUuid(byte[] bytes) {
        long msb = 0, lsb = 0;
        for (int i = 0; i < 8; i++) msb = (msb << 8) | (bytes[i] & 0xff);
        for (int i = 8; i < 16; i++) lsb = (lsb << 8) | (bytes[i] & 0xff);
        return new UUID(msb, lsb);
    }

    /**
     * Generate a JWT using the same algorithm as the Node.js SDK
     * @vortex.category authentication
     * @vortex.since 0.3.0
     * @param params Map containing "user" key with User object
     * @return JWT token string
     * @throws VortexException if JWT generation fails
     */
    public String generateJwt(Map<String, Object> params) throws VortexException {
        return generateJwt(params, null);
    }

    public String generateJwt(Map<String, Object> params, GenerateTokenOptions options) throws VortexException {
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

            // Step 3: Calculate expiration (default: 30 days, configurable via options)
            long now = Instant.now().getEpochSecond();
            long expiresInSeconds = (options != null && options.getExpiresIn() != null) ? parseExpiresIn(options.getExpiresIn()) : 2592000; // 30 days
            long expires = now + expiresInSeconds;

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

            // Add name if present (prefer new property, fall back to deprecated)
            String userName = user.getName() != null ? user.getName() : user.getUserName();
            if (userName != null) {
                jwtPayload.put("name", userName);
            }

            // Add avatarUrl if present (prefer new property, fall back to deprecated)
            String userAvatarUrl = user.getAvatarUrl() != null ? user.getAvatarUrl() : user.getUserAvatarUrl();
            if (userAvatarUrl != null) {
                jwtPayload.put("avatarUrl", userAvatarUrl);
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
     * @vortex.category invitations
     * @vortex.since 0.1.0
     * @param targetType Type of target (email, phone, etc.)
     * @param targetValue The target value
     * @return List of invitations
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
     *
     * <pre>{@code
     * InvitationResult invitation = client.getInvitation("inv-123");
     * System.out.println("Status: " + invitation.getStatus());
     * }</pre>
     *
     * @vortex.category invitations
     * @vortex.since 0.1.0
     * @vortex.primary
     * @param invitationId The invitation ID
     * @return The invitation details
     */
    public InvitationResult getInvitation(String invitationId) throws VortexException {
        return apiRequest("GET", "/api/v1/invitations/" + invitationId, null, null, new TypeReference<InvitationResult>() {});
    }

    /**
     * Revoke (delete) an invitation
     * @vortex.category invitations
     * @vortex.since 0.1.0
     * @param invitationId The invitation ID to revoke
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
     * <pre>{@code
     * AcceptUser user = new AcceptUser()
     *     .setEmail("user@example.com")
     *     .setName("John Doe");
     * InvitationResult result = client.acceptInvitation("inv-123", user);
     * }</pre>
     *
     * @vortex.category invitations
     * @vortex.since 0.6.0
     * @vortex.primary
     * @param invitationId Single invitation ID to accept
     * @param user User object with email and/or phone
     * @return The accepted invitation result
     */
    public InvitationResult acceptInvitation(String invitationId, AcceptUser user) throws VortexException {
        return acceptInvitations(List.of(invitationId), user);
    }

    /**
     * Delete all invitations for a specific scope
     * @vortex.category invitations
     * @vortex.since 0.4.0
     * @param scopeType The scope type (organization, team, etc.)
     * @param scope The scope identifier
     */
    public void deleteInvitationsByScope(String scopeType, String scope) throws VortexException {
        apiRequest("DELETE", "/api/v1/invitations/by-scope/" + scopeType + "/" + scope, null, null, new TypeReference<Void>() {});
    }

    /**
     * Get all invitations for a specific scope
     * @vortex.category invitations
     * @vortex.since 0.4.0
     * @param scopeType The scope type (organization, team, etc.)
     * @param scope The scope identifier
     * @return List of invitations for the scope
     */
    public List<InvitationResult> getInvitationsByScope(String scopeType, String scope) throws VortexException {
        InvitationResponse response = apiRequest("GET", "/api/v1/invitations/by-scope/" + scopeType + "/" + scope, null, null, new TypeReference<InvitationResponse>() {});
        return response != null ? response.getInvitations() : new ArrayList<>();
    }

    /**
     * Reinvite a user (send invitation again)
     * @vortex.category invitations
     * @vortex.since 0.2.0
     * @param invitationId The invitation ID to reinvite
     * @return The reinvited invitation result
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
     *     new CreateInvitationScope("team", "team-789", "Engineering")
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

        // Scope translation: flat params > scopes > groups
        if (request.getScopeId() != null && !request.getScopeId().isEmpty()
                && (request.getGroups() == null || request.getGroups().isEmpty())
                && (request.getScopes() == null || request.getScopes().isEmpty())) {
            request.setGroups(java.util.List.of(
                new CreateInvitationScope(
                    request.getScopeType() != null ? request.getScopeType() : "",
                    request.getScopeId(),
                    request.getScopeName() != null ? request.getScopeName() : ""
                )
            ));
        } else if (request.getScopes() != null && !request.getScopes().isEmpty()
                && (request.getGroups() == null || request.getGroups().isEmpty())) {
            request.setGroups(request.getScopes());
        }

        return apiRequest("POST", "/api/v1/invitations", request, null, new TypeReference<CreateInvitationResponse>() {});
    }

    /**
     * Get autojoin domains configured for a specific scope
     * @vortex.category autojoin
     * @vortex.since 0.6.0
     * @param scopeType The type of scope (e.g., "organization", "team", "project")
     * @param scope The scope identifier (customer's group ID)
     * @return AutojoinDomainsResponse with autojoin domains and invitation
     */
    public AutojoinDomainsResponse getAutojoinDomains(String scopeType, String scope) throws VortexException {
        String encodedScopeType = java.net.URLEncoder.encode(scopeType, StandardCharsets.UTF_8);
        String encodedScope = java.net.URLEncoder.encode(scope, StandardCharsets.UTF_8);
        String path = "/api/v1/invitations/by-scope/" + encodedScopeType + "/" + encodedScope + "/autojoin";
        return apiRequest("GET", path, null, null, new TypeReference<AutojoinDomainsResponse>() {});
    }

    /**
     * Configure autojoin domains for a specific scope
     * @vortex.category autojoin
     * @vortex.since 0.6.0
     * @param request The configure autojoin request
     * @return AutojoinDomainsResponse with updated autojoin domains
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
        if (request.getComponentId() == null || request.getComponentId().isEmpty()) {
            throw new VortexException("componentId is required");
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

        return apiRequest("POST", "/api/v1/invitations/sync-internal-invitation", request, null, new TypeReference<SyncInternalInvitationResponse>() {});
    }

    /**
     * Parse an expiration time string or number into seconds
     */
    private long parseExpiresIn(Object expiresIn) throws VortexException {
        if (expiresIn instanceof Number) {
            long seconds = ((Number) expiresIn).longValue();
            if (seconds <= 0) {
                throw new VortexException("Invalid expiresIn value: \"" + expiresIn + "\". Numeric expiresIn must be positive.");
            }
            return seconds;
        }
        if (expiresIn instanceof String) {
            String str = (String) expiresIn;
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^(\\d+)(m|h|d)$");
            java.util.regex.Matcher matcher = pattern.matcher(str);
            if (!matcher.matches()) {
                throw new VortexException("Invalid expiresIn format: \"" + str + "\". Use \"5m\", \"1h\", \"24h\", \"7d\" or seconds.");
            }
            long value = Long.parseLong(matcher.group(1));
            if (value <= 0) {
                throw new VortexException("Invalid expiresIn value: \"" + str + "\". Duration must be positive.");
            }
            String unit = matcher.group(2);
            switch (unit) {
                case "m": return value * 60L;
                case "h": return value * 60L * 60L;
                case "d": return value * 60L * 60L * 24L;
                default: throw new VortexException("Unknown time unit: " + unit);
            }
        }
        throw new VortexException("expiresIn must be a String or Number");
    }

    /**
     * Generate a signed token for use with Vortex widgets
     * @vortex.category authentication
     * @vortex.since 0.8.0
     * @vortex.primary
     * @param payload Data to sign (user, component, scope, vars, etc.)
     * @return Signed JWT token string
     */
    public String generateToken(GenerateTokenPayload payload) throws VortexException {
        return generateToken(payload, null);
    }

    /**
     * Generate a signed token for use with Vortex widgets
     * @vortex.category authentication
     * @vortex.since 0.8.0
     * @param payload Data to sign (user, component, scope, vars, etc.)
     * @param options Optional configuration (expiresIn)
     * @return Signed JWT token string
     */
    public String generateToken(GenerateTokenPayload payload, GenerateTokenOptions options) throws VortexException {
        try {
            if (payload.getUser() == null || payload.getUser().getId() == null) {
                logger.warn("[Vortex SDK] Warning: signing payload without user.id means invitations won't be securely attributed.");
            }
            String[] parts = apiKey.split("\\.");
            if (parts.length != 3 || !"VRTX".equals(parts[0])) {
                throw new VortexException("Invalid API key format");
            }
            byte[] uuidBytes = Base64.getUrlDecoder().decode(parts[1]);
            String kid = bytesToUUID(uuidBytes);

            long expiresInSeconds = 30L * 24L * 60L * 60L; // Default 30 days
            if (options != null && options.getExpiresIn() != null) {
                expiresInSeconds = parseExpiresIn(options.getExpiresIn());
            }
            long now = Instant.now().getEpochSecond();
            long exp = now + expiresInSeconds;

            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(parts[2].getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(keySpec);
            byte[] signingKey = hmacSha256.doFinal(kid.getBytes(StandardCharsets.UTF_8));

            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            header.put("kid", kid);

            Map<String, Object> jwtPayload = new LinkedHashMap<>();
            if (payload.getComponent() != null) jwtPayload.put("component", payload.getComponent());
            if (payload.getTrigger() != null) jwtPayload.put("trigger", payload.getTrigger());
            if (payload.getEmbed() != null) jwtPayload.put("embed", payload.getEmbed());
            if (payload.getUser() != null) jwtPayload.put("user", objectMapper.convertValue(payload.getUser(), Map.class));
            if (payload.getScope() != null) jwtPayload.put("scope", payload.getScope());
            if (payload.getVars() != null) jwtPayload.put("vars", payload.getVars());
            if (payload.getAdditionalProperties() != null) jwtPayload.putAll(payload.getAdditionalProperties());
            jwtPayload.put("iat", now);
            jwtPayload.put("exp", exp);

            String headerB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(objectMapper.writeValueAsString(header).getBytes(StandardCharsets.UTF_8));
            String payloadB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(objectMapper.writeValueAsString(jwtPayload).getBytes(StandardCharsets.UTF_8));

            String toSign = headerB64 + "." + payloadB64;
            Mac signer = Mac.getInstance("HmacSHA256");
            signer.init(new SecretKeySpec(signingKey, "HmacSHA256"));
            String signatureB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(signer.doFinal(toSign.getBytes(StandardCharsets.UTF_8)));

            return toSign + "." + signatureB64;
        } catch (VortexException e) {
            throw e;
        } catch (Exception e) {
            throw new VortexException("Failed to generate token: " + e.getMessage(), e);
        }
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