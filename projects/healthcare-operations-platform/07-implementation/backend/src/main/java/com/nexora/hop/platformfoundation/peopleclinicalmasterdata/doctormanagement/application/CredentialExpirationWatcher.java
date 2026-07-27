package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.Doctor;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.DoctorRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.ProfessionalCredential;

/**
 * BCM-PER-003 RN-005 {@code scheduler:credential_expiration_watcher}: proactively transitions
 * verified professional credentials to {@link ProfessionalCredential#STATUS_EXPIRED} once their
 * {@code expiresAt} date has passed, and raises an audited {@code DoctorCredentialExpired} event so
 * a doctor/administrator worklist can flag the doctor for re-verification. Runs daily by default;
 * {@link #runOnce()} is also invoked directly by tests and can be triggered on demand by
 * operational tooling. Closes technical debt TD-BE-007.
 */
@Component
public class CredentialExpirationWatcher {

    private final DoctorRepository repository;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public CredentialExpirationWatcher(DoctorRepository repository, AuditRecorder auditRecorder) {
        this(repository, auditRecorder, Clock.systemUTC());
    }

    CredentialExpirationWatcher(DoctorRepository repository, AuditRecorder auditRecorder, Clock clock) {
        this.repository = repository;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    @Scheduled(cron = "${hop.doctor-management.credential-expiration-cron:0 0 2 * * *}")
    public void transitionExpiredCredentials() {
        runOnce();
    }

    /**
     * Scans for verified credentials whose {@code expiresAt} has passed, transitions each to
     * {@link ProfessionalCredential#STATUS_EXPIRED} and records a
     * {@code DoctorCredentialExpired} audit event carrying the owning doctor id so downstream
     * consumers can build a re-verification worklist/notification. Returns the number of
     * credentials transitioned.
     */
    public int runOnce() {
        LocalDate today = LocalDate.now(clock);
        List<ProfessionalCredential> due = repository.findVerifiedCredentialsExpiringBefore(today);
        for (ProfessionalCredential credential : due) {
            ProfessionalCredential expired = expire(credential);
            repository.saveCredential(expired);

            Optional<Doctor> doctor = repository.findById(credential.doctorId());
            String tenantId = doctor.map(Doctor::tenantId).orElse(null);
            auditRecorder.recordSystemEvent(tenantId, "DoctorCredentialExpired", "ProfessionalCredential",
                    credential.credentialId(),
                    "{\"doctorId\":\"%s\",\"reVerificationRequired\":true}".formatted(jsonText(credential.doctorId())));
        }
        return due.size();
    }

    private static ProfessionalCredential expire(ProfessionalCredential credential) {
        return new ProfessionalCredential(
                credential.credentialId(), credential.doctorId(), credential.credentialType(),
                credential.credentialNumber(), credential.issuingAuthority(), credential.issuingCountry(),
                credential.issuedAt(), credential.expiresAt(), ProfessionalCredential.STATUS_EXPIRED,
                credential.verifiedAt());
    }

    private static String jsonText(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
