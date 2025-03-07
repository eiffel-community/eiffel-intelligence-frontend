package com.ericsson.ei.frontend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@SuppressWarnings("deprecation")
@Configuration
@ConditionalOnProperty(name = "spring.cloud.azure.active-directory.enabled", havingValue = "true")
public class SecurityConfigAzureAD extends WebSecurityConfigurerAdapter {
    
    @Value("${spring.cloud.azure.active-directory.credential.client-id}")
    private String clientId;

    @Value("${spring.cloud.azure.active-directory.profile.tenant-id}")
    private String tenantId;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/authentication/*").permitAll()
            .anyRequest().authenticated()
            .and()
            .oauth2Login()  // Enable Azure MFA for H2M
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .and()
            .oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(jwtAuthenticationConverter())  // Configure JWT converter
            .decoder(jwtDecoder()); // Configure JWT decoder to handle client credentials flow
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        // You can add additional configuration for authorities mapping here
        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Use Azure AD's JWKS endpoint
        String jwkSetUri = "https://login.microsoftonline.com/" + tenantId + "/discovery/v2.0/keys";
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        // token validators (e.g., audience, issuer) to validate the token properly
        OAuth2TokenValidator<Jwt> audienceValidator = new OAuth2TokenValidator<Jwt>() {
            @Override
            public OAuth2TokenValidatorResult validate(Jwt token) {
                if (!token.getAudience().contains("api://" + clientId)) {
                    return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_audience", "Invalid audience", null));
                }
                return OAuth2TokenValidatorResult.success();
            }
        };

        OAuth2TokenValidator<Jwt> issuerValidator = new OAuth2TokenValidator<Jwt>() {
            @Override
            public OAuth2TokenValidatorResult validate(Jwt token) {
                String issuer = token.getIssuer().toString();
                // Validate the issuer for both v1.0 and v2.0 endpoints
                if (!issuer.equals("https://login.microsoftonline.com/" + tenantId +"/v2.0") && !issuer.equals("https://sts.windows.net/" + tenantId + "/")) {
                    return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_issuer", "Invalid issuer", null));
                }
                return OAuth2TokenValidatorResult.success();
            }
        };

        // Chain validators together (audience and issuer)
        OAuth2TokenValidator<Jwt> tokenValidator = new DelegatingOAuth2TokenValidator<>(audienceValidator, issuerValidator);
        jwtDecoder.setJwtValidator(tokenValidator); // Apply validators to the decoder
        return jwtDecoder;
    }
}
