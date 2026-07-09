package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application;

import java.time.LocalDate;

public record AttachCredentialCommand(
        String credentialType,
        String credentialNumber,
        String issuingAuthority,
        String issuingCountry,
        LocalDate issuedAt,
        LocalDate expiresAt) {
}
