package com.nexora.hop.platformfoundation.inventoryquality.stockexits.application;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.application.LotManagementService;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.StockLot;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.application.InventoryItemService;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InvalidInventoryCommandException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryConflictException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryErrorCodes;
import com.nexora.hop.platformfoundation.inventoryquality.stockexits.domain.StockExitRecord;
import com.nexora.hop.platformfoundation.inventoryquality.stockexits.domain.StockExitRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** BCM-INV-006 Stock Exits. Real-time remainingQuantity/status guard against the referenced StockLot. */
@Service
public class StockExitService {

  private static final Set<String> VALID_EXIT_TYPES =
      Set.of(
          StockExitRecord.EXIT_TYPE_INTER_BRANCH_TRANSFER,
          StockExitRecord.EXIT_TYPE_RETURN_TO_SUPPLIER,
          StockExitRecord.EXIT_TYPE_INTERNAL_RELOCATION);

  private final StockExitRepository repository;
  private final InventoryItemService inventoryItemService;
  private final LotManagementService lotManagementService;
  private final AuditRecorder auditRecorder;
  private final Clock clock;

  @Autowired
  public StockExitService(
      StockExitRepository repository,
      InventoryItemService inventoryItemService,
      LotManagementService lotManagementService,
      AuditRecorder auditRecorder) {
    this(repository, inventoryItemService, lotManagementService, auditRecorder, Clock.systemUTC());
  }

  StockExitService(
      StockExitRepository repository,
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

  public StockExitRecord applyStockExit(ApplyStockExitCommand command) {
    String exitType = command.exitType();
    if (exitType == null || !VALID_EXIT_TYPES.contains(exitType)) {
      throw new InvalidInventoryCommandException(
          "Exit type is invalid. Allowed: " + VALID_EXIT_TYPES,
          InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
    BigDecimal quantity = command.quantity();
    if (quantity == null || quantity.signum() <= 0) {
      throw new InvalidInventoryCommandException(
          "Exit quantity must be positive.",
          InventoryErrorCodes.STOCK_EXIT_QUANTITY_EXCEEDS_LOT);
    }
    if (StockExitRecord.EXIT_TYPE_INTER_BRANCH_TRANSFER.equals(exitType)
        && (command.destinationBranchId() == null || command.destinationBranchId().isBlank())) {
      throw new InvalidInventoryCommandException(
          "Destination branch is required for inter-branch transfers.",
          InventoryErrorCodes.STOCK_EXIT_DESTINATION_BRANCH_REQUIRED);
    }
    String actor = requireText(command.actorId(), "Actor id is required.");

    InventoryItem item = inventoryItemService.requireItem(command.inventoryItemId());
    StockLot lot = lotManagementService.requireLot(command.stockLotId());
    if (!lot.inventoryItemId().equals(item.inventoryItemId())) {
      throw new InventoryConflictException(
          "Stock lot does not belong to the specified inventory item.",
          InventoryErrorCodes.STOCK_EXIT_SCOPE_MISMATCH);
    }
    if (!lot.canBeConsumed()) {
      throw new InventoryConflictException(
          "Stock lot is not eligible for exit (status=" + lot.status() + ").",
          InventoryErrorCodes.STOCK_EXIT_LOT_NOT_ELIGIBLE);
    }
    if (lot.remainingQuantity().compareTo(quantity) < 0) {
      throw new InventoryConflictException(
          "Exit quantity exceeds lot remaining quantity.",
          InventoryErrorCodes.STOCK_EXIT_QUANTITY_EXCEEDS_LOT);
    }
    LocalDateTime now = LocalDateTime.now(clock);
    StockExitRecord record =
        new StockExitRecord(
            newId(),
            item.inventoryItemId(),
            lot.stockLotId(),
            item.tenantId(),
            item.laboratoryId(),
            item.branchId(),
            command.destinationBranchId(),
            quantity,
            exitType,
            command.reasonCode(),
            now,
            actor,
            now);
    StockExitRecord saved = repository.save(record);
    inventoryItemService.save(
        item.withStockSummary(
            item.stockSummary().withOnHandDelta(quantity.negate(), now),
            new AuditMetadata(item.audit().createdBy(), item.audit().createdAt(), actor, now)));
    lotManagementService.save(
        lot.withRemainingQuantity(
            lot.remainingQuantity().subtract(quantity),
            new AuditMetadata(lot.audit().createdBy(), lot.audit().createdAt(), actor, now)));
    auditRecorder.recordSystemEvent(
        item.tenantId(),
        "StockExitApplied",
        "InventoryItem",
        item.inventoryItemId(),
        "{\"stockExitId\":\"%s\",\"quantity\":\"%s\",\"exitType\":\"%s\"}"
            .formatted(saved.stockExitId(), quantity.toPlainString(), exitType));
    return saved;
  }

  public List<StockExitRecord> listExits(String tenantId, String laboratoryId, String branchId) {
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

  public record ApplyStockExitCommand(
      String inventoryItemId,
      String stockLotId,
      String destinationBranchId,
      BigDecimal quantity,
      String exitType,
      String reasonCode,
      String actorId) {}
}
