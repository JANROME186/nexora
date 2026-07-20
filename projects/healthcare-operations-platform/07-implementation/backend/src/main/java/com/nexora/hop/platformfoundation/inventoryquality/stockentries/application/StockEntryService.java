package com.nexora.hop.platformfoundation.inventoryquality.stockentries.application;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.application.LotManagementService;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.StockLot;
import com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.application.ProcurementManagementService;
import com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.domain.PurchaseOrder;
import com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.domain.PurchaseOrderLine;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.application.InventoryItemService;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InvalidInventoryCommandException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryConflictException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryEntityNotFoundException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryErrorCodes;
import com.nexora.hop.platformfoundation.inventoryquality.stockentries.domain.StockEntryRecord;
import com.nexora.hop.platformfoundation.inventoryquality.stockentries.domain.StockEntryRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * BCM-INV-005 Stock Entries. Applies an inventory receipt: increments the item's
 * stockSummary.onHandQuantity and, when a lot is provided, the lot's remainingQuantity. RN-002
 * cross-validates against a referenced purchase-order line and delegates the line-receipt state
 * transition back to BCM-INV-004 so purchase-order state remains authoritative there.
 */
@Service
public class StockEntryService {

  private static final Set<String> VALID_ENTRY_TYPES =
      Set.of(
          StockEntryRecord.ENTRY_TYPE_PURCHASE_RECEIPT,
          StockEntryRecord.ENTRY_TYPE_TRANSFER_IN,
          StockEntryRecord.ENTRY_TYPE_INITIAL_LOAD,
          StockEntryRecord.ENTRY_TYPE_RETURN);

  private final StockEntryRepository repository;
  private final InventoryItemService inventoryItemService;
  private final LotManagementService lotManagementService;
  private final ProcurementManagementService procurementService;
  private final AuditRecorder auditRecorder;
  private final Clock clock;

  @Autowired
  public StockEntryService(
      StockEntryRepository repository,
      InventoryItemService inventoryItemService,
      LotManagementService lotManagementService,
      @Lazy ProcurementManagementService procurementService,
      AuditRecorder auditRecorder) {
    this(
        repository,
        inventoryItemService,
        lotManagementService,
        procurementService,
        auditRecorder,
        Clock.systemUTC());
  }

  StockEntryService(
      StockEntryRepository repository,
      InventoryItemService inventoryItemService,
      LotManagementService lotManagementService,
      ProcurementManagementService procurementService,
      AuditRecorder auditRecorder,
      Clock clock) {
    this.repository = repository;
    this.inventoryItemService = inventoryItemService;
    this.lotManagementService = lotManagementService;
    this.procurementService = procurementService;
    this.auditRecorder = auditRecorder;
    this.clock = clock;
  }

  public StockEntryRecord applyStockReceipt(ApplyStockReceiptCommand command) {
    InventoryItem item =
        inventoryItemService.requireActiveItem(
            command.inventoryItemId(), InventoryErrorCodes.STOCK_ENTRY_ITEM_DISCONTINUED);
    validateEntryType(command.entryType());
    BigDecimal quantity = command.quantity();
    if (quantity == null || quantity.signum() <= 0) {
      throw new InvalidInventoryCommandException(
          "Stock entry quantity must be positive.",
          InventoryErrorCodes.STOCK_ENTRY_QUANTITY_INVALID);
    }
    String actor = requireText(command.actorId(), "Actor id is required.");
    LocalDateTime now = LocalDateTime.now(clock);

    StockLot lot = null;
    if (command.stockLotId() != null && !command.stockLotId().isBlank()) {
      lot = lotManagementService.requireLot(command.stockLotId());
      if (!lot.inventoryItemId().equals(item.inventoryItemId())) {
        throw new InventoryConflictException(
            "Stock lot does not belong to the specified inventory item.",
            InventoryErrorCodes.STOCK_ENTRY_SCOPE_MISMATCH);
      }
    }

    if (command.purchaseOrderLineId() != null && !command.purchaseOrderLineId().isBlank()) {
      if (command.purchaseOrderId() == null || command.purchaseOrderId().isBlank()) {
        throw new InvalidInventoryCommandException(
            "purchaseOrderId is required when receiving against a purchase order line.",
            InventoryErrorCodes.STOCK_ENTRY_PURCHASE_ORDER_LINE_INVALID);
      }
      procurementService.recordLineReceipt(
          command.purchaseOrderId(), command.purchaseOrderLineId(), quantity, actor);
    }

    StockEntryRecord record =
        new StockEntryRecord(
            newId(),
            item.inventoryItemId(),
            lot == null ? null : lot.stockLotId(),
            item.tenantId(),
            item.laboratoryId(),
            item.branchId(),
            command.purchaseOrderLineId(),
            quantity,
            command.entryType(),
            command.reasonCode(),
            now,
            actor,
            now);
    StockEntryRecord saved = repository.save(record);

    inventoryItemService.save(
        item.withStockSummary(
            item.stockSummary().withOnHandDelta(quantity, now),
            new AuditMetadata(item.audit().createdBy(), item.audit().createdAt(), actor, now)));
    if (lot != null) {
      lotManagementService.save(
          lot.withRemainingQuantity(
              lot.remainingQuantity().add(quantity),
              new AuditMetadata(lot.audit().createdBy(), lot.audit().createdAt(), actor, now)));
    }
    auditRecorder.recordSystemEvent(
        item.tenantId(),
        "StockEntryApplied",
        "InventoryItem",
        item.inventoryItemId(),
        "{\"stockEntryId\":\"%s\",\"quantity\":\"%s\",\"entryType\":\"%s\"}"
            .formatted(saved.stockEntryId(), quantity.toPlainString(), command.entryType()));
    return saved;
  }

  /** Convenience used by BCM-INV-004's Receive endpoint. */
  public PurchaseOrder applyReceiptForPurchaseOrderLine(
      String purchaseOrderId,
      String purchaseOrderLineId,
      BigDecimal receivedNow,
      String stockLotId,
      String actorId) {
    PurchaseOrder order = procurementService.getOrder(purchaseOrderId);
    PurchaseOrderLine line =
        order.lines().stream()
            .filter(l -> l.purchaseOrderLineId().equals(purchaseOrderLineId))
            .findFirst()
            .orElseThrow(
                () ->
                    new InventoryEntityNotFoundException(
                        "Purchase order line was not found.",
                        InventoryErrorCodes.PURCHASE_ORDER_LINE_NOT_FOUND));
    applyStockReceipt(
        new ApplyStockReceiptCommand(
            line.inventoryItemId(),
            stockLotId,
            purchaseOrderId,
            purchaseOrderLineId,
            receivedNow,
            StockEntryRecord.ENTRY_TYPE_PURCHASE_RECEIPT,
            null,
            actorId));
    return procurementService.getOrder(purchaseOrderId);
  }

  public List<StockEntryRecord> listEntries(String tenantId, String laboratoryId, String branchId) {
    return repository.findByScope(
        requireText(tenantId, "Tenant id is required."),
        requireText(laboratoryId, "Laboratory id is required."),
        requireText(branchId, "Branch id is required."));
  }

  private static void validateEntryType(String entryType) {
    if (entryType == null || !VALID_ENTRY_TYPES.contains(entryType)) {
      throw new InvalidInventoryCommandException(
          "Entry type is invalid. Allowed: " + VALID_ENTRY_TYPES,
          InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
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

  public record ApplyStockReceiptCommand(
      String inventoryItemId,
      String stockLotId,
      String purchaseOrderId,
      String purchaseOrderLineId,
      BigDecimal quantity,
      String entryType,
      String reasonCode,
      String actorId) {}
}
