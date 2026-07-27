package com.nexora.hop.platformfoundation.organizationmanagement.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.organizationmanagement.domain.Country;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.CurrencyOption;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.LocaleOption;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.ReferenceDataRepository;

/** TD-DB-003: application-layer read access to the country/locale/currency reference catalogs. */
@Service
public class ReferenceDataService {

    private final ReferenceDataRepository repository;

    ReferenceDataService(ReferenceDataRepository repository) {
        this.repository = repository;
    }

    public List<Country> listCountries() {
        return repository.findAllCountries();
    }

    public List<LocaleOption> listLocales() {
        return repository.findAllLocales();
    }

    public List<CurrencyOption> listCurrencies() {
        return repository.findAllCurrencies();
    }
}
