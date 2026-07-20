package com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared;

/**
 * Named cross-module read port exposing the referring-doctor/patient relationship captured on
 * {@link com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrder}
 * order snapshots to other bounded contexts.
 *
 * <p>COM-MOD-009-PORTAL-002 (doctor portal commercial workflow) needs a real, server-enforced
 * least-privilege boundary for a referring doctor's access to a patient's released results: a
 * doctor may only view results for a patient they have actually referred (i.e. a patient captured
 * on at least one of their own diagnostic orders). This port lets resultsanddigitaldelivery check
 * that relationship without duplicating DiagnosticOrder ownership, mirroring the
 * {@code laboratoryworkflow.shared.SampleReadPort} pattern established for TD-BE-010.
 *
 * <p>Exposed as the named {@code referring-doctor-authorization-port} dependency slot for Spring
 * Modulith.
 */
@org.springframework.modulith.NamedInterface("referring-doctor-authorization-port")
public interface ReferringDoctorAuthorizationPort {

    /**
     * Returns whether {@code doctorId} is the referring doctor on at least one diagnostic order
     * for {@code patientId} within {@code tenantId}.
     *
     * @param tenantId  the tenant scope
     * @param doctorId  the referring doctor identifier
     * @param patientId the patient identifier
     * @return true if a referral relationship exists between the doctor and the patient
     */
    boolean isPatientReferredByDoctor(String tenantId, String doctorId, String patientId);
}
