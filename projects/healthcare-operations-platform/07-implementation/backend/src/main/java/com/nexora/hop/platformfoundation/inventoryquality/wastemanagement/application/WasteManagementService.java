package com.nexora.hop.platformfoundation.inventoryquality.wastemanagement.application;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.application.LotManagementService;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.StockLot;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.application.InventoryItemService;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InvalidInventoryCommandException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryConflictException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryErrorCodes;
import com.nexora.hop.platformfoundation.inventoryquality.wastemanagement.domain.WasteRecord;
import com.nexora.hop.platformfoundation.inventoryquality.wastemanagement.domain.WasteRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * BCM-INV-009 Waste Management. Decrements the referenced lot's remainingQuantity and the
 * InventoryItem's stockSummary. If disposing the entire remaining lot quantity, transitions the
 * lot to disposed (conditional cross-entity status transition per openapi-source.md
 * custom_reason).
 */
@Service
public class WasteManagementService {

  private final WasteRepository repository;
  private final InventoryItemService inventoryItemService;
  private final LotManagementService lotManagementService;
  private final AuditRecorder auditRecorder;
  private final Clock clock;

  @Autowired
  public WasteManagementService(
      WasteRepository repository,
      InventoryItemService inventoryItemService,
      LotManagementService lotManagementService,
      AuditRecorder auditRecorder) {
    this(repository, inventoryItemService, lotManagementService, auditRecorder, Clock.systemUTC());
  }

  WasteManagementService(
      WasteRepository repository,
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

  public WasteRecord applyWasteDisposal(ApplyWasteCommand command) {
    BigDecimal quantity = command.disposedQuantity();
    if (quantity == null || quantity.signum() <= 0) {
      throw new InvalidInventoryCommandException(
          "Disposal quantity must be positive.",
          InventoryErrorCodes.WASTE_QUANTITY_EXCEEDS_LOT);
    }
    String reasonCode = command.reasonCode();
    if (reasonCode == null || reasonCode.isBlank()) {
      throw new InvalidInventoryCommandException(
          "Waste reason code is required.", InventoryErrorCodes.WASTE_REASON_CODE_REQUIRED);
    }
    String actor = requireText(command.actorId(), "Actor id is required.");

    InventoryItem item = inventoryItemService.requireItem(command.inventoryItemId());
    StockLot lot = lotManagementService.requireLot(command.stockLotId());
    if (!lot.inventoryItemId().equals(item.inventoryItemId())) {
      throw new InventoryConflictException(
          "Lot does not belong to the specified inventory item.",
          InventoryErrorCodes.WASTE_SCOPE_MISMATCH);
    }
    if (lot.remainingQuantity().compareTo(quantity) < 0) {
      throw new InventoryConflictException(
          "Disposal quantity exceeds lot remaining quantity.",
          InventoryErrorCodes.WASTE_QUANTITY_EXCEEDS_LOT);
    }

    LocalDateTime now = LocalDateTime.now(clock);
    WasteRecord record =
        new WasteRecord(
            newId(),
            item.inventoryItemId(),
            lot.stockLotId(),
            item.tenantId(),
            item.laboratoryId(),
            item.branchId(),
            quantity,
            reasonCode,
            command.reasonNote(),
            now,
            actor,
            now);
    WasteRecord saved = repository.save(record);
    inventoryItemService.save(
        item.withStockSummary(
            item.stockSummary().withOnHandDelta(quantity.negate(), now),
            new AuditMetadata(item.audit().createdBy(), item.audit().createdAt(), actor, now)));
    BigDecimal newRemaining = lot.remainingQuantity().subtract(quantity);
    AuditMetadata lotAudit =
        new AuditMetadata(lot.audit().createdBy(), lot.audit().createdAt(), actor, now);
    StockLot updatedLot = lot.withRemainingQuantity(newRemaining, lotAudit);
    if (newRemaining.signum() == 0) {
      updatedLot = updatedLot.withStatus(StockLot.STATUS_DISPOSED, lotAudit);
    }
    lotManagementService.save(updatedLot);
    auditRecorder.recordSystemEvent(
        item.tenantId(),
        "WasteDisposalApplied",
        "InventoryItem",
        item.inventoryItemId(),
        "{\"wasteRecordId\":\"%s\",\"quantity\":\"%s\",\"reasonCode\":\"%s\"}"
            .formatted(saved.wasteRecordId(), quantity.toPlainString(), reasonCode));
    return saved;
  }

  public List<WasteRecord> listWaste(String tenantId, String laboratoryId, String branchId) {
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

  public record ApplyWasteCommand(
      String inventoryItemId,
      String stockLotId,
      BigDecimal disposedQuantity,
      String reasonCode,
      String reasonNote,
      String actorId) {}
}
