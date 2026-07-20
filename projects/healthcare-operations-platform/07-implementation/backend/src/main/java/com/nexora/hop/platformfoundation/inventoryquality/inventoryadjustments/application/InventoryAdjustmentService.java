package com.nexora.hop.platformfoundation.inventoryquality.inventoryadjustments.application;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.inventoryquality.inventoryadjustments.domain.AdjustmentRecord;
import com.nexora.hop.platformfoundation.inventoryquality.inventoryadjustments.domain.AdjustmentRepository;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.application.LotManagementService;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.StockLot;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.application.InventoryItemService;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InvalidInventoryCommandException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryConflictException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryErrorCodes;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * BCM-INV-008 Inventory Adjustments. Real-time negative-quantity guard (RN-001) and mandatory
 * dual-actor approval (RN-002 — requester and approver must be distinct actors). Adjustment
 * delta may be positive (correction upward, e.g. found stock) or negative (correction downward,
 * e.g. inventory shrinkage).
 */
@Service
public class InventoryAdjustmentService {

  private final AdjustmentRepository repository;
  private final InventoryItemService inventoryItemService;
  private final LotManagementService lotManagementService;
  private final AuditRecorder auditRecorder;
  private final Clock clock;

  @Autowired
  public InventoryAdjustmentService(
      AdjustmentRepository repository,
      InventoryItemService inventoryItemService,
      LotManagementService lotManagementService,
      AuditRecorder auditRecorder) {
    this(repository, inventoryItemService, lotManagementService, auditRecorder, Clock.systemUTC());
  }

  InventoryAdjustmentService(
      AdjustmentRepository repository,
      InventoryItemService inventoryItemService,
      LotManagementService lotManagementService,
      AuditRecorder auditRecorder,
      Clock clock) {
    this.repository = repository;
    this.inventoryItemService = inventoryItemService;
    this.lotManagementService = lotManagementService;
    this.auditRecorder = auditRecorder;
    this.clock = clock;
  }

  public AdjustmentRecord applyAdjustment(ApplyAdjustmentCommand command) {
    BigDecimal delta = command.deltaQuantity();
    if (delta == null || delta.signum() == 0) {
      throw new InvalidInventoryCommandException(
          "Adjustment delta quantity is required and must be non-zero.",
          InventoryErrorCodes.ADJUSTMENT_QUANTITY_INVALID);
    }
    String reasonCode = command.reasonCode();
    if (reasonCode == null || reasonCode.isBlank()) {
      throw new InvalidInventoryCommandException(
          "Adjustment reason code is required.",
          InventoryErrorCodes.ADJUSTMENT_REASON_CODE_REQUIRED);
    }
    String requester = requireText(command.requestedBy(), "Requested-by actor is required.");
    String approver = requireText(command.approverId(), "Approver id is required.");
    if (Objects.equals(requester, approver)) {
      throw new InventoryConflictException(
          "Adjustment approver must not be the same actor as the requester.",
          InventoryErrorCodes.ADJUSTMENT_APPROVER_SAME_AS_REQUESTER);
    }
    String actor = requireText(command.actorId(), "Actor id is required.");

    InventoryItem item = inventoryItemService.requireItem(command.inventoryItemId());
    StockLot lot = null;
    if (command.stockLotId() != null && !command.stockLotId().isBlank()) {
      lot = lotManagementService.requireLot(command.stockLotId());
      if (!lot.inventoryItemId().equals(item.inventoryItemId())) {
        throw new InventoryConflictException(
            "Lot does not belong to the specified inventory item.",
            InventoryErrorCodes.ADJUSTMENT_SCOPE_MISMATCH);
      }
    }

    // INV-CAT-002: onHandQuantity must never go negative.
    if (item.stockSummary().onHandQuantity().add(delta).signum() < 0) {
      throw new InventoryConflictException(
          "Adjustment would drive on-hand quantity below zero.",
          InventoryErrorCodes.ADJUSTMENT_QUANTITY_INVALID);
    }
    if (lot != null && lot.remainingQuantity().add(delta).signum() < 0) {
      throw new InventoryConflictException(
          "Adjustment would drive lot remaining quantity below zero.",
          InventoryErrorCodes.ADJUSTMENT_QUANTITY_INVALID);
    }
    LocalDateTime now = LocalDateTime.now(clock);
    AdjustmentRecord record =
        new AdjustmentRecord(
            newId(),
            item.inventoryItemId(),
            lot == null ? null : lot.stockLotId(),
            item.tenantId(),
            item.laboratoryId(),
            item.branchId(),
            delta,
            reasonCode,
            command.reasonNote(),
            approver,
            requester,
            now,
            actor,
            now);
    AdjustmentRecord saved = repository.save(record);
    inventoryItemService.save(
        item.withStockSummary(
            item.stockSummary().withOnHandDelta(delta, now),
            new AuditMetadata(item.audit().createdBy(), item.audit().createdAt(), actor, now)));
    if (lot != null) {
      lotManagementService.save(
          lot.withRemainingQuantity(
              lot.remainingQuantity().add(delta),
              new AuditMetadata(lot.audit().createdBy(), lot.audit().createdAt(), actor, now)));
    }
    auditRecorder.recordSystemEvent(
        item.tenantId(),
        "InventoryAdjustmentApplied",
        "InventoryItem",
        item.inventoryItemId(),
        "{\"adjustmentId\":\"%s\",\"delta\":\"%s\",\"reasonCode\":\"%s\",\"approverId\":\"%s\"}"
            .formatted(saved.adjustmentId(), delta.toPlainString(), reasonCode, approver));
    return saved;
  }

  public List<AdjustmentRecord> listAdjustments(String tenantId, String laboratoryId, String branchId) {
    return repository.findByScope(
        requireText(tenantId, "Tenant id is required."),
        requireText(laboratoryId, "Laboratory id is required."),
        requireText(branchId, "Branch id is required."));
  }

  private static String requireText(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new InvalidInventoryCommandException(
          message, InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
    return value;
  }

  private static String newId() {
    return UUID.randomUUID().toString();
  }

  public record ApplyAdjustmentCommand(
      String inventoryItemId,
      String stockLotId,
      BigDecimal deltaQuantity,
      String reasonCode,
      String reasonNote,
      String requestedBy,
      String approverId,
      String actorId) {}
}
