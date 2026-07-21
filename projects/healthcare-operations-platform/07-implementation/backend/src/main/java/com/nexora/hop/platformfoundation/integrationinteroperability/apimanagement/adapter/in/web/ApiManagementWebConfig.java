package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.adapter.in.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers {@link PartnerApiKeyRateLimitInterceptor} (RN-004, partner-classification) and
 * {@link PublicApiRateLimitInterceptor} (RN-007, public-classification, materially reducing
 * TD-BE-015) independently of identityaccess's security wiring.
 */
@Configuration
class ApiManagementWebConfig implements WebMvcConfigurer {

    private final PartnerApiKeyRateLimitInterceptor partnerRateLimitInterceptor;
    private final PublicApiRateLimitInterceptor publicRateLimitInterceptor;

    ApiManagementWebConfig(
            PartnerApiKeyRateLimitInterceptor partnerRateLimitInterceptor,
            PublicApiRateLimitInterceptor publicRateLimitInterceptor) {
        this.partnerRateLimitInterceptor = partnerRateLimitInterceptor;
        this.publicRateLimitInterceptor = publicRateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(partnerRateLimitInterceptor).addPathPatterns("/api/**");
        registry.addInterceptor(publicRateLimitInterceptor)
                .addPathPatterns(PublicApiRateLimitInterceptor.PUBLIC_API_BASE_PATH + "/**");
    }
}
