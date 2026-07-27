package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument.DocumentNumberMaskingPolicy;

/**
 * Immutable minimal doctor projection consumed by downstream contexts (orders-samples,
 * laboratory-results, imaging-operations, doctor-portal). Modeled as RM-DOC-001. Document numbers
 * are masked per the owning tenant's {@link DocumentNumberMaskingPolicy} to honor BCM-PER-003
 * RN-008.
 */
public record DoctorSnapshot(
        String doctorId,
        String tenantId,
        String laboratoryId,
        String doctorCode,
        String fullName,
        String primaryDocumentType,
        String primaryDocumentNumberMasked,
        String doctorType,
        String status,
        int version) {

    public static DoctorSnapshot from(Doctor doctor, DocumentNumberMaskingPolicy maskingPolicy) {
        return new DoctorSnapshot(
                doctor.doctorId(),
                doctor.tenantId(),
                doctor.laboratoryId(),
                doctor.doctorCode(),
                doctor.name() == null ? null : doctor.name().fullNameDisplay(),
                doctor.primaryDocument() == null ? null : doctor.primaryDocument().documentType(),
                doctor.primaryDocument() == null ? null : doctor.primaryDocument().maskedNumber(maskingPolicy),
                doctor.doctorType(),
                doctor.status(),
                doctor.version());
    }
}
