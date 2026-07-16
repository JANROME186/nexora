package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain;

import java.util.List;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

/**
 * Provider-agnostic fiscal adapter request. Contains all data required to submit a billing
 * request to any fiscal provider without exposing country-specific fields.
 *
 * @param idempotencyKey      unique key derived from invoiceRequestId and version; used to prevent
 *                            duplicate submissions on retry
 * @param invoiceRequestId    the HOP-internal invoice request identifier
 * @param fiscalProfileSnapshot the fiscal profile captured at request creation time
 * @param taxLines            line-item tax breakdown
 * @param totalAmount         the sale total amount this request covers
 */
public record FiscalAdapterRequest(
        String idempotencyKey,
        String invoiceRequestId,
        FiscalProfileSnapshot fiscalProfileSnapshot,
        List<TaxLine> taxLines,
        Money totalAmount) {
}
