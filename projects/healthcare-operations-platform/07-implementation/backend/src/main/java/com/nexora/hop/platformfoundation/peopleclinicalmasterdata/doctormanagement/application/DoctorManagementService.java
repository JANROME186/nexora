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
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.InvalidPeopleCommandException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleConflictException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleCustomRuleNotImplementedException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleEntityNotFoundException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonAddress;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonName;

/**
 * Compiles the generatable outputs of {@code bcm-per-003-doctor-management/generation-plan.yaml}
 * (MVP-MOD-003-BE-001). Custom rules RN-002 (duplicate detection), RN-004 (activation with
 * verified credential), RN-005 (credential expiration cascade), RN-006 (suspended eligibility)
 * and RN-007 (portal preparation guard) are deferred to MVP-MOD-003-BE-002 and surface as explicit
 * {@link PeopleCustomRuleNotImplementedException} hooks.
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
    private final Clock clock;

    @Autowired
    public DoctorManagementService(
            DoctorRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder) {
        this(repository, tenantDirectory, auditRecorder, Clock.systemUTC());
    }

    DoctorManagementService(
            DoctorRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
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

    /** Suspension cascade to downstream eligibility projection is deferred to MVP-MOD-003-BE-002. */
    public Doctor suspend(String doctorId, String reasonCode) {
        require(doctorId);
        optionalText(reasonCode);
        throw new PeopleCustomRuleNotImplementedException("BCM-PER-003-RN-006",
                "Doctor suspension cascade to downstream eligibility is deferred to MVP-MOD-003-BE-002.");
    }

    /** Portal preparation guard is deferred to MVP-MOD-003-BE-002 (BCM-PER-003 RN-007). */
    public Doctor preparePortalAccess(String doctorId, String portalEmail) {
        require(doctorId);
        optionalText(portalEmail);
        throw new PeopleCustomRuleNotImplementedException("BCM-PER-003-RN-007",
                "Doctor portal access preparation is deferred to MVP-MOD-003-BE-002.");
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

    public ProfessionalCredential verifyCredential(String doctorId, String credentialId) {
        require(doctorId);
        requiredText(credentialId, "Credential id is required.");
        throw new PeopleCustomRuleNotImplementedException("BCM-PER-003-RN-004",
                "Credential verification with activation cascade is deferred to MVP-MOD-003-BE-002.");
    }

    public ProfessionalCredential revokeCredential(String doctorId, String credentialId) {
        require(doctorId);
        requiredText(credentialId, "Credential id is required.");
        throw new PeopleCustomRuleNotImplementedException("BCM-PER-003-RN-005",
                "Credential revocation with eligibility cascade is deferred to MVP-MOD-003-BE-002.");
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
