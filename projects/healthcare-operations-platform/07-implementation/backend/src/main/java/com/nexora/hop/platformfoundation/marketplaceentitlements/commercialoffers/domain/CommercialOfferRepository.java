package com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.domain;

import java.util.List;
import java.util.Optional;

public interface CommercialOfferRepository {

    CommercialOffer save(CommercialOffer offer);

    Optional<CommercialOffer> findById(String offerId);

    List<CommercialOffer> findByPackageId(String packageId);

    List<CommercialOffer> findAll();
}
