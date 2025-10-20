package com.vortexsoftware.sdk.spring;

import com.vortexsoftware.sdk.VortexClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for Vortex Spring Boot integration
 *
 * This automatically sets up VortexClient and related beans when the
 * vortex.api.key property is provided.
 */
@Configuration
@ConditionalOnProperty(name = "vortex.api.key")
public class VortexAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public VortexClient vortexClient(@Value("${vortex.api.key}") String apiKey,
                                    @Value("${vortex.api.base-url:#{null}}") String baseUrl) {
        return baseUrl != null ? new VortexClient(apiKey, baseUrl) : new VortexClient(apiKey);
    }

    @Bean
    @ConditionalOnMissingBean
    public VortexConfig vortexConfig() {
        return new DefaultVortexConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public VortexController vortexController(VortexClient vortexClient, VortexConfig vortexConfig) {
        return new VortexController(vortexClient, vortexConfig);
    }

    /**
     * Default configuration that allows all operations
     * Applications should override this with their own security logic
     */
    private static class DefaultVortexConfig implements VortexConfig {

        @Override
        public VortexUser authenticateUser() {
            // Default implementation - applications should override this
            return null;
        }

        @Override
        public boolean authorizeOperation(String operation, VortexUser user) {
            // Default implementation - allows all operations if user is authenticated
            return user != null;
        }
    }
}