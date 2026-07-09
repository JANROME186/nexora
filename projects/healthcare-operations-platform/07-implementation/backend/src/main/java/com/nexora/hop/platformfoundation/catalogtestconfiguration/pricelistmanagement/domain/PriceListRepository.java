package com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain;

import java.util.List;
import java.util.Optional;

public interface PriceListRepository {

    PriceList save(PriceList priceList);

    Optional<PriceList> findById(String priceListId);

    List<PriceList> findByLaboratoryId(String laboratoryId);

    /**
     * Returns every price list in the given lifecycle status. Used by effective-dated price
     * resolution (RN-006) which receives no laboratory in its contract and must therefore search
     * published price lists by their entries.
     */
    List<PriceList> findByStatus(String status);

    boolean existsByCode(String laboratoryId, String code, String excludePriceListId);

    PriceEntry saveEntry(PriceEntry entry);

    List<PriceEntry> findEntries(String priceListId);
}
