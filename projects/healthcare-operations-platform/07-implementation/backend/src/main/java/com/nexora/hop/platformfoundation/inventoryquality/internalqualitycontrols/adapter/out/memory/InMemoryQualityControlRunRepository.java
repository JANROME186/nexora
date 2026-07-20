package com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.adapter.out.memory;

import com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.domain.QualityControlRun;
import com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.domain.QualityControlRunRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
class InMemoryQualityControlRunRepository implements QualityControlRunRepository {

  private final ConcurrentHashMap<String, QualityControlRun> runs = new ConcurrentHashMap<>();

  @Override
  public QualityControlRun save(QualityControlRun run) {
    runs.put(run.qcRunId(), run);
    return run;
  }

  @Override
  public Optional<QualityControlRun> findById(String qcRunId) {
    return Optional.ofNullable(runs.get(qcRunId));
  }

  @Override
  public List<QualityControlRun> findByScope(
      String tenantId, String laboratoryId, String branchId) {
    return runs.values().stream()
        .filter(run -> run.tenantId().equals(tenantId))
        .filter(run -> run.laboratoryId().equals(laboratoryId))
        .filter(run -> run.branchId().equals(branchId))
        .sorted(Comparator.comparing(QualityControlRun::performedAt))
        .toList();
  }
}
