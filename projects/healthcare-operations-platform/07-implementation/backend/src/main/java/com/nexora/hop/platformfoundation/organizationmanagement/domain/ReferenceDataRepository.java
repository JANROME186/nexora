package com.nexora.hop.platformfoundation.organizationmanagement.domain;

import java.util.List;

/**
 * TD-DB-003: read-only access to the country/locale/currency reference tables added by
 * HOP-ENT-FOUND-001 ({@code organization.countries/locales/currencies}). No write side exists;
 * these are small, static, seed-managed catalogs, not a user-editable screen.
 */
public interface ReferenceDataRepository {

    List<Country> findAllCountries();

    List<LocaleOption> findAllLocales();

    List<CurrencyOption> findAllCurrencies();
}
