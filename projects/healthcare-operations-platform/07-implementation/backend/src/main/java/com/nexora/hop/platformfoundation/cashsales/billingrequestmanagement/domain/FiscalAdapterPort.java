package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain;

import java.util.List;

/**
 * Provider-agnostic outbound port for fiscal invoice submission. Implementations must be
 * replaceable; no country-specific, tax-authority or proprietary protocol logic may appear
 * in this interface or in the domain layer. The local deterministic adapter is the default
 * implementation; a real country adapter replaces it without any application or domain code
 * changes.
 *
 * <p>All operations are idempotent relative to the idempotency key embedded in the request.
 * Implementations must honour the key to prevent duplicate submissions on retries.</p>
 *
 * <p>Invariant: the fiscal adapter must never read or mutate clinical, patient, order, quotation
 * or catalog aggregates.</p>
 */
public interface FiscalAdapterPort {

    /**
     * Submits a billing request to the fiscal provider for the first time.
     *
     * @param request the provider-agnostic fiscal request
     * @return the adapter response capturing correlation id, status and raw snapshot
     * @throws FiscalAdapterException if the provider rejects the request (retryable or terminal)
     */
    FiscalAdapterResponse submit(FiscalAdapterRequest request);

    /**
     * Retries a previously submitted billing request using the same idempotency key.
     *
     * @param idempotencyKey the original idempotency key used on first submission
     * @param correlationId  the provider correlation id returned on first submission; may be null
     * @param taxLines       the tax lines to include in the retry request
     * @return the updated adapter response
     * @throws FiscalAdapterException if the retry is rejected
     */
    FiscalAdapterResponse retry(String idempotencyKey, String correlationId, List<TaxLine> taxLines);

    /**
     * Requests cancellation of a previously submitted billing request.
     *
     * @param idempotencyKey the original idempotency key
     * @param correlationId  the provider correlation id; may be null if never submitted
     * @return the adapter response with status {@code cancelled}
     * @throws FiscalAdapterException if the cancellation is rejected
     */
    FiscalAdapterResponse cancel(String idempotencyKey, String correlationId);
}
