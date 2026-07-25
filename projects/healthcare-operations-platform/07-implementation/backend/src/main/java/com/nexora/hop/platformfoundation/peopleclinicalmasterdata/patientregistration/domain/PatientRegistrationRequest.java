package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.domain;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Registration process record modeled by {@code bcm-att-002-patient-registration/business-model.md}
 * ENT-REG-001. The registration workflow orchestrates BCM-PER-001 duplicate detection and delegates
 * mutation to BCM-PER-002 aggregate commands; the aggregate is never owned here (RN-002).
 */
public record PatientRegistrationRequest(
        String registrationRequestId,
        String tenantId,
        String laboratoryId,
        String branchId,
        String intakeChannel,
        String candidatePatientId,
        String registrationKind,
        String normalizedFamilyName,
        String normalizedGivenName,
        LocalDate birthDate,
        String draftGivenName,
        String draftFamilyName,
        String draftDocumentType,
        String draftDocumentNumber,
        String draftPatientCode,
        String outcome,
        String outcomePatientId,
        String actorId,
        Instant createdAt,
        Instant updatedAt) {

    public static final String INTAKE_WALK_IN = "walk_in";
    public static final String INTAKE_APPOINTMENT = "appointment";
    public static final String INTAKE_PORTAL_HANDOFF = "portal_handoff";
    public static final String INTAKE_MIGRATION_IMPORT = "migration_import";

    public static final String KIND_NEW_PATIENT = "new_patient";
    public static final String KIND_EXISTING_PATIENT_CONFIRMATION = "existing_patient_confirmation";
    public static final String KIND_REPRESENTATIVE_REGISTRATION = "representative_registration";

    public static final String OUTCOME_PENDING = "pending";
    public static final String OUTCOME_COMMITTED = "committed";
    public static final String OUTCOME_CANCELLED = "cancelled";
    public static final String OUTCOME_REJECTED = "rejected";
}
