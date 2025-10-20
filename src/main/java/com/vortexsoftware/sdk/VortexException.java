package com.vortexsoftware.sdk;

/**
 * Exception thrown by Vortex SDK operations
 */
public class VortexException extends Exception {

    public VortexException(String message) {
        super(message);
    }

    public VortexException(String message, Throwable cause) {
        super(message, cause);
    }

    public VortexException(Throwable cause) {
        super(cause);
    }
}