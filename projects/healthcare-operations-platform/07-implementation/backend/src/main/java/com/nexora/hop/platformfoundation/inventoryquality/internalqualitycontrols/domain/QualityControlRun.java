package com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Internal quality-control execution record for BCM-QLT-001 Internal Quality Controls. */
public record QualityControlRun(
    String qcRunId,
    String tenantId,
    String laboratoryId,
    String branchId,
    String testDefinitionId,
    String controlMaterialStockLotId,
    BigDecimal measuredValue,
    ExpectedRange expectedRange,
    String ruleEvaluation,
    String acceptanceDecision,
    List<String> linkedLaboratoryResultIds,
    String performedBy,
    LocalDateTime performedAt,
    String evidenceReference,
    String overrideReason,
    String overrideBy,
    AuditMetadata audit) {

  public static final String EVALUATION_IN_CONTROL = "in_control";
  public static final String EVALUATION_OUT_OF_CONTROL = "out_of_control";
  public static final String DECISION_ACCEPTED = "accepted";
  public static final String DECISION_REPEAT_REQUIRED = "repeat_required";
  public static final String DECISION_REJECTED = "rejected";

  public QualityControlRun overrideDecision(
      String decision, String reason, String supervisorId, AuditMetadata newAudit) {
    return new QualityControlRun(
        qcRunId,
        tenantId,
        laboratoryId,
        branchId,
        testDefinitionId,
        controlMaterialStockLotId,
        measuredValue,
        expectedRange,
        ruleEvaluation,
        decision,
        linkedLaboratoryResultIds,
        performedBy,
        performedAt,
        evidenceReference,
        reason,
        supervisorId,
        newAudit);
  }
}
