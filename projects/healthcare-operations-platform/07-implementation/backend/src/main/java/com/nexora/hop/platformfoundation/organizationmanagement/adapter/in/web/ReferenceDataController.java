package com.nexora.hop.platformfoundation.organizationmanagement.adapter.in.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.organizationmanagement.application.ReferenceDataService;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.Country;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.CurrencyOption;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.LocaleOption;

/**
 * TD-DB-003: read-only country/locale/currency picker data for tenant provisioning and future
 * registration/settings screens. Backed by {@code organization.countries/locales/currencies}
 * (HOP-ENT-FOUND-001); no write endpoints exist since these are small, seed-managed catalogs.
 */
@RestController
@RequestMapping("/api/organization/reference-data")
class ReferenceDataController {

    private final ReferenceDataService service;

    ReferenceDataController(ReferenceDataService service) {
        this.service = service;
    }

    @GetMapping("/countries")
    ResponseEntity<List<CountryResponse>> listCountries() {
        return ResponseEntity.ok(service.listCountries().stream().map(CountryResponse::from).toList());
    }

    @GetMapping("/locales")
    ResponseEntity<List<LocaleResponse>> listLocales() {
        return ResponseEntity.ok(service.listLocales().stream().map(LocaleResponse::from).toList());
    }

    @GetMapping("/currencies")
    ResponseEntity<List<CurrencyResponse>> listCurrencies() {
        return ResponseEntity.ok(service.listCurrencies().stream().map(CurrencyResponse::from).toList());
    }

    record CountryResponse(String countryCode, String nameEsMx, String nameEnUs, String status) {
        static CountryResponse from(Country country) {
            return new CountryResponse(
                    country.countryCode(), country.nameEsMx(), country.nameEnUs(), country.status());
        }
    }

    record LocaleResponse(String localeCode, String nameEsMx, String nameEnUs, boolean isDefault) {
        static LocaleResponse from(LocaleOption locale) {
            return new LocaleResponse(
                    locale.localeCode(), locale.nameEsMx(), locale.nameEnUs(), locale.isDefault());
        }
    }

    record CurrencyResponse(String currencyCode, String nameEsMx, String nameEnUs, int minorUnitDigits) {
        static CurrencyResponse from(CurrencyOption currency) {
            return new CurrencyResponse(
                    currency.currencyCode(), currency.nameEsMx(), currency.nameEnUs(), currency.minorUnitDigits());
        }
    }
}
