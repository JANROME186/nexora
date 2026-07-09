package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain;

import java.time.Instant;
import java.time.LocalDate;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonAddress;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonName;

/**
 * Patient aggregate root (AGG-001) owned by the {@code patient-management} bounded context.
 * Modeled by {@code bcm-per-002-patient-management/business-model.yaml} ENT-PAT-001. Only the
 * patient-management context may mutate this aggregate; downstream consumers must reference
 * {@link PatientSnapshot} instead (BCM-PER-002 RN-003).
 */
public record Patient(
        String patientId,
        String tenantId,
        String laboratoryId,
        String patientCode,
        PersonName name,
        LocalDate birthDate,
        String sexAtBirth,
        PersonDocument primaryDocument,
        PersonAddress address,
        String preferredLocale,
        String status,
        String mergedIntoPatientId,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
    public static final String STATUS_MERGED = "merged";
    public static final String STATUS_DECEASED = "deceased";

    public static final String SEX_FEMALE = "female";
    public static final String SEX_MALE = "male";
    public static final String SEX_UNKNOWN = "unknown";
    public static final String SEX_NOT_DISCLOSED = "not_disclosed";
}
