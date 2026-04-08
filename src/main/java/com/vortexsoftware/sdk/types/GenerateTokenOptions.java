package com.vortexsoftware.sdk.types;

/**
 * Options for generateToken()
 */
public class GenerateTokenOptions {
    /**
     * Expiration time - can be:
     * - Integer: seconds (e.g., 300)
     * - String: duration string (e.g., "5m", "1h", "24h", "7d")
     */
    private Object expiresIn;

    public GenerateTokenOptions() {}

    public GenerateTokenOptions(Object expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * Create options with expiration in seconds
     */
    public static GenerateTokenOptions withExpiresIn(int seconds) {
        return new GenerateTokenOptions(seconds);
    }

    /**
     * Create options with expiration as duration string
     */
    public static GenerateTokenOptions withExpiresIn(String duration) {
        return new GenerateTokenOptions(duration);
    }

    public Object getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Object expiresIn) { this.expiresIn = expiresIn; }
}
