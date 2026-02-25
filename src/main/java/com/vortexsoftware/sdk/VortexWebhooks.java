package com.vortexsoftware.sdk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vortexsoftware.sdk.types.VortexAnalyticsEvent;
import com.vortexsoftware.sdk.types.VortexWebhookEvent;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Core webhook verification and parsing.
 *
 * <p>This class is framework-agnostic — use it directly or with
 * the Spring Boot auto-configuration.</p>
 *
 * <pre>{@code
 * VortexWebhooks webhooks = new VortexWebhooks("whsec_your_secret");
 *
 * // In any HTTP handler:
 * Object event = webhooks.constructEvent(requestBody, signatureHeader);
 * if (event instanceof VortexWebhookEvent) {
 *     VortexWebhookEvent webhookEvent = (VortexWebhookEvent) event;
 *     System.out.println("Type: " + webhookEvent.getType());
 * }
 * }</pre>
 */
public class VortexWebhooks {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String secret;

    /**
     * Create a new VortexWebhooks instance.
     *
     * @param secret The webhook signing secret from your Vortex dashboard
     * @throws IllegalArgumentException if secret is null or empty
     */
    public VortexWebhooks(String secret) {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("VortexWebhooks requires a secret");
        }
        this.secret = secret;
    }

    /**
     * Verify the HMAC-SHA256 signature of an incoming webhook payload.
     *
     * @param payload   The raw request body
     * @param signature The value of the X-Vortex-Signature header
     * @return true if the signature is valid
     */
    public boolean verifySignature(String payload, String signature) {
        if (signature == null || signature.isEmpty()) {
            return false;
        }

        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(keySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expected = bytesToHex(hash);

            // Timing-safe comparison
            return MessageDigest.isEqual(
                    signature.getBytes(StandardCharsets.UTF_8),
                    expected.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verify and parse an incoming webhook payload.
     *
     * @param payload   The raw request body
     * @param signature The value of the X-Vortex-Signature header
     * @return A {@link VortexWebhookEvent} or {@link VortexAnalyticsEvent}
     * @throws VortexWebhookSignatureException if the signature is invalid
     * @throws VortexException                 if the payload cannot be parsed
     */
    public Object constructEvent(String payload, String signature)
            throws VortexWebhookSignatureException, VortexException {
        if (!verifySignature(payload, signature)) {
            throw new VortexWebhookSignatureException(
                    "Webhook signature verification failed. Ensure you are using " +
                            "the raw request body and the correct signing secret.");
        }

        try {
            JsonNode node = objectMapper.readTree(payload);
            if (node.has("name")) {
                return objectMapper.treeToValue(node, VortexAnalyticsEvent.class);
            } else {
                return objectMapper.treeToValue(node, VortexWebhookEvent.class);
            }
        } catch (Exception e) {
            throw new VortexException("Failed to parse webhook event", e);
        }
    }

    /**
     * Check if a parsed event is a webhook event.
     *
     * @param event The event returned by {@link #constructEvent}
     * @return true if the event is a VortexWebhookEvent
     */
    public static boolean isWebhookEvent(Object event) {
        return event instanceof VortexWebhookEvent;
    }

    /**
     * Check if a parsed event is an analytics event.
     *
     * @param event The event returned by {@link #constructEvent}
     * @return true if the event is a VortexAnalyticsEvent
     */
    public static boolean isAnalyticsEvent(Object event) {
        return event instanceof VortexAnalyticsEvent;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
