package org.mamba.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class MicrosoftOAuthConfig {
    @Value("${microsoft.client-id}")
    private String clientId;

    @Value("${microsoft.client-secret}")
    private String clientSecret;

    @Value("${microsoft.redirect-uri}")
    private String redirectUri;

    @Value("${microsoft.tenant-id}")
    private String tenantId;
}