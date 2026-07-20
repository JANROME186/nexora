package com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.domain;

import java.util.List;
import java.util.Optional;

public interface QualityControlRunRepository {

  QualityControlRun save(QualityControlRun run);

  Optional<QualityControlRun> findById(String qcRunId);

  List<QualityControlRun> findByScope(String tenantId, String laboratoryId, String branchId);
}
