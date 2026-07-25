package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.adapter.in.web;

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

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.AssignSpecialtyCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.AttachCredentialCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.DoctorManagementService;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.RegisterDoctorCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.UpdateDoctorCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.Doctor;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.DoctorSnapshot;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.ProfessionalCredential;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.SpecialtyAssignment;

/**
 * Rendered controller for {@code bcm-per-003-doctor-management/openapi-source.md} (base path
 * /api/people/doctors).
 */
@RestController
@RequestMapping("/api/people/doctors")
class DoctorController {

    private final DoctorManagementService service;

    DoctorController(DoctorManagementService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<DoctorResponse>> listDoctors(@RequestParam String laboratoryId) {
        return ResponseEntity.ok(service.list(laboratoryId).stream().map(DoctorResponse::from).toList());
    }

    @GetMapping("/{doctorId}")
    ResponseEntity<DoctorResponse> getDoctor(@PathVariable String doctorId) {
        return ResponseEntity.ok(DoctorResponse.from(service.get(doctorId)));
    }

    @GetMapping("/{doctorId}/snapshot")
    ResponseEntity<DoctorSnapshot> getDoctorSnapshot(@PathVariable String doctorId) {
        return ResponseEntity.ok(service.snapshot(doctorId));
    }

    @PostMapping
    ResponseEntity<DoctorResponse> registerDoctor(@Valid @RequestBody RegisterDoctorRequest request) {
        Doctor created = service.register(new RegisterDoctorCommand(
                request.tenantId(), request.laboratoryId(), request.doctorCode(),
                request.givenName(), request.middleName(), request.familyName(),
                request.secondFamilyName(), request.doctorType(),
                request.primaryDocumentType(), request.primaryDocumentNumber(),
                request.primaryDocumentIssuingCountry(), request.primaryDocumentIssuedAt(),
                request.primaryDocumentExpiresAt(), request.addressCountry(), request.addressCity(),
                request.addressStreet()));
        return ResponseEntity.created(URI.create("/api/people/doctors/" + created.doctorId()))
                .body(DoctorResponse.from(created));
    }

    @PutMapping("/{doctorId}")
    ResponseEntity<DoctorResponse> updateDoctor(@PathVariable String doctorId,
            @Valid @RequestBody UpdateDoctorRequest request) {
        Doctor updated = service.update(doctorId, new UpdateDoctorCommand(
                request.givenName(), request.middleName(), request.familyName(),
                request.secondFamilyName(), request.doctorType(),
                request.primaryDocumentType(), request.primaryDocumentNumber(),
                request.primaryDocumentIssuingCountry(), request.primaryDocumentIssuedAt(),
                request.primaryDocumentExpiresAt(), request.addressCountry(), request.addressCity(),
                request.addressStreet()));
        return ResponseEntity.ok(DoctorResponse.from(updated));
    }

    @PostMapping("/{doctorId}/suspend")
    ResponseEntity<DoctorResponse> suspendDoctor(@PathVariable String doctorId,
            @RequestBody(required = false) SuspendDoctorRequest request) {
        String reasonCode = request == null ? null : request.reasonCode();
        return ResponseEntity.ok(DoctorResponse.from(service.suspend(doctorId, reasonCode)));
    }

    @PostMapping("/{doctorId}/retire")
    ResponseEntity<DoctorResponse> retireDoctor(@PathVariable String doctorId) {
        return ResponseEntity.ok(DoctorResponse.from(service.retire(doctorId)));
    }

    @PostMapping("/{doctorId}/portal-access/prepare")
    ResponseEntity<DoctorResponse> preparePortalAccess(@PathVariable String doctorId,
            @RequestBody(required = false) PreparePortalRequest request) {
        String email = request == null ? null : request.portalEmail();
        return ResponseEntity.ok(DoctorResponse.from(service.preparePortalAccess(doctorId, email)));
    }

    // Credentials

    @GetMapping("/{doctorId}/credentials")
    ResponseEntity<List<ProfessionalCredential>> listDoctorCredentials(@PathVariable String doctorId) {
        return ResponseEntity.ok(service.listCredentials(doctorId));
    }

    @PostMapping("/{doctorId}/credentials")
    ResponseEntity<ProfessionalCredential> attachDoctorCredential(@PathVariable String doctorId,
            @Valid @RequestBody AttachCredentialRequest request) {
        ProfessionalCredential credential = service.attachCredential(doctorId, new AttachCredentialCommand(
                request.credentialType(), request.credentialNumber(), request.issuingAuthority(),
                request.issuingCountry(), request.issuedAt(), request.expiresAt()));
        return ResponseEntity.created(URI.create("/api/people/doctors/" + doctorId + "/credentials/"
                + credential.credentialId())).body(credential);
    }

    @PostMapping("/{doctorId}/credentials/{credentialId}/verify")
    ResponseEntity<ProfessionalCredential> verifyDoctorCredential(@PathVariable String doctorId,
            @PathVariable String credentialId) {
        return ResponseEntity.ok(service.verifyCredential(doctorId, credentialId));
    }

    @PostMapping("/{doctorId}/credentials/{credentialId}/revoke")
    ResponseEntity<ProfessionalCredential> revokeDoctorCredential(@PathVariable String doctorId,
            @PathVariable String credentialId) {
        return ResponseEntity.ok(service.revokeCredential(doctorId, credentialId));
    }

    // Specialties

    @GetMapping("/{doctorId}/specialties")
    ResponseEntity<List<SpecialtyAssignment>> listSpecialtyAssignments(@PathVariable String doctorId) {
        return ResponseEntity.ok(service.listSpecialties(doctorId));
    }

    @PostMapping("/{doctorId}/specialties")
    ResponseEntity<SpecialtyAssignment> assignSpecialty(@PathVariable String doctorId,
            @Valid @RequestBody AssignSpecialtyRequest request) {
        SpecialtyAssignment assignment = service.assignSpecialty(doctorId, new AssignSpecialtyCommand(
                request.specialtyCode(), request.primary()));
        return ResponseEntity.created(URI.create("/api/people/doctors/" + doctorId + "/specialties/"
                + assignment.assignmentId())).body(assignment);
    }

    @DeleteMapping("/{doctorId}/specialties/{assignmentId}")
    ResponseEntity<Void> unassignSpecialty(@PathVariable String doctorId,
            @PathVariable String assignmentId) {
        service.unassignSpecialty(doctorId, assignmentId);
        return ResponseEntity.noContent().build();
    }

    // Requests / responses

    record SuspendDoctorRequest(String reasonCode) {
    }

    record PreparePortalRequest(String portalEmail) {
    }

    record RegisterDoctorRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String doctorCode,
            @NotBlank String givenName,
            String middleName,
            @NotBlank String familyName,
            String secondFamilyName,
            @NotBlank String doctorType,
            @NotBlank String primaryDocumentType,
            @NotBlank String primaryDocumentNumber,
            String primaryDocumentIssuingCountry,
            LocalDate primaryDocumentIssuedAt,
            LocalDate primaryDocumentExpiresAt,
            String addressCountry,
            String addressCity,
            String addressStreet) {
    }

    record UpdateDoctorRequest(
            @NotBlank String givenName,
            String middleName,
            @NotBlank String familyName,
            String secondFamilyName,
            @NotBlank String doctorType,
            @NotBlank String primaryDocumentType,
            @NotBlank String primaryDocumentNumber,
            String primaryDocumentIssuingCountry,
            LocalDate primaryDocumentIssuedAt,
            LocalDate primaryDocumentExpiresAt,
            String addressCountry,
            String addressCity,
            String addressStreet) {
    }

    record AttachCredentialRequest(
            @NotBlank String credentialType,
            @NotBlank String credentialNumber,
            @NotBlank String issuingAuthority,
            String issuingCountry,
            LocalDate issuedAt,
            LocalDate expiresAt) {
    }

    record AssignSpecialtyRequest(@NotBlank String specialtyCode, boolean primary) {
    }

    record DoctorResponse(
            String doctorId,
            String tenantId,
            String laboratoryId,
            String doctorCode,
            String givenName,
            String middleName,
            String familyName,
            String secondFamilyName,
            String fullName,
            String doctorType,
            String primaryDocumentType,
            String primaryDocumentNumberMasked,
            String status,
            String portalStatus,
            String portalEmail,
            int version,
            Instant createdAt,
            Instant updatedAt) {
        static DoctorResponse from(Doctor doctor) {
            return new DoctorResponse(
                    doctor.doctorId(), doctor.tenantId(), doctor.laboratoryId(), doctor.doctorCode(),
                    doctor.name() == null ? null : doctor.name().givenName(),
                    doctor.name() == null ? null : doctor.name().middleName(),
                    doctor.name() == null ? null : doctor.name().familyName(),
                    doctor.name() == null ? null : doctor.name().secondFamilyName(),
                    doctor.name() == null ? null : doctor.name().fullNameDisplay(),
                    doctor.doctorType(),
                    doctor.primaryDocument() == null ? null : doctor.primaryDocument().documentType(),
                    doctor.primaryDocument() == null ? null : doctor.primaryDocument().maskedNumber(),
                    doctor.status(), doctor.portalStatus(), doctor.portalEmail(),
                    doctor.version(), doctor.createdAt(), doctor.updatedAt());
        }
    }
}
