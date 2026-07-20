package com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.domain;

import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.SupplierSnapshot;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/** BCM-INV-004 PurchaseOrder aggregate. */
public record PurchaseOrder(
    String purchaseOrderId,
    String tenantId,
    String laboratoryId,
    String branchId,
    SupplierSnapshot supplier,
    String status,
    List<PurchaseOrderLine> lines,
    BigDecimal totalAmount,
    String currencyCode,
    String approverId,
    String cancellationReason,
    AuditMetadata audit) {

  public static final String STATUS_DRAFT = "draft";
  public static final String STATUS_SUBMITTED = "submitted";
  public static final String STATUS_APPROVED = "approved";
  public static final String STATUS_RECEIVING = "receiving";
  public static final String STATUS_RECEIVED = "received";
  public static final String STATUS_CANCELLED = "cancelled";

  public boolean isTerminal() {
    return STATUS_RECEIVED.equals(status) || STATUS_CANCELLED.equals(status);
  }

  public boolean canReceiveLines() {
    return STATUS_APPROVED.equals(status) || STATUS_RECEIVING.equals(status);
  }

  public PurchaseOrder withStatus(String newStatus, AuditMetadata newAudit) {
    return new PurchaseOrder(
        purchaseOrderId,
        tenantId,
        laboratoryId,
        branchId,
        supplier,
        newStatus,
        lines,
        totalAmount,
        currencyCode,
        approverId,
        cancellationReason,
        newAudit);
  }

  public PurchaseOrder withApprover(String approver, AuditMetadata newAudit) {
    return new PurchaseOrder(
        purchaseOrderId,
        tenantId,
        laboratoryId,
        branchId,
        supplier,
        STATUS_APPROVED,
        lines,
        totalAmount,
        currencyCode,
        approver,
        cancellationReason,
        newAudit);
  }

  public PurchaseOrder withCancellation(String reason, AuditMetadata newAudit) {
    return new PurchaseOrder(
        purchaseOrderId,
        tenantId,
        laboratoryId,
        branchId,
        supplier,
        STATUS_CANCELLED,
        lines,
        totalAmount,
        currencyCode,
        approverId,
        reason,
        newAudit);
  }

  public PurchaseOrder withUpdatedLine(PurchaseOrderLine updatedLine, AuditMetadata newAudit) {
    List<PurchaseOrderLine> newLines =
        lines.stream()
            .map(
                existing ->
                    existing.purchaseOrderLineId().equals(updatedLine.purchaseOrderLineId())
                        ? updatedLine
                        : existing)
            .collect(Collectors.toList());
    boolean allReceived = newLines.stream().allMatch(l -> PurchaseOrderLine.STATUS_RECEIVED.equals(l.lineStatus()));
    boolean anyReceived =
        newLines.stream().anyMatch(l -> l.receivedQuantity().signum() > 0);
    String newStatus = allReceived ? STATUS_RECEIVED : (anyReceived ? STATUS_RECEIVING : status);
    return new PurchaseOrder(
        purchaseOrderId,
        tenantId,
        laboratoryId,
        branchId,
        supplier,
        newStatus,
        newLines,
        totalAmount,
        currencyCode,
        approverId,
        cancellationReason,
        newAudit);
  }
}
