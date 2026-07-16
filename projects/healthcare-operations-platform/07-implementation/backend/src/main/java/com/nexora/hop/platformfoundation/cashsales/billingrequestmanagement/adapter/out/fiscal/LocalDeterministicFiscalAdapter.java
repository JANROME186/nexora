package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.adapter.out.fiscal;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalAdapterException;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalAdapterPort;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalAdapterRequest;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalAdapterResponse;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.TaxLine;

/**
 * Local deterministic implementation of {@link FiscalAdapterPort}. This adapter is the default
 * active bean and represents the explicit replaceable boundary that can be swapped for a
 * country-specific fiscal provider adapter without any application or domain code changes.
 *
 * <p>Behaviour contract (for tests and local development):</p>
 * <ul>
 *   <li>{@link #submit} - immediately returns {@code submitted} to model a provider that
 *       acknowledges the request asynchronously.</li>
 *   <li>{@link #retry} - returns {@code issued} to model a successful asynchronous completion
 *       on the second attempt.</li>
 *   <li>{@link #cancel} - always returns {@code cancelled}.</li>
 * </ul>
 *
 * <p>This class must never contain real fiscal authority, SAT, CFDI, PAC, HMRC, or any other
 * country-specific protocol logic.</p>
 */
@Component
public class LocalDeterministicFiscalAdapter implements FiscalAdapterPort {

    private static final String LOCAL_PROVIDER_ID = "local-deterministic";

    @Override
    public FiscalAdapterResponse submit(FiscalAdapterRequest request) {
        validateRequest(request);
        String correlationId = LOCAL_PROVIDER_ID + "-" + UUID.randomUUID();
        String snapshot = "{\"provider\":\"local-deterministic\",\"idempotencyKey\":\"%s\",\"action\":\"submit\"}"
                .formatted(request.idempotencyKey());
        return new FiscalAdapterResponse(correlationId, FiscalAdapterResponse.STATUS_SUBMITTED, snapshot);
    }

    @Override
    public FiscalAdapterResponse retry(String idempotencyKey, String correlationId, List<TaxLine> taxLines) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new FiscalAdapterException(
                    "Idempotency key is required for retry.", "MISSING_IDEMPOTENCY_KEY", false);
        }
        String resolvedCorrelationId = correlationId != null ? correlationId
                : LOCAL_PROVIDER_ID + "-" + UUID.randomUUID();
        String snapshot = "{\"provider\":\"local-deterministic\",\"idempotencyKey\":\"%s\",\"action\":\"retry\"}"
                .formatted(idempotencyKey);
        return new FiscalAdapterResponse(resolvedCorrelationId, FiscalAdapterResponse.STATUS_ISSUED, snapshot);
    }

    @Override
    public FiscalAdapterResponse cancel(String idempotencyKey, String correlationId) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new FiscalAdapterException(
                    "Idempotency key is required for cancel.", "MISSING_IDEMPOTENCY_KEY", false);
        }
        String resolvedCorrelationId = correlationId != null ? correlationId
                : LOCAL_PROVIDER_ID + "-" + UUID.randomUUID();
        String snapshot = "{\"provider\":\"local-deterministic\",\"idempotencyKey\":\"%s\",\"action\":\"cancel\"}"
                .formatted(idempotencyKey);
        return new FiscalAdapterResponse(resolvedCorrelationId, FiscalAdapterResponse.STATUS_CANCELLED, snapshot);
    }

    private static void validateRequest(FiscalAdapterRequest request) {
        if (request == null) {
            throw new FiscalAdapterException("Fiscal adapter request must not be null.", "NULL_REQUEST", false);
        }
        if (request.idempotencyKey() == null || request.idempotencyKey().isBlank()) {
            throw new FiscalAdapterException(
                    "Idempotency key is required.", "MISSING_IDEMPOTENCY_KEY", false);
        }
        if (request.invoiceRequestId() == null || request.invoiceRequestId().isBlank()) {
            throw new FiscalAdapterException(
                    "Invoice request id is required.", "MISSING_INVOICE_REQUEST_ID", false);
        }
        if (request.fiscalProfileSnapshot() == null) {
            throw new FiscalAdapterException(
                    "Fiscal profile snapshot is required.", "MISSING_FISCAL_PROFILE", false);
        }
    }
}
