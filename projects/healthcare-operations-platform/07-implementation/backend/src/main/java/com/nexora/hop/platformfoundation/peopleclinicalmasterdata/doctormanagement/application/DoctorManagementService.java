package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application;

import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.optionalText;
import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.requiredText;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.DoctorDirectory;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.Doctor;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.DoctorRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.DoctorSnapshot;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.ProfessionalCredential;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.SpecialtyAssignment;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application.PersonDocumentUniquenessPolicy;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application.PersonDuplicateDetectionEngine;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonKind;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.InvalidPeopleCommandException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleConflictException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleEntityNotFoundException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonAddress;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonName;

/**
 * Compiles the generatable outputs of {@code bcm-per-003-doctor-management/generation-plan.md}
 * (MVP-MOD-003-BE-001) and implements its custom rules (MVP-MOD-003-BE-002): RN-002 (duplicate
 * detection integration), RN-004/RN-006 (referring-doctor eligibility requiring an active status
 * and at least one verified, non-expired medical license), RN-005 (credential revocation cascade)
 * and RN-007 (portal access preparation that never grants access on its own).
 * <p>
 * <b>Known boundary (see technical debt TD-BE-005):</b> BE-001 compiled {@link #register} to set
 * every new doctor to {@link Doctor#STATUS_ACTIVE} immediately, independent of credential state.
 * Changing that default would be a breaking behavior change to already-validated generatable
 * output and would require a business-model change outside this backlog's scope. RN-004's
 * "cannot become active without a verified credential" requirement is therefore enforced at the
 * <em>eligibility query</em> level ({@link #isEligibleAsReferringDoctor(String)}) rather than by
 * gating the stored {@code status} field.
 */
@Service
public class DoctorManagementService implements DoctorDirectory {

    private static final List<String> DOCTOR_TYPES = List.of(
            Doctor.TYPE_REFERRING_EXTERNAL, Doctor.TYPE_INTERNAL_MEDICAL_VALIDATOR, Doctor.TYPE_BOTH);

    private static final List<String> DOCUMENT_TYPES = List.of(
            PersonDocument.TYPE_NATIONAL_ID,
            PersonDocument.TYPE_PASSPORT,
            PersonDocument.TYPE_DRIVERS_LICENSE,
            PersonDocument.TYPE_TAX_ID,
            PersonDocument.TYPE_PROFESSIONAL_LICENSE,
            PersonDocument.TYPE_OTHER);

    private static final List<String> CREDENTIAL_TYPES = List.of(
            ProfessionalCredential.TYPE_MEDICAL_LICENSE,
            ProfessionalCredential.TYPE_SPECIALTY_CERTIFICATION,
            ProfessionalCredential.TYPE_BOARD_CERTIFICATION,
            ProfessionalCredential.TYPE_INSTITUTIONAL_REGISTRATION,
            ProfessionalCredential.TYPE_OTHER);

    private final DoctorRepository repository;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final PersonDocumentUniquenessPolicy documentUniquenessPolicy;
    private final PersonDuplicateDetectionEngine duplicateDetectionEngine;
    private final Clock clock;

    @Autowired
    public DoctorManagementService(
            DoctorRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            PersonDocumentUniquenessPolicy documentUniquenessPolicy,
            PersonDuplicateDetectionEngine duplicateDetectionEngine) {
        this(repository, tenantDirectory, auditRecorder, documentUniquenessPolicy, duplicateDetectionEngine,
                Clock.systemUTC());
    }

    DoctorManagementService(
            DoctorRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            PersonDocumentUniquenessPolicy documentUniquenessPolicy,
            PersonDuplicateDetectionEngine duplicateDetectionEngine,
            Clock clock) {
        this.repository = repository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.documentUniquenessPolicy = documentUniquenessPolicy;
        this.duplicateDetectionEngine = duplicateDetectionEngine;
        this.clock = clock;
    }

    public Doctor register(RegisterDoctorCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String doctorCode = requiredText(command.doctorCode(), "Doctor code is required.");
        PersonName name = requiredName(command.givenName(), command.middleName(), command.familyName(),
                command.secondFamilyName());
        String doctorType = requiredOneOf(command.doctorType(), "Doctor type is invalid.",
                DOCTOR_TYPES.toArray(String[]::new));
        PersonDocument primaryDocument = requiredDocument(command.primaryDocumentType(),
                command.primaryDocumentNumber(), command.primaryDocumentIssuingCountry(),
                command.primaryDocumentIssuedAt(), command.primaryDocumentExpiresAt());

        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new PeopleEntityNotFoundException("Tenant was not found.");
        }
        if (repository.existsByDoctorCode(tenantId, doctorCode, null)) {
            throw new PeopleConflictException("Doctor code already exists in this tenant.");
        }
        // BCM-PER-001 RN-002: primary document number must be unique within tenant across Patient
        // and Doctor scopes.
        documentUniquenessPolicy.ensureUnique(tenantId, primaryDocument.documentType(),
                primaryDocument.documentNumber(), null, null);

        // BCM-PER-003 RN-002: duplicate detection must be invoked and its result recorded before a
        // doctor is registered. Non-blocking at the aggregate-command level; see
        // PatientManagementService.register for the equivalent design rationale.
        duplicateDetectionEngine.detect(tenantId, PersonKind.DOCTOR, command.familyName(),
                command.givenName(), null, null, null, true);

        PersonAddress address = optionalAddress(command.addressCountry(), command.addressCity(),
                command.addressStreet());
        Instant now = Instant.now(clock);
        Doctor doctor = new Doctor(newId(), tenantId, laboratoryId, doctorCode, name, primaryDocument,
                address, doctorType, Doctor.STATUS_ACTIVE, Doctor.PORTAL_STATUS_NOT_PROVISIONED,
                null, 1, now, now);
        Doctor saved = repository.save(doctor);
        auditRecorder.recordSystemEvent(tenantId, "DoctorRegistered", "Doctor", saved.doctorId(),
                "{\"doctorCode\":\"%s\"}".formatted(jsonText(saved.doctorCode())));
        return saved;
    }

    public Doctor update(String doctorId, UpdateDoctorCommand command) {
        Doctor current = require(doctorId);
        if (Doctor.STATUS_RETIRED.equals(current.status())) {
            throw new PeopleConflictException("A retired doctor cannot be updated.");
        }
        PersonName name = requiredName(command.givenName(), command.middleName(), command.familyName(),
                command.secondFamilyName());
        String doctorType = requiredOneOf(command.doctorType(), "Doctor type is invalid.",
                DOCTOR_TYPES.toArray(String[]::new));
        PersonDocument primaryDocument = requiredDocument(command.primaryDocumentType(),
                command.primaryDocumentNumber(), command.primaryDocumentIssuingCountry(),
                command.primaryDocumentIssuedAt(), command.primaryDocumentExpiresAt());
        PersonAddress address = optionalAddress(command.addressCountry(), command.addressCity(),
                command.addressStreet());
        Doctor updated = new Doctor(current.doctorId(), current.tenantId(), current.laboratoryId(),
                current.doctorCode(), name, primaryDocument, address, doctorType, current.status(),
                current.portalStatus(), current.portalEmail(), current.version() + 1,
                current.createdAt(), Instant.now(clock));
        Doctor saved = repository.save(updated);
        auditRecorder.recordSystemEvent(saved.tenantId(), "DoctorUpdated", "Doctor", saved.doctorId(),
                "{\"version\":%d}".formatted(saved.version()));
        return saved;
    }

    public Doctor retire(String doctorId) {
        Doctor current = require(doctorId);
        if (Doctor.STATUS_RETIRED.equals(current.status())) {
            throw new PeopleConflictException("Doctor is already retired.");
        }
        Doctor retired = new Doctor(current.doctorId(), current.tenantId(), current.laboratoryId(),
                current.doctorCode(), current.name(), current.primaryDocument(), current.address(),
                current.doctorType(), Doctor.STATUS_RETIRED, current.portalStatus(),
                current.portalEmail(), current.version() + 1, current.createdAt(), Instant.now(clock));
        Doctor saved = repository.save(retired);
        auditRecorder.recordSystemEvent(saved.tenantId(), "DoctorRetired", "Doctor", saved.doctorId(), "{}");
        return saved;
    }

    /**
     * BCM-PER-003 RN-006: suspends a doctor so the downstream eligibility projection
     * ({@link #isEligibleAsReferringDoctor(String)}) immediately excludes it from referring-doctor
     * selection on new orders. A retired doctor cannot be suspended (retirement is terminal); an
     * already-suspended doctor is a conflict rather than a silent no-op so callers see the true
     * current state.
     */
    public Doctor suspend(String doctorId, String reasonCode) {
        Doctor current = require(doctorId);
        if (Doctor.STATUS_RETIRED.equals(current.status())) {
            throw new PeopleConflictException("A retired doctor cannot be suspended.");
        }
        if (Doctor.STATUS_SUSPENDED.equals(current.status())) {
            throw new PeopleConflictException("Doctor is already suspended.");
        }
        Doctor suspended = new Doctor(current.doctorId(), current.tenantId(), current.laboratoryId(),
                current.doctorCode(), current.name(), current.primaryDocument(), current.address(),
                current.doctorType(), Doctor.STATUS_SUSPENDED, current.portalStatus(),
                current.portalEmail(), current.version() + 1, current.createdAt(), Instant.now(clock));
        Doctor saved = repository.save(suspended);
        auditRecorder.recordSystemEvent(saved.tenantId(), "DoctorSuspended", "Doctor", saved.doctorId(),
                "{\"reasonCode\":\"%s\"}".formatted(jsonText(optionalText(reasonCode) == null
                        ? "unspecified" : reasonCode)));
        return saved;
    }

    /**
     * BCM-PER-003 RN-007: a doctor portal access baseline may declare provisioning readiness
     * ({@link Doctor#PORTAL_STATUS_READY}) but must never grant portal access
     * ({@link Doctor#PORTAL_STATUS_PROVISIONED}) directly; actual identity provisioning happens in
     * COM-MOD-009 and is intentionally out of this module's reach.
     */
    public Doctor preparePortalAccess(String doctorId, String portalEmail) {
        Doctor current = require(doctorId);
        String email = requiredText(portalEmail, "Portal email is required.");
        if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {
            throw new InvalidPeopleCommandException("Portal email is invalid.");
        }
        if (Doctor.PORTAL_STATUS_PROVISIONED.equals(current.portalStatus())) {
            throw new PeopleConflictException(
                    "Doctor portal access is already provisioned; use the portal identity workflow to change it.");
        }
        Doctor prepared = new Doctor(current.doctorId(), current.tenantId(), current.laboratoryId(),
                current.doctorCode(), current.name(), current.primaryDocument(), current.address(),
                current.doctorType(), current.status(), Doctor.PORTAL_STATUS_READY, email,
                current.version() + 1, current.createdAt(), Instant.now(clock));
        Doctor saved = repository.save(prepared);
        auditRecorder.recordSystemEvent(saved.tenantId(), "DoctorPortalAccessPrepared", "Doctor",
                saved.doctorId(), "{\"portalStatus\":\"%s\"}".formatted(saved.portalStatus()));
        return saved;
    }

    public Doctor get(String doctorId) {
        return require(doctorId);
    }

    public DoctorSnapshot snapshot(String doctorId) {
        return DoctorSnapshot.from(require(doctorId));
    }

    public List<Doctor> list(String laboratoryId) {
        return repository.findByLaboratoryId(requiredText(laboratoryId, "Laboratory id is required."));
    }

    // -- Credentials --------------------------------------------------------------------------

    public ProfessionalCredential attachCredential(String doctorId, AttachCredentialCommand command) {
        Doctor doctor = require(doctorId);
        String credentialType = requiredOneOf(command.credentialType(), "Credential type is invalid.",
                CREDENTIAL_TYPES.toArray(String[]::new));
        String number = requiredText(command.credentialNumber(), "Credential number is required.");
        String issuingAuthority = requiredText(command.issuingAuthority(), "Issuing authority is required.");
        LocalDate issuedAt = requiredDate(command.issuedAt(), "Credential issued date is required.");
        ProfessionalCredential credential = new ProfessionalCredential(newId(), doctor.doctorId(),
                credentialType, number, issuingAuthority, optionalText(command.issuingCountry()),
                issuedAt, command.expiresAt(), ProfessionalCredential.STATUS_PENDING, null);
        repository.saveCredential(credential);
        auditRecorder.recordSystemEvent(doctor.tenantId(), "DoctorCredentialAttached",
                "ProfessionalCredential", credential.credentialId(),
                "{\"doctorId\":\"%s\",\"credentialType\":\"%s\"}"
                        .formatted(jsonText(doctor.doctorId()), jsonText(credential.credentialType())));
        return credential;
    }

    public List<ProfessionalCredential> listCredentials(String doctorId) {
        require(doctorId);
        return repository.findCredentials(doctorId);
    }

    /**
     * BCM-PER-003 RN-004: verifies a credential. Verifying the doctor's first medical license is
     * what makes {@link #isEligibleAsReferringDoctor(String)} return {@code true} for an otherwise
     * active doctor, which is where this backlog enforces "cannot become active without a verified
     * credential" (see the class-level known-boundary note on the stored {@code status} field).
     * A credential already past its {@code expiresAt} date cannot be verified.
     */
    public ProfessionalCredential verifyCredential(String doctorId, String credentialId) {
        Doctor doctor = require(doctorId);
        ProfessionalCredential credential = requireCredential(doctor.doctorId(), credentialId);
        if (ProfessionalCredential.STATUS_VERIFIED.equals(credential.verificationStatus())) {
            throw new PeopleConflictException("Credential is already verified.");
        }
        if (ProfessionalCredential.STATUS_REVOKED.equals(credential.verificationStatus())) {
            throw new PeopleConflictException("A revoked credential cannot be verified.");
        }
        if (credential.expiresAt() != null && credential.expiresAt().isBefore(LocalDate.now(clock))) {
            throw new PeopleConflictException("Credential is expired and cannot be verified.");
        }
        ProfessionalCredential verified = new ProfessionalCredential(
                credential.credentialId(), credential.doctorId(), credential.credentialType(),
                credential.credentialNumber(), credential.issuingAuthority(), credential.issuingCountry(),
                credential.issuedAt(), credential.expiresAt(), ProfessionalCredential.STATUS_VERIFIED,
                Instant.now(clock));
        repository.saveCredential(verified);
        auditRecorder.recordSystemEvent(doctor.tenantId(), "DoctorCredentialVerified", "ProfessionalCredential",
                verified.credentialId(), "{\"doctorId\":\"%s\"}".formatted(jsonText(doctor.doctorId())));
        return verified;
    }

    /**
     * BCM-PER-003 RN-005: revokes a credential. The eligibility cascade is expressed through
     * {@link #isEligibleAsReferringDoctor(String)}, which recomputes eligibility from the doctor's
     * remaining verified, non-expired medical license credentials rather than mutating a separate
     * stored eligibility flag.
     */
    public ProfessionalCredential revokeCredential(String doctorId, String credentialId) {
        Doctor doctor = require(doctorId);
        ProfessionalCredential credential = requireCredential(doctor.doctorId(), credentialId);
        if (ProfessionalCredential.STATUS_REVOKED.equals(credential.verificationStatus())) {
            throw new PeopleConflictException("Credential is already revoked.");
        }
        ProfessionalCredential revoked = new ProfessionalCredential(
                credential.credentialId(), credential.doctorId(), credential.credentialType(),
                credential.credentialNumber(), credential.issuingAuthority(), credential.issuingCountry(),
                credential.issuedAt(), credential.expiresAt(), ProfessionalCredential.STATUS_REVOKED,
                credential.verifiedAt());
        repository.saveCredential(revoked);
        auditRecorder.recordSystemEvent(doctor.tenantId(), "DoctorCredentialRevoked", "ProfessionalCredential",
                revoked.credentialId(), "{\"doctorId\":\"%s\"}".formatted(jsonText(doctor.doctorId())));
        return revoked;
    }

    // -- Specialties -------------------------------------------------------------------------

    public SpecialtyAssignment assignSpecialty(String doctorId, AssignSpecialtyCommand command) {
        Doctor doctor = require(doctorId);
        String specialtyCode = requiredText(command.specialtyCode(), "Specialty code is required.");
        SpecialtyAssignment assignment = new SpecialtyAssignment(newId(), doctor.doctorId(),
                specialtyCode, command.primary());
        repository.saveSpecialty(assignment);
        return assignment;
    }

    public List<SpecialtyAssignment> listSpecialties(String doctorId) {
        require(doctorId);
        return repository.findSpecialties(doctorId);
    }

    public void unassignSpecialty(String doctorId, String assignmentId) {
        require(doctorId);
        repository.deleteSpecialty(requiredText(assignmentId, "Specialty assignment id is required."));
    }

    // -- DoctorDirectory --------------------------------------------------------------------

    @Override
    public Optional<DoctorSnapshot> findSnapshot(String doctorId) {
        if (doctorId == null || doctorId.isBlank()) {
            return Optional.empty();
        }
        return repository.findById(doctorId).map(DoctorSnapshot::from);
    }

    @Override
    public boolean doctorExists(String doctorId) {
        if (doctorId == null || doctorId.isBlank()) {
            return false;
        }
        return repository.findById(doctorId).isPresent();
    }

    /**
     * BCM-PER-003 RN-004/RN-006: eligible only when the doctor is {@link Doctor#STATUS_ACTIVE} and
     * has at least one {@link ProfessionalCredential#STATUS_VERIFIED} medical-license credential
     * that is not past its {@code expiresAt} date.
     */
    @Override
    public boolean isEligibleAsReferringDoctor(String doctorId) {
        if (doctorId == null || doctorId.isBlank()) {
            return false;
        }
        Optional<Doctor> doctor = repository.findById(doctorId);
        if (doctor.isEmpty() || !Doctor.STATUS_ACTIVE.equals(doctor.get().status())) {
            return false;
        }
        LocalDate today = LocalDate.now(clock);
        return repository.findCredentials(doctorId).stream()
                .anyMatch(credential -> ProfessionalCredential.TYPE_MEDICAL_LICENSE.equals(credential.credentialType())
                        && ProfessionalCredential.STATUS_VERIFIED.equals(credential.verificationStatus())
                        && (credential.expiresAt() == null || !credential.expiresAt().isBefore(today)));
    }

    /** Used by BCM-PER-001 duplicate detection to inspect medical-staff records. */
    public List<Doctor> searchByNaturalKey(String tenantId, String normalizedFamilyName,
            String normalizedGivenName, LocalDate birthDate) {
        return repository.searchByNaturalKey(
                requiredText(tenantId, "Tenant id is required."),
                normalizedFamilyName, normalizedGivenName, birthDate);
    }

    // -- Helpers ----------------------------------------------------------------------------

    private Doctor require(String doctorId) {
        return repository.findById(requiredText(doctorId, "Doctor id is required."))
                .orElseThrow(() -> new PeopleEntityNotFoundException("Doctor was not found."));
    }

    private ProfessionalCredential requireCredential(String doctorId, String credentialId) {
        String id = requiredText(credentialId, "Credential id is required.");
        ProfessionalCredential credential = repository.findCredentialById(id)
                .orElseThrow(() -> new PeopleEntityNotFoundException("Professional credential was not found."));
        if (!doctorId.equals(credential.doctorId())) {
            throw new PeopleEntityNotFoundException("Professional credential was not found.");
        }
        return credential;
    }

    private static PersonName requiredName(String given, String middle, String family, String secondFamily) {
        String givenTrimmed = requiredText(given, "Given name is required.");
        String familyTrimmed = requiredText(family, "Family name is required.");
        return new PersonName(givenTrimmed, optionalText(middle), familyTrimmed,
                optionalText(secondFamily), null);
    }

    private static PersonDocument requiredDocument(String type, String number, String country,
            LocalDate issuedAt, LocalDate expiresAt) {
        String docType = requiredOneOf(type, "Document type is invalid.",
                DOCUMENT_TYPES.toArray(String[]::new));
        String docNumber = requiredText(number, "Document number is required.");
        return new PersonDocument(docType, docNumber, optionalText(country), issuedAt, expiresAt);
    }

    private static PersonAddress optionalAddress(String country, String city, String street) {
        if (country == null && city == null && street == null) {
            return null;
        }
        return new PersonAddress(optionalText(country), null, optionalText(city), null, null,
                optionalText(street), null, null);
    }

    private static LocalDate requiredDate(LocalDate value, String message) {
        if (value == null) {
            throw new InvalidPeopleCommandException(message);
        }
        return value;
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    private static String jsonText(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
