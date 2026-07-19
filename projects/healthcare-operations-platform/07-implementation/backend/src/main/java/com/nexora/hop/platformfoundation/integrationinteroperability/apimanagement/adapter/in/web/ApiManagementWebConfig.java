package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.adapter.in.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Registers {@link PartnerApiKeyRateLimitInterceptor} independently of identityaccess's security wiring. */
@Configuration
class ApiManagementWebConfig implements WebMvcConfigurer {

    private final PartnerApiKeyRateLimitInterceptor rateLimitInterceptor;

    ApiManagementWebConfig(PartnerApiKeyRateLimitInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/api/**");
    }
}
