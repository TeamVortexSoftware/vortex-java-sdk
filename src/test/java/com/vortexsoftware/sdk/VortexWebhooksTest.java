package com.vortexsoftware.sdk;

import com.vortexsoftware.sdk.types.VortexAnalyticsEvent;
import com.vortexsoftware.sdk.types.VortexWebhookEvent;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class VortexWebhooksTest {

    private static final String SECRET = "whsec_test_secret_123";

    private static final String WEBHOOK_EVENT_PAYLOAD = "{" +
            "\"id\":\"evt_123\"," +
            "\"type\":\"invitation.accepted\"," +
            "\"timestamp\":\"2025-01-15T12:00:00.000Z\"," +
            "\"accountId\":\"acc_123\"," +
            "\"environmentId\":\"env_456\"," +
            "\"sourceTable\":\"invitations\"," +
            "\"operation\":\"update\"," +
            "\"data\":{\"invitationId\":\"inv_789\"}" +
            "}";

    private static final String ANALYTICS_EVENT_PAYLOAD = "{" +
            "\"id\":\"evt_456\"," +
            "\"name\":\"widget_loaded\"," +
            "\"accountId\":\"acc_123\"," +
            "\"organizationId\":\"org_123\"," +
            "\"projectId\":\"proj_123\"," +
            "\"environmentId\":\"env_456\"," +
            "\"timestamp\":\"2025-01-15T12:00:00.000Z\"" +
            "}";

    private static String sign(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void constructorRequiresSecret() {
        assertThrows(IllegalArgumentException.class, () -> new VortexWebhooks(""));
        assertThrows(IllegalArgumentException.class, () -> new VortexWebhooks(null));
    }

    @Test
    void verifySignatureValid() {
        VortexWebhooks wh = new VortexWebhooks(SECRET);
        assertTrue(wh.verifySignature(WEBHOOK_EVENT_PAYLOAD, sign(WEBHOOK_EVENT_PAYLOAD, SECRET)));
    }

    @Test
    void verifySignatureInvalid() {
        VortexWebhooks wh = new VortexWebhooks(SECRET);
        assertFalse(wh.verifySignature(WEBHOOK_EVENT_PAYLOAD, "bad_sig"));
    }

    @Test
    void verifySignatureEmpty() {
        VortexWebhooks wh = new VortexWebhooks(SECRET);
        assertFalse(wh.verifySignature(WEBHOOK_EVENT_PAYLOAD, ""));
    }

    @Test
    void verifySignatureWrongSecret() {
        VortexWebhooks wh = new VortexWebhooks(SECRET);
        assertFalse(wh.verifySignature(WEBHOOK_EVENT_PAYLOAD, sign(WEBHOOK_EVENT_PAYLOAD, "wrong")));
    }

    @Test
    void constructWebhookEvent() throws Exception {
        VortexWebhooks wh = new VortexWebhooks(SECRET);
        Object event = wh.constructEvent(WEBHOOK_EVENT_PAYLOAD, sign(WEBHOOK_EVENT_PAYLOAD, SECRET));
        assertTrue(VortexWebhooks.isWebhookEvent(event));
        VortexWebhookEvent we = (VortexWebhookEvent) event;
        assertEquals("evt_123", we.getId());
        assertEquals("invitation.accepted", we.getType());
        assertEquals("acc_123", we.getAccountId());
    }

    @Test
    void constructAnalyticsEvent() throws Exception {
        VortexWebhooks wh = new VortexWebhooks(SECRET);
        Object event = wh.constructEvent(ANALYTICS_EVENT_PAYLOAD, sign(ANALYTICS_EVENT_PAYLOAD, SECRET));
        assertTrue(VortexWebhooks.isAnalyticsEvent(event));
        VortexAnalyticsEvent ae = (VortexAnalyticsEvent) event;
        assertEquals("evt_456", ae.getId());
        assertEquals("widget_loaded", ae.getName());
    }

    @Test
    void constructEventBadSignature() {
        VortexWebhooks wh = new VortexWebhooks(SECRET);
        assertThrows(VortexWebhookSignatureException.class,
                () -> wh.constructEvent(WEBHOOK_EVENT_PAYLOAD, "bad"));
    }
}
