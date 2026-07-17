package com.nexora.hop.platformfoundation.laboratoryworkflow.shared;

import java.util.Optional;

/**
 * Named cross-module read port exposing the Sample aggregate state to other bounded contexts.
 *
 * <p>This port is the TD-BE-010 advance: it provides the real Sample status needed to replace
 * the DiagnosticOrder.status() proxy in DiagnosticOrderManagementService.cancel(). The wiring
 * into FrontDeskCareDelivery is deferred to MVP-MOD-006-BE-002 (see TD-BE-010). This interface
 * is the compilation artifact that enables that future wiring.
 *
 * <p>Exposed as the named {@code sample-read-port} dependency slot for Spring Modulith.
 *
 * @see com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleStatus
 */
@org.springframework.modulith.NamedInterface("sample-read-port")
public interface SampleReadPort {

    /**
     * Returns whether any non-rejected Sample has been collected for the given order.
     * Returns {@code false} if no sample has been collected yet.
     *
     * <p>Used by order-cancellation logic to determine whether real downstream sample activity
     * exists (TD-BE-010 fix, wired in MVP-MOD-006-BE-002).
     *
     * @param orderId the diagnostic order identifier
     * @param tenantId the tenant scope
     * @return true if at least one non-terminal sample exists for this order
     */
    boolean hasActiveSampleForOrder(String orderId, String tenantId);

    /**
     * Returns the current status string of the most recent sample for the given order,
     * if one exists.
     *
     * @param orderId the diagnostic order identifier
     * @param tenantId the tenant scope
     * @return optional status value, empty if no sample has been collected
     */
    Optional<String> findPrimaryStatusForOrder(String orderId, String tenantId);

    /**
     * Returns the current status string of the specified sample.
     *
     * @param sampleId the sample identifier
     * @param tenantId the tenant scope
     * @return optional status value, empty if sample does not exist
     */
    Optional<String> findSampleStatus(String sampleId, String tenantId);
}
