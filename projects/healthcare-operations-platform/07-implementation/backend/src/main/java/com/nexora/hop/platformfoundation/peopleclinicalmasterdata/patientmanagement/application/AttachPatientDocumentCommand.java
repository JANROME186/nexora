package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application;

import java.time.LocalDate;

public record AttachPatientDocumentCommand(
        String category,
        String fileReference,
        LocalDate expiresAt) {
}
