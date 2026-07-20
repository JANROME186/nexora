package com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.application;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.SupplierSnapshot;
import com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.domain.PurchaseOrder;
import com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.domain.PurchaseOrderLine;
import com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.domain.PurchaseOrderRepository;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.application.InventoryItemService;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InvalidInventoryCommandException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryConflictException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryEntityNotFoundException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryErrorCodes;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** BCM-INV-004 Procurement Management. Compiles the full purchase-order lifecycle. */
@Service
public class ProcurementManagementService {

  private final PurchaseOrderRepository repository;
  private final InventoryItemService inventoryItemService;
  private final TenantDirectory tenantDirectory;
  private final AuditRecorder auditRecorder;
  private final Clock clock;

  @Autowired
  public ProcurementManagementService(
      PurchaseOrderRepository repository,
      InventoryItemService inventoryItemService,
      TenantDirectory tenantDirectory,
      AuditRecorder auditRecorder) {
    this(repository, inventoryItemService, tenantDirectory, auditRecorder, Clock.systemUTC());
  }

  ProcurementManagementService(
      PurchaseOrderRepository repository,
      InventoryItemService inventoryItemService,
      TenantDirectory tenantDirectory,
      AuditRecorder auditRecorder,
      Clock clock) {
    this.repository = repository;
    this.inventoryItemService = inventoryItemService;
    this.tenantDirectory = tenantDirectory;
    this.auditRecorder = auditRecorder;
    this.clock = clock;
  }

  public PurchaseOrder createOrder(CreatePurchaseOrderCommand command) {
    String tenantId = requireText(command.tenantId(), "Tenant id is required.");
    if (!tenantDirectory.tenantExists(tenantId)) {
      throw new InventoryEntityNotFoundException(
          "Tenant was not found.", InventoryErrorCodes.TENANT_NOT_FOUND);
    }
    String laboratoryId = requireText(command.laboratoryId(), "Laboratory id is required.");
    String branchId = requireText(command.branchId(), "Branch id is required.");
    String supplierId = requireText(command.supplierId(), "Supplier id is required.");
    String supplierName = requireText(command.supplierName(), "Supplier name is required.");
    String currencyCode = requireText(command.currencyCode(), "Currency code is required.");
    String actor = requireText(command.actorId(), "Actor id is required.");
    if (command.lines() == null || command.lines().isEmpty()) {
      throw new InvalidInventoryCommandException(
          "At least one purchase order line is required.",
          InventoryErrorCodes.PURCHASE_ORDER_LINE_ITEM_INVALID);
    }
    List<PurchaseOrderLine> lines = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;
    for (var lineCmd : command.lines()) {
      InventoryItem item =
          inventoryItemService.requireActiveItem(
              lineCmd.inventoryItemId(), InventoryErrorCodes.INVENTORY_ITEM_DISCONTINUED);
      if (!item.tenantId().equals(tenantId)
          || !item.laboratoryId().equals(laboratoryId)
          || !item.branchId().equals(branchId)) {
        throw new InventoryConflictException(
            "Line item " + item.inventoryItemId() + " is outside the order scope.",
            InventoryErrorCodes.PROCUREMENT_SCOPE_MISMATCH);
      }
      BigDecimal ordered = lineCmd.orderedQuantity();
      BigDecimal unitCost = lineCmd.unitCost();
      if (ordered == null || ordered.signum() <= 0 || unitCost == null || unitCost.signum() < 0) {
        throw new InvalidInventoryCommandException(
            "Line quantity must be positive and unit cost must be non-negative.",
            InventoryErrorCodes.PURCHASE_ORDER_LINE_QUANTITY_OR_COST_INVALID);
      }
      lines.add(
          new PurchaseOrderLine(
              newId(),
              lineCmd.inventoryItemId(),
              ordered,
              unitCost,
              BigDecimal.ZERO,
              PurchaseOrderLine.STATUS_PENDING));
      total = total.add(ordered.multiply(unitCost));
    }
    LocalDateTime now = LocalDateTime.now(clock);
    PurchaseOrder order =
        new PurchaseOrder(
            newId(),
            tenantId,
            laboratoryId,
            branchId,
            new SupplierSnapshot(supplierId, supplierName),
            PurchaseOrder.STATUS_DRAFT,
            List.copyOf(lines),
            total,
            currencyCode,
            null,
            null,
            new AuditMetadata(actor, now, actor, now));
    PurchaseOrder saved = repository.save(order);
    auditRecorder.recordSystemEvent(
        tenantId,
        "PurchaseOrderCreated",
        "PurchaseOrder",
        saved.purchaseOrderId(),
        "{\"lineCount\":%d}".formatted(saved.lines().size()));
    return saved;
  }

  public PurchaseOrder submitOrder(String purchaseOrderId, String actorId) {
    PurchaseOrder current = requireOrder(purchaseOrderId);
    if (!PurchaseOrder.STATUS_DRAFT.equals(current.status())) {
      throw new InventoryConflictException(
          "Only draft purchase orders can be submitted.",
          InventoryErrorCodes.PURCHASE_ORDER_TERMINAL_STATE);
    }
    for (PurchaseOrderLine line : current.lines()) {
      inventoryItemService.requireActiveItem(
          line.inventoryItemId(), InventoryErrorCodes.STOCK_ENTRY_ITEM_DISCONTINUED);
    }
    PurchaseOrder updated =
        current.withStatus(PurchaseOrder.STATUS_SUBMITTED, touched(current.audit(), actorId));
    PurchaseOrder saved = repository.save(updated);
    auditRecorder.recordSystemEvent(
        saved.tenantId(),
        "PurchaseOrderSubmitted",
        "PurchaseOrder",
        saved.purchaseOrderId(),
        "{}");
    return saved;
  }

  public PurchaseOrder approveOrder(String purchaseOrderId, String approverId) {
    PurchaseOrder current = requireOrder(purchaseOrderId);
    if (!PurchaseOrder.STATUS_SUBMITTED.equals(current.status())) {
      throw new InventoryConflictException(
          "Only submitted purchase orders can be approved.",
          InventoryErrorCodes.PURCHASE_ORDER_TERMINAL_STATE);
    }
    String approver = requireText(approverId, "Approver id is required.");
    PurchaseOrder updated = current.withApprover(approver, touched(current.audit(), approver));
    PurchaseOrder saved = repository.save(updated);
    auditRecorder.recordSystemEvent(
        saved.tenantId(),
        "PurchaseOrderApproved",
        "PurchaseOrder",
        saved.purchaseOrderId(),
        "{\"approverId\":\"%s\"}".formatted(approver));
    return saved;
  }

  public PurchaseOrder cancelOrder(String purchaseOrderId, String reason, String actorId) {
    PurchaseOrder current = requireOrder(purchaseOrderId);
    if (current.isTerminal() || PurchaseOrder.STATUS_RECEIVING.equals(current.status())) {
      throw new InventoryConflictException(
          "Purchase order is in a state (" + current.status() + ") that cannot be cancelled.",
          InventoryErrorCodes.PURCHASE_ORDER_TERMINAL_STATE);
    }
    String cancellationReason = requireText(reason, "Cancellation reason is required.");
    PurchaseOrder updated =
        current.withCancellation(cancellationReason, touched(current.audit(), actorId));
    PurchaseOrder saved = repository.save(updated);
    auditRecorder.recordSystemEvent(
        saved.tenantId(),
        "PurchaseOrderCancelled",
        "PurchaseOrder",
        saved.purchaseOrderId(),
        "{\"reason\":\"%s\"}".formatted(cancellationReason));
    return saved;
  }

  /**
   * Delegated receipt: BCM-INV-005 calls this to record a receipt against a line. This method
   * updates the line's receivedQuantity/lineStatus and advances the header to receiving/received
   * as appropriate. Does not itself mutate stock; that is BCM-INV-005's own responsibility.
   */
  public PurchaseOrder recordLineReceipt(
      String purchaseOrderId, String purchaseOrderLineId, BigDecimal receivedNow, String actorId) {
    PurchaseOrder current = requireOrder(purchaseOrderId);
    if (!current.canReceiveLines()) {
      throw new InventoryConflictException(
          "Purchase order " + purchaseOrderId + " is not in a state that accepts line receipts.",
          InventoryErrorCodes.PURCHASE_ORDER_TERMINAL_STATE);
    }
    PurchaseOrderLine line =
        current.lines().stream()
            .filter(l -> l.purchaseOrderLineId().equals(purchaseOrderLineId))
            .findFirst()
            .orElseThrow(
                () ->
                    new InventoryEntityNotFoundException(
                        "Purchase order line was not found.",
                        InventoryErrorCodes.PURCHASE_ORDER_LINE_NOT_FOUND));
    if (receivedNow == null || receivedNow.signum() <= 0) {
      throw new InvalidInventoryCommandException(
          "Received quantity must be positive.",
          InventoryErrorCodes.STOCK_ENTRY_QUANTITY_INVALID);
    }
    BigDecimal newTotalReceived = line.receivedQuantity().add(receivedNow);
    if (newTotalReceived.compareTo(line.orderedQuantity()) > 0) {
      throw new InventoryConflictException(
          "Received quantity would exceed the ordered quantity for this line.",
          InventoryErrorCodes.STOCK_ENTRY_PURCHASE_ORDER_LINE_INVALID);
    }
    String newStatus =
        newTotalReceived.compareTo(line.orderedQuantity()) == 0
            ? PurchaseOrderLine.STATUS_RECEIVED
            : PurchaseOrderLine.STATUS_PARTIALLY_RECEIVED;
    PurchaseOrderLine updatedLine = line.withReceipt(receivedNow, newStatus);
    PurchaseOrder updated = current.withUpdatedLine(updatedLine, touched(current.audit(), actorId));
    PurchaseOrder saved = repository.save(updated);
    auditRecorder.recordSystemEvent(
        saved.tenantId(),
        "PurchaseOrderLineReceived",
        "PurchaseOrderLine",
        purchaseOrderLineId,
        "{\"purchaseOrderId\":\"%s\",\"receivedNow\":\"%s\"}"
            .formatted(purchaseOrderId, receivedNow.toPlainString()));
    return saved;
  }

  public List<PurchaseOrder> listOrders(String tenantId, String laboratoryId, String branchId) {
    return repository.findByScope(
        requireText(tenantId, "Tenant id is required."),
        requireText(laboratoryId, "Laboratory id is required."),
        requireText(branchId, "Branch id is required."));
  }

  public PurchaseOrder getOrder(String purchaseOrderId) {
    return requireOrder(purchaseOrderId);
  }

  public PurchaseOrder requireOrder(String purchaseOrderId) {
    return repository
        .findById(requireText(purchaseOrderId, "Purchase order id is required."))
        .orElseThrow(
            () ->
                new InventoryEntityNotFoundException(
                    "Purchase order was not found.",
                    InventoryErrorCodes.PURCHASE_ORDER_NOT_FOUND));
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

  public record CreatePurchaseOrderCommand(
      String tenantId,
      String laboratoryId,
      String branchId,
      String supplierId,
      String supplierName,
      String currencyCode,
      List<LineCommand> lines,
      String actorId) {

    public record LineCommand(
        String inventoryItemId, BigDecimal orderedQuantity, BigDecimal unitCost) {}
  }
}
