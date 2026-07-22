package com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain;

import java.time.Instant;
import java.time.LocalDate;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

/**
 * Standalone pre-order price estimate aggregate (ENT-QUO-001), owned entirely by this capability.
 * The cash-sales Sale aggregate (AGG-010) is deferred to a future MVP-MOD-005 (TD-DEF-001); this
 * aggregate remains the root until that module optionally supersedes its commercial lifecycle.
 */
public record QuotationRequest(
        String quotationId,
        String tenantId,
        String laboratoryId,
        String branchId,
        String patientId,
        String prospectiveFullName,
        String prospectivePhone,
        String prospectiveEmail,
        String priceListId,
        int priceListVersion,
        Money totalAmount,
        String discountKind,
        java.math.BigDecimal discountValue,
        LocalDate validUntil,
        String channel,
        String status,
        String convertedOrderId,
        String cancellationReason,
        String actorId,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_ISSUED = "issued";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_EXPIRED = "expired";
    public static final String STATUS_CONVERTED = "converted";
    public static final String STATUS_CANCELLED = "cancelled";

    public static final String DISCOUNT_PERCENTAGE = "percentage";
    public static final String DISCOUNT_FIXED_AMOUNT = "fixed_amount";
    public static final String DISCOUNT_PROMOTION_CODE = "promotion_code";

    /** Mirrors {@code AppointmentSlot}'s channel set (COM-MOD-011-FE-001 defect fix: quotations
     * previously had no queryable way to distinguish public-website-submitted drafts). */
    public static final String CHANNEL_WALK_IN_SCHEDULING = "walk_in_scheduling";
    public static final String CHANNEL_PHONE = "phone";
    public static final String CHANNEL_EMPLOYEE_PORTAL = "employee_portal";
    public static final String CHANNEL_PATIENT_PORTAL_REQUEST_LATER = "patient_portal_request_later";
    public static final String CHANNEL_PUBLIC_WEBSITE = "public_website";
}
