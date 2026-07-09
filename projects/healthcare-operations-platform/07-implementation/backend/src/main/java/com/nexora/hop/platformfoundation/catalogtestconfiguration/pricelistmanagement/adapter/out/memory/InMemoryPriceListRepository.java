package com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.adapter.out.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceEntry;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceList;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceListRepository;

@Repository
@Profile("!local")
class InMemoryPriceListRepository implements PriceListRepository {

    private final Map<String, PriceList> priceLists = new ConcurrentHashMap<>();
    private final Map<String, List<PriceEntry>> entries = new ConcurrentHashMap<>();

    @Override
    public PriceList save(PriceList priceList) {
        priceLists.put(priceList.priceListId(), priceList);
        return priceList;
    }

    @Override
    public Optional<PriceList> findById(String priceListId) {
        return Optional.ofNullable(priceLists.get(priceListId));
    }

    @Override
    public List<PriceList> findByLaboratoryId(String laboratoryId) {
        return priceLists.values().stream().filter(list -> list.laboratoryId().equals(laboratoryId)).toList();
    }

    @Override
    public boolean existsByCode(String laboratoryId, String code, String excludePriceListId) {
        return priceLists.values().stream()
                .anyMatch(list -> list.laboratoryId().equals(laboratoryId)
                        && list.code().equals(code)
                        && !list.priceListId().equals(excludePriceListId));
    }

    @Override
    public PriceEntry saveEntry(PriceEntry entry) {
        entries.computeIfAbsent(entry.priceListId(), key -> new ArrayList<>()).add(entry);
        return entry;
    }

    @Override
    public List<PriceEntry> findEntries(String priceListId) {
        return List.copyOf(entries.getOrDefault(priceListId, List.of()));
    }
}
