package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.reportgeneration.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GeneratedResultReportRepository {

    GeneratedResultReport save(GeneratedResultReport report);

    Optional<GeneratedResultReport> findById(UUID reportId);

    List<GeneratedResultReport> findByResultId(ResultId resultId, TenantId tenantId);
}
