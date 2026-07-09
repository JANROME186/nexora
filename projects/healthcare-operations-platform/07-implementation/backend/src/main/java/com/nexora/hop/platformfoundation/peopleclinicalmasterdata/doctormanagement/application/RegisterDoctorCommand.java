package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application;

import java.time.LocalDate;

public record RegisterDoctorCommand(
        String tenantId,
        String laboratoryId,
        String doctorCode,
        String givenName,
        String middleName,
        String familyName,
        String secondFamilyName,
        String doctorType,
        String primaryDocumentType,
        String primaryDocumentNumber,
        String primaryDocumentIssuingCountry,
        LocalDate primaryDocumentIssuedAt,
        LocalDate primaryDocumentExpiresAt,
        String addressCountry,
        String addressCity,
        String addressStreet) {
}
