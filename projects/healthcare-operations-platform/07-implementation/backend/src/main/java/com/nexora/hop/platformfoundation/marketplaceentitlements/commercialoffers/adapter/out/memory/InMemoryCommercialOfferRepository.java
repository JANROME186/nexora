package com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.domain.CommercialOffer;
import com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.domain.CommercialOfferRepository;

@Repository
@Profile("!local")
class InMemoryCommercialOfferRepository implements CommercialOfferRepository {

    private final Map<String, CommercialOffer> offers = new ConcurrentHashMap<>();

    @Override
    public CommercialOffer save(CommercialOffer offer) {
        offers.put(offer.offerId(), offer);
        return offer;
    }

    @Override
    public Optional<CommercialOffer> findById(String offerId) {
        return Optional.ofNullable(offers.get(offerId));
    }

    @Override
    public List<CommercialOffer> findByPackageId(String packageId) {
        return offers.values().stream().filter(candidate -> candidate.packageId().equals(packageId)).toList();
    }

    @Override
    public List<CommercialOffer> findAll() {
        return List.copyOf(offers.values());
    }
}
