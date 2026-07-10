package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.adapter.in.web;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application.DetectPersonDuplicatesCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application.PersonManagementService;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application.SearchPersonsQuery;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonDuplicateCandidate;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonMergeCoordination;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonSearchEntry;

/**
 * Rendered controller for {@code bcm-per-001-person-management/openapi-source.yaml} (base path
 * /api/people/persons).
 */
@RestController
@RequestMapping("/api/people/persons")
class PersonController {

    private final PersonManagementService service;

    PersonController(PersonManagementService service) {
        this.service = service;
    }

    @GetMapping("/search")
    ResponseEntity<List<PersonSearchEntry>> searchPersons(
            @RequestParam String tenantId,
            @RequestParam(required = false) String personKind,
            @RequestParam(required = false) String familyName,
            @RequestParam(required = false) String givenName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate) {
        SearchPersonsQuery query = new SearchPersonsQuery(tenantId, personKind, familyName, givenName, birthDate);
        return ResponseEntity.ok(service.search(query));
    }

    @PostMapping("/duplicates/detect")
    ResponseEntity<List<PersonDuplicateCandidate>> detectPersonDuplicates(
            @Valid @RequestBody DetectDuplicatesRequest request) {
        return ResponseEntity.ok(service.detectDuplicates(new DetectPersonDuplicatesCommand(
                request.tenantId(), request.personKind(), request.familyName(), request.givenName(),
                request.birthDate(), request.sexAtBirth(), request.nationalIdentifier())));
    }

    @PostMapping("/index/rebuild")
    ResponseEntity<PersonManagementService.PersonSearchIndexRebuildResult> rebuildPersonSearchIndex(
            @RequestParam String tenantId) {
        return ResponseEntity.accepted().body(service.rebuildIndex(tenantId));
    }

    @GetMapping("/merges/{coordinationId}")
    ResponseEntity<PersonMergeCoordinationResponse> getPersonMergeCoordination(
            @PathVariable String coordinationId) {
        return ResponseEntity.ok(PersonMergeCoordinationResponse.from(
                service.getMergeCoordination(coordinationId)));
    }

    @PostMapping("/merges")
    ResponseEntity<PersonMergeCoordinationResponse> initiatePersonMergeCoordination(
            @Valid @RequestBody InitiateMergeRequest request) {
        PersonMergeCoordination coordination = service.initiateMergeCoordination(request.tenantId(),
                request.sourceRecordId(), request.targetRecordId());
        return ResponseEntity.created(
                URI.create("/api/people/persons/merges/" + coordination.coordinationId()))
                .body(PersonMergeCoordinationResponse.from(coordination));
    }

    record DetectDuplicatesRequest(
            @NotBlank String tenantId,
            String personKind,
            String familyName,
            String givenName,
            LocalDate birthDate,
            String sexAtBirth,
            String nationalIdentifier) {
    }

    record InitiateMergeRequest(
            @NotBlank String tenantId,
            @NotBlank String sourceRecordId,
            @NotBlank String targetRecordId) {
    }

    record PersonMergeCoordinationResponse(
            String coordinationId,
            String tenantId,
            String sourceKind,
            String sourceRecordId,
            String targetKind,
            String targetRecordId,
            String status,
            boolean patientMergeApplied) {
        static PersonMergeCoordinationResponse from(PersonMergeCoordination coordination) {
            return new PersonMergeCoordinationResponse(
                    coordination.coordinationId(), coordination.tenantId(), coordination.sourceKind(),
                    coordination.sourceRecordId(), coordination.targetKind(), coordination.targetRecordId(),
                    coordination.status(), coordination.patientMergeApplied());
        }
    }
}
