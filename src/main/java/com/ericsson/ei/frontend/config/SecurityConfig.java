package com.ericsson.ei.frontend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "spring.cloud.azure.active-directory.enabled", havingValue = "false")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Disable CSRF protection
            .authorizeRequests()
            .anyRequest().permitAll()
            .and()
            .formLogin().disable();
    }
}
