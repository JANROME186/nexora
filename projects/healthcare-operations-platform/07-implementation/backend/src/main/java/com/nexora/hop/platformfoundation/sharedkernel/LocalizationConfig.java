package com.nexora.hop.platformfoundation.sharedkernel;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Backend message-catalog configuration (TD-I18N-002 baseline demonstration).
 * <p>
 * Exposes a Spring {@link MessageSource} backed by the {@code classpath:i18n/messages} resource
 * bundle family ({@code messages.properties}, {@code messages_es_MX.properties},
 * {@code messages_en_US.properties}). es-MX is the platform default/fallback locale, per
 * {@code enterprise-product-foundation-standard.md}'s
 * {@code mandatory_foundations.localization_and_i18n} section
 * ({@code required_base_locales: [es-MX, en-US]}, {@code default_locale: es-MX}).
 */
@Configuration
public class LocalizationConfig {

    /** Platform default/fallback locale, per the enterprise foundation standard. */
    public static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("es-MX");

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setUseCodeAsDefaultMessage(false);
        // Resolve to the platform default locale (es-MX) instead of falling back to the JVM's
        // system locale when a message is requested for a locale with no matching bundle.
        messageSource.setDefaultLocale(DEFAULT_LOCALE);
        return messageSource;
    }
}
