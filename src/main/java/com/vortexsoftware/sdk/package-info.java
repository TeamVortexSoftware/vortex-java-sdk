/**
 * Vortex Java SDK for invitation management and JWT generation.
 *
 * <p>This package provides a complete Java SDK for integrating with the Vortex API,
 * offering the same functionality as the Node.js SDK with full compatibility
 * for React providers and other frontend frameworks.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>JWT generation with identical algorithm to Node.js SDK</li>
 *   <li>Complete invitation management (CRUD operations)</li>
 *   <li>Group-based invitation operations</li>
 *   <li>Spring Boot auto-configuration</li>
 *   <li>Comprehensive error handling</li>
 * </ul>
 *
 * <p>Basic usage:</p>
 * <pre>{@code
 * VortexClient client = new VortexClient("your-api-key");
 *
 * // Generate JWT
 * JWTPayload payload = new JWTPayload("user-123", identifiers, groups, "admin");
 * String jwt = client.generateJWT(payload);
 *
 * // Get invitations
 * List<InvitationResult> invitations = client.getInvitationsByTarget("email", "user@example.com");
 *
 * client.close();
 * }</pre>
 *
 * @since 1.0.0
 */
package com.vortexsoftware.sdk;