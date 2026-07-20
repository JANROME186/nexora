package com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.application;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.domain.ExpectedRange;
import com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.domain.QualityControlRun;
import com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.domain.QualityControlRunRepository;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.application.LotManagementService;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.StockLot;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.application.InventoryItemService;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InvalidInventoryCommandException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryConflictException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryEntityNotFoundException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryErrorCodes;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** BCM-QLT-001 Internal Quality Controls compiled from the capability model. */
@Service
public class InternalQualityControlService {

  private final QualityControlRunRepository repository;
  private final LotManagementService lotManagementService;
  private final InventoryItemService inventoryItemService;
  private final AuditRecorder auditRecorder;
  private final Clock clock;

  @Autowired
  public InternalQualityControlService(
      QualityControlRunRepository repository,
      LotManagementService lotManagementService,
      InventoryItemService inventoryItemService,
      AuditRecorder auditRecorder) {
    this(repository, lotManagementService, inventoryItemService, auditRecorder, Clock.systemUTC());
  }

  InternalQualityControlService(
      QualityControlRunRepository repository,
      LotManagementService lotManagementService,
      InventoryItemService inventoryItemService,
      AuditRecorder auditRecorder,
      Clock clock) {
    this.repository = repository;
    this.lotManagementService = lotManagementService;
    this.inventoryItemService = inventoryItemService;
    this.auditRecorder = auditRecorder;
    this.clock = clock;
  }

  public QualityControlRun recordQualityControlRun(RecordQualityControlRunCommand command) {
    StockLot lot = lotManagementService.requireLot(command.controlMaterialStockLotId());
    InventoryItem item = inventoryItemService.requireItem(lot.inventoryItemId());
    validateControlMaterial(lot, item, command);
    ExpectedRange range = validateRange(command.expectedMin(), command.expectedMax());
    BigDecimal measured = command.measuredValue();
    if (measured == null) {
      throw new InvalidInventoryCommandException(
          "Measured value is required.", InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
    String evaluation = evaluate(measured, range);
    String decision =
        QualityControlRun.EVALUATION_IN_CONTROL.equals(evaluation)
            ? QualityControlRun.DECISION_ACCEPTED
            : QualityControlRun.DECISION_REPEAT_REQUIRED;
    String actor = requireText(command.performedBy(), "Performed by is required.");
    LocalDateTime now = LocalDateTime.now(clock);
    QualityControlRun saved =
        repository.save(
            new QualityControlRun(
                UUID.randomUUID().toString(),
                item.tenantId(),
                item.laboratoryId(),
                item.branchId(),
                requireText(command.testDefinitionId(), "Test definition id is required."),
                lot.stockLotId(),
                measured,
                range,
                evaluation,
                decision,
                command.linkedLaboratoryResultIds() == null
                    ? List.of()
                    : List.copyOf(command.linkedLaboratoryResultIds()),
                actor,
                command.performedAt() == null ? now : command.performedAt(),
                command.evidenceReference(),
                null,
                null,
                new AuditMetadata(actor, now, actor, now)));
    auditRecorder.recordSystemEvent(
        item.tenantId(),
        "QualityControlRunRecorded",
        "QualityControlRun",
        saved.qcRunId(),
        "{\"ruleEvaluation\":\"%s\",\"acceptanceDecision\":\"%s\"}"
            .formatted(evaluation, decision));
    return saved;
  }

  public QualityControlRun overrideAcceptanceDecision(
      String qcRunId, OverrideAcceptanceDecisionCommand command) {
    QualityControlRun current = requireRun(qcRunId);
    if (!command.supervisorScoped()) {
      throw new InventoryConflictException(
          "Supervisor authorization is required for quality-control override.",
          InventoryErrorCodes.QC_OVERRIDE_NOT_AUTHORIZED);
    }
    String supervisor = requireText(command.supervisorId(), "Supervisor id is required.");
    String decision = requireText(command.acceptanceDecision(), "Acceptance decision is required.");
    if (!QualityControlRun.DECISION_ACCEPTED.equals(decision)
        && !QualityControlRun.DECISION_REJECTED.equals(decision)
        && !QualityControlRun.DECISION_REPEAT_REQUIRED.equals(decision)) {
      throw new InvalidInventoryCommandException(
          "Acceptance decision is invalid.", InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
    LocalDateTime now = LocalDateTime.now(clock);
    QualityControlRun saved =
        repository.save(
            current.overrideDecision(
                decision,
                requireText(command.overrideReason(), "Override reason is required."),
                supervisor,
                new AuditMetadata(
                    current.audit().createdBy(), current.audit().createdAt(), supervisor, now)));
    auditRecorder.recordSystemEvent(
        current.tenantId(),
        "QualityControlDecisionOverridden",
        "QualityControlRun",
        saved.qcRunId(),
        "{\"acceptanceDecision\":\"%s\"}".formatted(decision));
    return saved;
  }

  public QualityControlRun getQualityControlRun(String qcRunId) {
    return requireRun(qcRunId);
  }

  public List<QualityControlRun> listQualityControlRuns(
      String tenantId, String laboratoryId, String branchId) {
    return repository.findByScope(
        requireText(tenantId, "Tenant id is required."),
        requireText(laboratoryId, "Laboratory id is required."),
        requireText(branchId, "Branch id is required."));
  }

  private QualityControlRun requireRun(String qcRunId) {
    return repository
        .findById(requireText(qcRunId, "Quality-control run id is required."))
        .orElseThrow(
            () ->
                new InventoryEntityNotFoundException(
                    "Quality-control run was not found.",
                    InventoryErrorCodes.QUALITY_CONTROL_NOT_FOUND));
  }

  private static ExpectedRange validateRange(BigDecimal min, BigDecimal max) {
    if (min == null || max == null || min.compareTo(max) > 0) {
      throw new InvalidInventoryCommandException(
          "Expected range is invalid.", InventoryErrorCodes.QC_EXPECTED_RANGE_INVALID);
    }
    return new ExpectedRange(min, max, LocalDateTime.now());
  }

  private static String evaluate(BigDecimal measured, ExpectedRange range) {
    return measured.compareTo(range.min()) >= 0 && measured.compareTo(range.max()) <= 0
        ? QualityControlRun.EVALUATION_IN_CONTROL
        : QualityControlRun.EVALUATION_OUT_OF_CONTROL;
  }

  private static void validateControlMaterial(
      StockLot lot, InventoryItem item, RecordQualityControlRunCommand command) {
    if (!StockLot.STATUS_AVAILABLE.equals(lot.status())
        || !InventoryItem.CLASSIFICATION_CALIBRATOR_CONTROL_MATERIAL.equals(item.classification())) {
      throw new InventoryConflictException(
          "Control material lot is not eligible for internal quality control.",
          InventoryErrorCodes.QC_CONTROL_MATERIAL_LOT_INELIGIBLE);
    }
    if (!item.tenantId().equals(command.tenantId())
        || !item.laboratoryId().equals(command.laboratoryId())
        || !item.branchId().equals(command.branchId())) {
      throw new InventoryConflictException(
          "Quality-control run is outside the control material scope.",
          InventoryErrorCodes.QC_SCOPE_MISMATCH);
    }
  }

  private static String requireText(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new InvalidInventoryCommandException(
          message, InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
    return value;
  }

  public record RecordQualityControlRunCommand(
      String tenantId,
      String laboratoryId,
      String branchId,
      String testDefinitionId,
      String controlMaterialStockLotId,
      BigDecimal measuredValue,
      BigDecimal expectedMin,
      BigDecimal expectedMax,
      List<String> linkedLaboratoryResultIds,
      String performedBy,
      LocalDateTime performedAt,
      String evidenceReference) {}

  public record OverrideAcceptanceDecisionCommand(
      String acceptanceDecision,
      String overrideReason,
      String supervisorId,
      boolean supervisorScoped) {}
}
