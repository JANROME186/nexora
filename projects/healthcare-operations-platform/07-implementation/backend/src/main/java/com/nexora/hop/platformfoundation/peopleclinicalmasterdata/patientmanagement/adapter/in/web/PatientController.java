package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.adapter.in.web;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.AttachPatientDocumentCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.AttachPatientRepresentativeCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.PatientManagementService;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.RecordPatientConsentCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.RegisterPatientCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.UpdatePatientCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.Patient;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientConsent;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientDocument;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientRepresentative;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientSnapshot;

/**
 * Rendered controller for {@code bcm-per-002-patient-management/openapi-source.yaml} (base path
 * /api/people/patients). Custom-rule operations declared as {@code generatable: false} throw
 * {@link com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleCustomRuleNotImplementedException}
 * inside the service and are mapped to HTTP 501 by the shared exception handler.
 */
@RestController
@RequestMapping("/api/people/patients")
class PatientController {

    private final PatientManagementService service;

    PatientController(PatientManagementService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<PatientResponse>> listPatients(@RequestParam String laboratoryId) {
        return ResponseEntity.ok(service.list(laboratoryId).stream().map(PatientResponse::from).toList());
    }

    @GetMapping("/{patientId}")
    ResponseEntity<PatientResponse> getPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(PatientResponse.from(service.get(patientId)));
    }

    @GetMapping("/{patientId}/snapshot")
    ResponseEntity<PatientSnapshot> getPatientSnapshot(@PathVariable String patientId) {
        return service.findSnapshot(patientId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleEntityNotFoundException(
                        "Patient was not found."));
    }

    @PostMapping
    ResponseEntity<PatientResponse> registerPatient(@Valid @RequestBody RegisterPatientRequest request) {
        Patient created = service.register(toRegisterPatientCommand(request));
        return ResponseEntity.created(URI.create("/api/people/patients/" + created.patientId()))
                .body(PatientResponse.from(created));
    }

    @PutMapping("/{patientId}")
    ResponseEntity<PatientResponse> updatePatient(@PathVariable String patientId,
            @Valid @RequestBody UpdatePatientRequest request) {
        Patient updated = service.update(patientId, toUpdatePatientCommand(request));
        return ResponseEntity.ok(PatientResponse.from(updated));
    }

    @PostMapping("/{patientId}/deactivate")
    ResponseEntity<PatientResponse> deactivatePatient(@PathVariable String patientId) {
        return ResponseEntity.ok(PatientResponse.from(service.deactivate(patientId)));
    }

    @PostMapping("/{patientId}/merge")
    ResponseEntity<PatientResponse> mergePatient(@PathVariable String patientId,
            @Valid @RequestBody MergePatientRequest request) {
        return ResponseEntity.ok(PatientResponse.from(service.merge(patientId, request.survivingPatientId())));
    }

    // -- Representatives ----------------------------------------------------------------------

    @GetMapping("/{patientId}/representatives")
    ResponseEntity<List<PatientRepresentative>> listPatientRepresentatives(@PathVariable String patientId) {
        return ResponseEntity.ok(service.listRepresentatives(patientId));
    }

    @PostMapping("/{patientId}/representatives")
    ResponseEntity<PatientRepresentative> attachPatientRepresentative(@PathVariable String patientId,
            @Valid @RequestBody AttachPatientRepresentativeRequest request) {
        PatientRepresentative representative = service.attachRepresentative(patientId,
                new AttachPatientRepresentativeCommand(request.relationship(), request.givenName(),
                        request.middleName(), request.familyName(), request.secondFamilyName(),
                        request.documentType(), request.documentNumber(),
                        request.authorizationFrom(), request.authorizationTo()));
        return ResponseEntity.created(URI.create("/api/people/patients/" + patientId + "/representatives/"
                + representative.representativeId())).body(representative);
    }

    @PutMapping("/{patientId}/representatives/{representativeId}")
    ResponseEntity<PatientRepresentative> updatePatientRepresentative(@PathVariable String patientId,
            @PathVariable String representativeId,
            @Valid @RequestBody AttachPatientRepresentativeRequest request) {
        // Update behaves like a fresh attach for the generatable slice; the append-only revocation
        // history plus authorization-window enforcement land in MVP-MOD-003-BE-002.
        PatientRepresentative representative = service.attachRepresentative(patientId,
                new AttachPatientRepresentativeCommand(request.relationship(), request.givenName(),
                        request.middleName(), request.familyName(), request.secondFamilyName(),
                        request.documentType(), request.documentNumber(),
                        request.authorizationFrom(), request.authorizationTo()));
        // Consume representativeId to acknowledge the path parameter.
        if (representativeId == null || representativeId.isBlank()) {
            throw new IllegalStateException("representativeId must be present");
        }
        return ResponseEntity.ok(representative);
    }

    @PostMapping("/{patientId}/representatives/{representativeId}/revoke")
    ResponseEntity<PatientRepresentative> revokePatientRepresentative(@PathVariable String patientId,
            @PathVariable String representativeId) {
        return ResponseEntity.ok(service.revokeRepresentative(patientId, representativeId));
    }

    // -- Consents ----------------------------------------------------------------------------

    @GetMapping("/{patientId}/consents")
    ResponseEntity<List<PatientConsent>> listPatientConsents(@PathVariable String patientId) {
        return ResponseEntity.ok(service.listConsents(patientId));
    }

    @PostMapping("/{patientId}/consents")
    ResponseEntity<PatientConsent> recordPatientConsent(@PathVariable String patientId,
            @Valid @RequestBody RecordPatientConsentRequest request) {
        PatientConsent consent = service.recordConsent(patientId, new RecordPatientConsentCommand(
                request.consentType(), request.granted(), request.grantedBy(), request.evidenceReference()));
        return ResponseEntity.created(URI.create(
                "/api/people/patients/" + patientId + "/consents/" + consent.consentId())).body(consent);
    }

    @PostMapping("/{patientId}/consents/{consentId}/revoke")
    ResponseEntity<PatientConsent> revokePatientConsent(@PathVariable String patientId,
            @PathVariable String consentId) {
        return ResponseEntity.ok(service.revokeConsent(patientId, consentId));
    }

    // -- Documents ---------------------------------------------------------------------------

    @GetMapping("/{patientId}/documents")
    ResponseEntity<List<PatientDocument>> listPatientDocuments(@PathVariable String patientId) {
        return ResponseEntity.ok(service.listDocuments(patientId));
    }

    @PostMapping("/{patientId}/documents")
    ResponseEntity<PatientDocument> attachPatientDocument(@PathVariable String patientId,
            @Valid @RequestBody AttachPatientDocumentRequest request) {
        PatientDocument document = service.attachDocument(patientId, new AttachPatientDocumentCommand(
                request.category(), request.fileReference(), request.expiresAt()));
        return ResponseEntity.created(URI.create(
                "/api/people/patients/" + patientId + "/documents/" + document.documentId())).body(document);
    }

    @DeleteMapping("/{patientId}/documents/{documentId}")
    ResponseEntity<Void> removePatientDocument(@PathVariable String patientId, @PathVariable String documentId) {
        service.removeDocument(patientId, documentId);
        return ResponseEntity.noContent().build();
    }

    private static RegisterPatientCommand toRegisterPatientCommand(RegisterPatientRequest request) {
        UpdatePatientCommand profile = toUpdatePatientCommand(request);
        return new RegisterPatientCommand(
                request.tenantId(), request.laboratoryId(), request.patientCode(), profile.givenName(),
                profile.middleName(), profile.familyName(), profile.secondFamilyName(), profile.preferredName(),
                profile.birthDate(), profile.sexAtBirth(), profile.primaryDocumentType(),
                profile.primaryDocumentNumber(), profile.primaryDocumentIssuingCountry(),
                profile.primaryDocumentIssuedAt(), profile.primaryDocumentExpiresAt(), profile.addressCountry(),
                profile.addressState(), profile.addressCity(), profile.addressPostalCode(), profile.addressStreet(),
                profile.preferredLocale());
    }

    private static UpdatePatientCommand toUpdatePatientCommand(UpdatePatientRequest request) {
        return toUpdatePatientCommand((PatientProfileRequest) request);
    }

    private static UpdatePatientCommand toUpdatePatientCommand(PatientProfileRequest request) {
        return new UpdatePatientCommand(
                request.givenName(), request.middleName(), request.familyName(), request.secondFamilyName(),
                request.preferredName(), request.birthDate(), request.sexAtBirth(), request.primaryDocumentType(),
                request.primaryDocumentNumber(), request.primaryDocumentIssuingCountry(),
                request.primaryDocumentIssuedAt(), request.primaryDocumentExpiresAt(), request.addressCountry(),
                request.addressState(), request.addressCity(), request.addressPostalCode(), request.addressStreet(),
                request.preferredLocale());
    }

    // -- Request/response records --------------------------------------------------------------

    record MergePatientRequest(@NotBlank String survivingPatientId) {
    }

    private interface PatientProfileRequest {

        String givenName();

        String middleName();

        String familyName();

        String secondFamilyName();

        String preferredName();

        LocalDate birthDate();

        String sexAtBirth();

        String primaryDocumentType();

        String primaryDocumentNumber();

        String primaryDocumentIssuingCountry();

        LocalDate primaryDocumentIssuedAt();

        LocalDate primaryDocumentExpiresAt();

        String addressCountry();

        String addressState();

        String addressCity();

        String addressPostalCode();

        String addressStreet();

        String preferredLocale();
    }

    record RegisterPatientRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String patientCode,
            @NotBlank String givenName,
            String middleName,
            @NotBlank String familyName,
            String secondFamilyName,
            String preferredName,
            LocalDate birthDate,
            @NotBlank String sexAtBirth,
            @NotBlank String primaryDocumentType,
            @NotBlank String primaryDocumentNumber,
            String primaryDocumentIssuingCountry,
            LocalDate primaryDocumentIssuedAt,
            LocalDate primaryDocumentExpiresAt,
            String addressCountry,
            String addressState,
            String addressCity,
            String addressPostalCode,
            String addressStreet,
            String preferredLocale) implements PatientProfileRequest {
    }

    record UpdatePatientRequest(
            @NotBlank String givenName,
            String middleName,
            @NotBlank String familyName,
            String secondFamilyName,
            String preferredName,
            LocalDate birthDate,
            @NotBlank String sexAtBirth,
            @NotBlank String primaryDocumentType,
            @NotBlank String primaryDocumentNumber,
            String primaryDocumentIssuingCountry,
            LocalDate primaryDocumentIssuedAt,
            LocalDate primaryDocumentExpiresAt,
            String addressCountry,
            String addressState,
            String addressCity,
            String addressPostalCode,
            String addressStreet,
            String preferredLocale) implements PatientProfileRequest {
    }

    record AttachPatientRepresentativeRequest(
            @NotBlank String relationship,
            @NotBlank String givenName,
            String middleName,
            @NotBlank String familyName,
            String secondFamilyName,
            @NotBlank String documentType,
            @NotBlank String documentNumber,
            LocalDate authorizationFrom,
            LocalDate authorizationTo) {
    }

    record RecordPatientConsentRequest(
            @NotBlank String consentType,
            boolean granted,
            @NotBlank String grantedBy,
            String evidenceReference) {
    }

    record AttachPatientDocumentRequest(
            @NotBlank String category,
            @NotBlank String fileReference,
            LocalDate expiresAt) {
    }

    record PatientResponse(
            String patientId,
            String tenantId,
            String laboratoryId,
            String patientCode,
            String givenName,
            String middleName,
            String familyName,
            String secondFamilyName,
            String preferredName,
            String fullName,
            LocalDate birthDate,
            String sexAtBirth,
            String primaryDocumentType,
            String primaryDocumentNumberMasked,
            String status,
            int version,
            Instant createdAt,
            Instant updatedAt) {
        static PatientResponse from(Patient patient) {
            return new PatientResponse(
                    patient.patientId(),
                    patient.tenantId(),
                    patient.laboratoryId(),
                    patient.patientCode(),
                    patient.name() == null ? null : patient.name().givenName(),
                    patient.name() == null ? null : patient.name().middleName(),
                    patient.name() == null ? null : patient.name().familyName(),
                    patient.name() == null ? null : patient.name().secondFamilyName(),
                    patient.name() == null ? null : patient.name().preferredName(),
                    patient.name() == null ? null : patient.name().fullNameDisplay(),
                    patient.birthDate(),
                    patient.sexAtBirth(),
                    patient.primaryDocument() == null ? null : patient.primaryDocument().documentType(),
                    patient.primaryDocument() == null ? null : patient.primaryDocument().maskedNumber(),
                    patient.status(),
                    patient.version(),
                    patient.createdAt(),
                    patient.updatedAt());
        }
    }
}
