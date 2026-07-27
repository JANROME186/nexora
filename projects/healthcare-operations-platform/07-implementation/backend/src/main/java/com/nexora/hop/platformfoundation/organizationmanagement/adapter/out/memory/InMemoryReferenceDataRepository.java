package com.nexora.hop.platformfoundation.organizationmanagement.adapter.out.memory;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.organizationmanagement.domain.Country;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.CurrencyOption;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.LocaleOption;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.ReferenceDataRepository;

/**
 * Mirrors the baseline seed rows {@code db/platform-foundation/schema.sql} inserts into {@code
 * organization.countries/locales/currencies} (MX/US, es-MX/en-US, MXN/USD), so the default/test
 * profile's reference-data reads behave the same as the real Postgres-backed one.
 */
@Repository
@Profile("!local")
class InMemoryReferenceDataRepository implements ReferenceDataRepository {

    @Override
    public List<Country> findAllCountries() {
        return List.of(
                new Country("MX", "México", "Mexico", "active"),
                new Country("US", "Estados Unidos", "United States", "active"));
    }

    @Override
    public List<LocaleOption> findAllLocales() {
        return List.of(
                new LocaleOption("es-MX", "Español (México)", "Spanish (Mexico)", true),
                new LocaleOption("en-US", "Inglés (Estados Unidos)", "English (United States)", false));
    }

    @Override
    public List<CurrencyOption> findAllCurrencies() {
        return List.of(
                new CurrencyOption("MXN", "Peso mexicano", "Mexican Peso", 2),
                new CurrencyOption("USD", "Dólar estadounidense", "US Dollar", 2));
    }
}
