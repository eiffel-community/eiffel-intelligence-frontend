package com.ericsson.ei.frontend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Registers {@link ResilientContextRefresher} as the primary ContextRefresher bean,
 * enabling recovery from corrupted ENC() values without restart.
 */
@Configuration
@ConditionalOnProperty(name = "spring.cloud.azure.active-directory.enabled", havingValue = "true")
public class ResilientRefreshConfig {

    @Bean
    @Primary
    public ResilientContextRefresher resilientContextRefresher(
            ConfigurableApplicationContext context,
            RefreshScope scope,
            RefreshAutoConfiguration.RefreshProperties properties) {
        return new ResilientContextRefresher(context, scope, properties);
    }
}
