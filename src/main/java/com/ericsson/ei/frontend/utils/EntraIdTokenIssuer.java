package com.ericsson.ei.frontend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredentialBuilder;

@Component
public class EntraIdTokenIssuer {

    @Value("${spring.cloud.azure.active-directory.credential.client-id}")
    private String clientId;

    @Value("${spring.cloud.azure.active-directory.credential.client-secret}")
    private String clientSecret;

    @Value("${spring.cloud.azure.active-directory.profile.tenant-id}")
    private String tenantId;

    @Value("${spring.cloud.azure.active-directory.api-scope}")
    private String scope;

    // This method now returns a plain string containing the token
    public String getAccessToken() {
        try {
            // Build the ClientSecretCredential using Spring's property values
            TokenCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .authorityHost("https://login.microsoftonline.com")
                .build();

            // Get the token using the scope defined (default is ".default")
            String token = clientSecretCredential
                .getToken(new TokenRequestContext().addScopes(scope))
                .block()
                .getToken();

            // Return the token string
            return token;
        } catch (Exception e) {
            // In case of error, return the error message as string
            return "Error: " + e.getMessage();
        }
    }
}
