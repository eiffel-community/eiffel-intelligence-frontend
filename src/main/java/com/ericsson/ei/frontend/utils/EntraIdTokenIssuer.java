package com.ericsson.ei.frontend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredentialBuilder;

import jakarta.annotation.PostConstruct;

@Component
@RefreshScope
@ConditionalOnProperty(name = "spring.cloud.azure.active-directory.enabled", havingValue = "true")
public class EntraIdTokenIssuer {

    private static final Logger LOG = LoggerFactory.getLogger(EntraIdTokenIssuer.class);

    @Value("${spring.cloud.azure.active-directory.credential.client-id}")
    private String clientId;

    @Value("${spring.cloud.azure.active-directory.credential.client-secret}")
    private String clientSecret;

    @Value("${spring.cloud.azure.active-directory.profile.tenant-id}")
    private String tenantId;

    @Value("${spring.cloud.azure.active-directory.api-scope}")
    private String scope;

    private TokenCredential credential;

    @PostConstruct
    @EventListener(RefreshScopeRefreshedEvent.class)
    public void refreshCredential() {
        TokenCredential newCredential = new ClientSecretCredentialBuilder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .tenantId(tenantId)
            .build();

        // Validate by acquiring a token before accepting the new credential
        try {
            newCredential.getToken(new TokenRequestContext().addScopes(scope)).block();
            this.credential = newCredential;
            LOG.info("Azure AD credentials refreshed and validated for client-id: {}", clientId);
        } catch (Exception e) {
            LOG.error("New client secret validation FAILED for client-id: {}", clientId, e);
            throw new IllegalStateException("Client secret refresh failed validation", e);
        }
    }

    /**
     * Retrieves an access token for the configured Azure AD scope.
     * @return the access token as a string
     */
    public String getAccessToken() {
        return credential
            .getToken(new TokenRequestContext().addScopes(scope))
            .block()
            .getToken();
    }
}
