package com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.domain;

import java.util.List;
import java.util.Optional;

public interface ReceptionVisitRepository {

    ReceptionVisit save(ReceptionVisit visit);

    Optional<ReceptionVisit> findById(String visitId);

    List<ReceptionVisit> findByTenantId(String tenantId);
}
