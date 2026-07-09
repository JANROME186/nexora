package com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain;

import java.util.List;
import java.util.Optional;

public interface PriceListRepository {

    PriceList save(PriceList priceList);

    Optional<PriceList> findById(String priceListId);

    List<PriceList> findByLaboratoryId(String laboratoryId);

    boolean existsByCode(String laboratoryId, String code, String excludePriceListId);

    PriceEntry saveEntry(PriceEntry entry);

    List<PriceEntry> findEntries(String priceListId);
}
