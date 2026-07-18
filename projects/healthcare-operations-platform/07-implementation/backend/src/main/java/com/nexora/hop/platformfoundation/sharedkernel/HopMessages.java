package com.nexora.hop.platformfoundation.sharedkernel;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * Thin convenience wrapper around Spring's {@link MessageSource} for resolving backend
 * message-catalog keys (TD-I18N-002 baseline demonstration, currently adopted end-to-end by the
 * identityaccess module; other modules' hardcoded error prose remains tracked debt).
 * <p>
 * <b>Current limitation:</b> locale is supplied explicitly by the caller. This backend has no
 * request-scoped authentication/session context yet, so there is nowhere to resolve a real
 * {@code Accept-Language} header or a tenant/user-selected locale from. Request-scoped locale
 * resolution is tracked as follow-up technical debt once real HTTP authentication/session context
 * exists in this backend.
 */
@Component
public class HopMessages {

    private final MessageSource messageSource;

    public HopMessages(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /** Resolves {@code key} for {@code locale}, with no message arguments. */
    public String get(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    /** Resolves {@code key} for {@code locale}, interpolating {@code args} into the message. */
    public String get(String key, Object[] args, Locale locale) {
        return messageSource.getMessage(key, args, locale);
    }

    /** Resolves {@code key} using the platform default locale ({@link LocalizationConfig#DEFAULT_LOCALE}). */
    public String get(String key) {
        return get(key, LocalizationConfig.DEFAULT_LOCALE);
    }
}
