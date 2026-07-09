package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.adapter.in.web;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.application.PatientRegistrationService;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.application.StartPatientRegistrationCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.domain.PatientRegistrationRequest;

/**
 * Rendered controller for {@code bcm-att-002-patient-registration/openapi-source.yaml} (base path
 * /api/care-delivery/patient-registrations).
 */
@RestController
@RequestMapping("/api/care-delivery/patient-registrations")
class PatientRegistrationController {

    private final PatientRegistrationService service;

    PatientRegistrationController(PatientRegistrationService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<PatientRegistrationRequest>> listPatientRegistrations(@RequestParam String tenantId) {
        return ResponseEntity.ok(service.list(tenantId));
    }

    @GetMapping("/{registrationRequestId}")
    ResponseEntity<PatientRegistrationRequest> getPatientRegistration(
            @PathVariable String registrationRequestId) {
        return ResponseEntity.ok(service.get(registrationRequestId));
    }

    @PostMapping
    ResponseEntity<PatientRegistrationRequest> startPatientRegistration(
            @Valid @RequestBody StartRegistrationRequest request) {
        PatientRegistrationRequest started = service.start(new StartPatientRegistrationCommand(
                request.tenantId(), request.laboratoryId(), request.branchId(),
                request.intakeChannel(), request.registrationKind(), request.givenName(),
                request.familyName(), request.birthDate(), request.documentType(),
                request.documentNumber(), request.draftPatientCode(), request.actorId()));
        return ResponseEntity.created(URI.create("/api/care-delivery/patient-registrations/"
                + started.registrationRequestId())).body(started);
    }

    @PostMapping("/{registrationRequestId}/commit")
    ResponseEntity<PatientRegistrationRequest> commitPatientRegistration(
            @PathVariable String registrationRequestId) {
        return ResponseEntity.ok(service.commit(registrationRequestId));
    }

    @PostMapping("/{registrationRequestId}/cancel")
    ResponseEntity<PatientRegistrationRequest> cancelPatientRegistration(
            @PathVariable String registrationRequestId,
            @RequestBody(required = false) CancelRegistrationRequest request) {
        String reasonCode = request == null ? null : request.reasonCode();
        return ResponseEntity.ok(service.cancel(registrationRequestId, reasonCode));
    }

    record StartRegistrationRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String branchId,
            @NotBlank String intakeChannel,
            @NotBlank String registrationKind,
            @NotBlank String givenName,
            @NotBlank String familyName,
            LocalDate birthDate,
            @NotBlank String documentType,
            @NotBlank String documentNumber,
            String draftPatientCode,
            String actorId) {
    }

    record CancelRegistrationRequest(String reasonCode) {
    }
}
