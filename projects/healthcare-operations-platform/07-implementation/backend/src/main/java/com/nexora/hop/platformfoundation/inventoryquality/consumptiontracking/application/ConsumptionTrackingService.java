package com.nexora.hop.platformfoundation.inventoryquality.consumptiontracking.application;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.inventoryquality.consumptiontracking.domain.ConsumptionRecord;
import com.nexora.hop.platformfoundation.inventoryquality.consumptiontracking.domain.ConsumptionRepository;
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
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * BCM-INV-007 Consumption Tracking. Applies a diagnostic-order-driven or QC-driven consumption
 * decrement. Enforces RN-001 (reagent item must have a reagent profile) and RN-004 (lot must be
 * available). RN-002 quantity-vs-onhand guard is enforced via the shared lot-remaining check when
 * a lot is provided, otherwise via the InventoryItem stockSummary invariant.
 */
@Service
public class ConsumptionTrackingService {

  private static final Set<String> VALID_CONTEXTS =
      Set.of(
          ConsumptionRecord.CONTEXT_TEST_PROCESSING,
          ConsumptionRecord.CONTEXT_INTERNAL_QC,
          ConsumptionRecord.CONTEXT_CALIBRATION);

  private final ConsumptionRepository repository;
  private final InventoryItemService inventoryItemService;
  private final LotManagementService lotManagementService;
  private final AuditRecorder auditRecorder;
  private final Clock clock;

  @Autowired
  public ConsumptionTrackingService(
      ConsumptionRepository repository,
      InventoryItemService inventoryItemService,
      LotManagementService lotManagementService,
      AuditRecorder auditRecorder) {
    this(repository, inventoryItemService, lotManagementService, auditRecorder, Clock.systemUTC());
  }

  ConsumptionTrackingService(
      ConsumptionRepository repository,
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

  public ConsumptionRecord applyConsumption(ApplyConsumptionCommand command) {
    if (command.consumptionContext() == null
        || !VALID_CONTEXTS.contains(command.consumptionContext())) {
      throw new InvalidInventoryCommandException(
          "Consumption context is invalid. Allowed: " + VALID_CONTEXTS,
          InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
    BigDecimal quantity = command.consumedQuantity();
    if (quantity == null || quantity.signum() <= 0) {
      throw new InvalidInventoryCommandException(
          "Consumed quantity must be positive.",
          InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
    String actor = requireText(command.actorId(), "Actor id is required.");
    InventoryItem item = inventoryItemService.requireItem(command.inventoryItemId());

    // RN-001: a reagent-typed item requires a reagent profile before consumption.
    if (InventoryItem.ITEM_TYPE_REAGENT.equals(item.itemType()) && item.reagentProfile() == null) {
      throw new InventoryConflictException(
          "Reagent item has no reagent profile assigned yet.",
          InventoryErrorCodes.CONSUMPTION_REAGENT_PROFILE_MISSING);
    }

    StockLot lot = null;
    if (command.stockLotId() != null && !command.stockLotId().isBlank()) {
      lot = lotManagementService.requireLot(command.stockLotId());
      if (!lot.inventoryItemId().equals(item.inventoryItemId())) {
        throw new InventoryConflictException(
            "Lot does not belong to the specified inventory item.",
            InventoryErrorCodes.CONSUMPTION_SCOPE_MISMATCH);
      }
      if (!lot.canBeConsumed()) {
        throw new InventoryConflictException(
            "Lot is not eligible for consumption (status=" + lot.status() + ").",
            InventoryErrorCodes.CONSUMPTION_LOT_NOT_ELIGIBLE);
      }
      if (lot.remainingQuantity().compareTo(quantity) < 0) {
        throw new InventoryConflictException(
            "Consumption quantity exceeds lot remaining quantity.",
            InventoryErrorCodes.CONSUMPTION_LOT_NOT_ELIGIBLE);
      }
    }

    // INV-CAT-002: onHandQuantity must never go negative.
    if (item.stockSummary().onHandQuantity().compareTo(quantity) < 0) {
      throw new InventoryConflictException(
          "Consumption would drive on-hand quantity below zero.",
          InventoryErrorCodes.CONSUMPTION_LOT_NOT_ELIGIBLE);
    }

    LocalDateTime now = LocalDateTime.now(clock);
    ConsumptionRecord record =
        new ConsumptionRecord(
            newId(),
            item.inventoryItemId(),
            lot == null ? null : lot.stockLotId(),
            item.tenantId(),
            item.laboratoryId(),
            item.branchId(),
            command.diagnosticOrderId(),
            command.testDefinitionId(),
            quantity,
            command.consumptionContext(),
            now,
            actor,
            now);
    ConsumptionRecord saved = repository.save(record);
    inventoryItemService.save(
        item.withStockSummary(
            item.stockSummary().withOnHandDelta(quantity.negate(), now),
            new AuditMetadata(item.audit().createdBy(), item.audit().createdAt(), actor, now)));
    if (lot != null) {
      lotManagementService.save(
          lot.withRemainingQuantity(
              lot.remainingQuantity().subtract(quantity),
              new AuditMetadata(lot.audit().createdBy(), lot.audit().createdAt(), actor, now)));
    }
    auditRecorder.recordSystemEvent(
        item.tenantId(),
        "ConsumptionApplied",
        "InventoryItem",
        item.inventoryItemId(),
        "{\"consumptionRecordId\":\"%s\",\"consumedQuantity\":\"%s\",\"context\":\"%s\"}"
            .formatted(saved.consumptionRecordId(), quantity.toPlainString(), command.consumptionContext()));
    return saved;
  }

  public List<ConsumptionRecord> listConsumption(String tenantId, String laboratoryId, String branchId) {
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

  public record ApplyConsumptionCommand(
      String inventoryItemId,
      String stockLotId,
      String diagnosticOrderId,
      String testDefinitionId,
      BigDecimal consumedQuantity,
      String consumptionContext,
      String actorId) {}
}
