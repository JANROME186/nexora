package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain;

import java.time.LocalDate;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument.DocumentNumberMaskingPolicy;

/**
 * Immutable minimal patient projection consumed by downstream contexts (orders-samples,
 * laboratory-results, imaging-operations, cash-sales, billing-tax). This is the only view exposed
 * to non-owning contexts (BCM-PER-002 RN-003 / POL-PAT-002-02). Document numbers are masked per
 * the owning tenant's {@link DocumentNumberMaskingPolicy} to honor RN-008. Modeled as RM-PAT-001.
 */
public record PatientSnapshot(
        String patientId,
        String tenantId,
        String laboratoryId,
        String patientCode,
        String fullName,
        LocalDate birthDate,
        String sexAtBirth,
        String primaryDocumentType,
        String primaryDocumentNumberMasked,
        String status,
        int version) {

    public static PatientSnapshot from(Patient patient, DocumentNumberMaskingPolicy maskingPolicy) {
        return new PatientSnapshot(
                patient.patientId(),
                patient.tenantId(),
                patient.laboratoryId(),
                patient.patientCode(),
                patient.name() == null ? null : patient.name().fullNameDisplay(),
                patient.birthDate(),
                patient.sexAtBirth(),
                patient.primaryDocument() == null ? null : patient.primaryDocument().documentType(),
                patient.primaryDocument() == null ? null : patient.primaryDocument().maskedNumber(maskingPolicy),
                patient.status(),
                patient.version());
    }
}
