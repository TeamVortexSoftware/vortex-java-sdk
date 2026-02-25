package com.vortexsoftware.sdk;

/**
 * Exception thrown when webhook signature verification fails.
 */
public class VortexWebhookSignatureException extends VortexException {

    public VortexWebhookSignatureException(String message) {
        super(message);
    }

    public VortexWebhookSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
