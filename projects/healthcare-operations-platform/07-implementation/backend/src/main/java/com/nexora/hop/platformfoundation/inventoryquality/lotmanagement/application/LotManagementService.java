package com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.application;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.StockLot;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.StockLotRepository;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.SupplierSnapshot;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.application.InventoryItemService;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InvalidInventoryCommandException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryConflictException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryEntityNotFoundException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryErrorCodes;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * BCM-INV-003 Lot Management. Compiles registerStockLot/listStockLots/quarantineStockLot/
 * expireStockLot per bcm-inv-003-lot-management/openapi-source.yaml. Enforces RN-001
 * (remainingQuantity cannot go negative), RN-004 (a disposed/expired lot cannot transition
 * further) and the delegated ownership boundary — this service is the single write path for
 * StockLot state.
 */
@Service
public class LotManagementService {

  private final StockLotRepository lotRepository;
  private final InventoryItemService inventoryItemService;
  private final AuditRecorder auditRecorder;
  private final Clock clock;

  @Autowired
  public LotManagementService(
      StockLotRepository lotRepository,
      InventoryItemService inventoryItemService,
      AuditRecorder auditRecorder) {
    this(lotRepository, inventoryItemService, auditRecorder, Clock.systemUTC());
  }

  LotManagementService(
      StockLotRepository lotRepository,
      InventoryItemService inventoryItemService,
      AuditRecorder auditRecorder,
      Clock clock) {
    this.lotRepository = lotRepository;
    this.inventoryItemService = inventoryItemService;
    this.auditRecorder = auditRecorder;
    this.clock = clock;
  }

  public StockLot registerLot(String inventoryItemId, RegisterStockLotCommand command) {
    InventoryItem item =
        inventoryItemService.requireActiveItem(
            inventoryItemId, InventoryErrorCodes.INVENTORY_ITEM_DISCONTINUED);
    String lotNumber = requireText(command.lotNumber(), "Lot number is required.");
    BigDecimal received = command.receivedQuantity();
    if (received == null || received.signum() <= 0) {
      throw new InvalidInventoryCommandException(
          "Received quantity must be a positive number.",
          InventoryErrorCodes.LOT_QUANTITY_INVARIANT_VIOLATION);
    }
    lotRepository
        .findByInventoryItemIdAndLotNumber(inventoryItemId, lotNumber)
        .ifPresent(
            existing -> {
              throw new InventoryConflictException(
                  "Lot number " + lotNumber + " already exists for this item.",
                  InventoryErrorCodes.LOT_QUANTITY_INVARIANT_VIOLATION);
            });
    String actor = requireText(command.actorId(), "Actor id is required.");
    LocalDateTime now = LocalDateTime.now(clock);
    SupplierSnapshot supplier =
        command.supplierId() == null && command.supplierName() == null
            ? null
            : new SupplierSnapshot(command.supplierId(), command.supplierName());
    StockLot lot =
        new StockLot(
            newId(),
            inventoryItemId,
            item.tenantId(),
            item.laboratoryId(),
            item.branchId(),
            lotNumber,
            supplier,
            command.expirationDate(),
            received,
            received,
            StockLot.STATUS_AVAILABLE,
            new AuditMetadata(actor, now, actor, now));
    StockLot saved = lotRepository.save(lot);
    // Delegated stockSummary mutation: registering a lot with a receivedQuantity records the
    // arrival of that many units into InventoryItem.stockSummary.onHandQuantity. BCM-INV-005
    // StockEntries covers subsequent lot-less or non-purchase-receipt increases.
    inventoryItemService.save(
        item.withStockSummary(
            item.stockSummary().withOnHandDelta(received, now),
            new AuditMetadata(item.audit().createdBy(), item.audit().createdAt(), actor, now)));
    auditRecorder.recordSystemEvent(
        item.tenantId(),
        "StockLotRegistered",
        "StockLot",
        saved.stockLotId(),
        "{\"inventoryItemId\":\"%s\",\"lotNumber\":\"%s\",\"receivedQuantity\":\"%s\"}"
            .formatted(inventoryItemId, lotNumber, received.toPlainString()));
    return saved;
  }

  public List<StockLot> listLots(String inventoryItemId) {
    inventoryItemService.requireItem(inventoryItemId);
    return lotRepository.findByInventoryItemId(inventoryItemId);
  }

  public StockLot quarantineLot(String stockLotId, String actorId) {
    StockLot current = requireLot(stockLotId);
    if (current.isTerminal()) {
      throw new InventoryConflictException(
          "Lot " + stockLotId + " is in a terminal state and cannot be quarantined.",
          InventoryErrorCodes.LOT_DISPOSED_TRANSITION_FORBIDDEN);
    }
    StockLot updated = current.withStatus(StockLot.STATUS_QUARANTINED, touched(current.audit(), actorId));
    StockLot saved = lotRepository.save(updated);
    auditRecorder.recordSystemEvent(
        saved.tenantId(), "StockLotQuarantined", "StockLot", saved.stockLotId(), "{}");
    return saved;
  }

  /**
   * BCM-INV-003 expireStockLot (custom because it is driven by a scheduled sweep). Kept as an
   * explicit manual endpoint for BE-001; a scheduler will be added in a later backlog item.
   */
  public StockLot expireLot(String stockLotId, String actorId) {
    StockLot current = requireLot(stockLotId);
    if (StockLot.STATUS_DISPOSED.equals(current.status())) {
      throw new InventoryConflictException(
          "Lot " + stockLotId + " was already disposed.",
          InventoryErrorCodes.LOT_DISPOSED_TRANSITION_FORBIDDEN);
    }
    StockLot updated = current.withStatus(StockLot.STATUS_EXPIRED, touched(current.audit(), actorId));
    StockLot saved = lotRepository.save(updated);
    auditRecorder.recordSystemEvent(
        saved.tenantId(), "StockLotExpired", "StockLot", saved.stockLotId(), "{}");
    return saved;
  }

  /** Delegated write path used by movement services (exits, consumption, adjustments, waste). */
  public StockLot save(StockLot lot) {
    return lotRepository.save(lot);
  }

  public StockLot requireLot(String stockLotId) {
    return lotRepository
        .findById(requireText(stockLotId, "Stock lot id is required."))
        .orElseThrow(
            () ->
                new InventoryEntityNotFoundException(
                    "Stock lot was not found.", InventoryErrorCodes.STOCK_LOT_NOT_FOUND));
  }

  private static AuditMetadata touched(AuditMetadata audit, String actorId) {
    return new AuditMetadata(
        audit.createdBy(),
        audit.createdAt(),
        requireText(actorId, "Actor id is required."),
        LocalDateTime.now());
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

  public record RegisterStockLotCommand(
      String lotNumber,
      String supplierId,
      String supplierName,
      LocalDate expirationDate,
      BigDecimal receivedQuantity,
      String actorId) {}
}
