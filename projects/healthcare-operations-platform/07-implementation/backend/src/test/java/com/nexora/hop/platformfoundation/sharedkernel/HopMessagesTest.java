package com.nexora.hop.platformfoundation.sharedkernel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Verifies the TD-I18N-002 baseline message catalog wiring: the same key must resolve to
 * different, real prose for es-MX versus en-US, and the no-locale overload must default to the
 * platform default locale (es-MX).
 */
class HopMessagesTest {

    private HopMessages messages;

    @BeforeEach
    void setUp() {
        messages = new HopMessages(new LocalizationConfig().messageSource());
    }

    @Test
    void resolvesDifferentProseForEsMxAndEnUsLocales() {
        String esMx = messages.get("identityaccess.tenant.notfound", Locale.forLanguageTag("es-MX"));
        String enUs = messages.get("identityaccess.tenant.notfound", Locale.forLanguageTag("en-US"));

        assertThat(esMx).isEqualTo("No se encontró el inquilino.");
        assertThat(enUs).isEqualTo("Tenant was not found.");
        assertThat(esMx).isNotEqualTo(enUs);
    }

    @Test
    void noLocaleOverloadDefaultsToThePlatformDefaultLocale() {
        assertThat(messages.get("identityaccess.field.actorUserId.required"))
                .isEqualTo(messages.get("identityaccess.field.actorUserId.required", LocalizationConfig.DEFAULT_LOCALE))
                .isEqualTo("El identificador del usuario que realiza la acción es obligatorio.");
    }

    @Test
    void resolvesArgumentsOverloadWithNoArguments() {
        String message = messages.get("identityaccess.authorization.denied", new Object[0], Locale.forLanguageTag("en-US"));

        assertThat(message).isEqualTo("You do not have permission to perform this action.");
    }
}
