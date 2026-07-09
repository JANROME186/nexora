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
    ResponseEntity<Void> rebuildPersonSearchIndex(@RequestParam String tenantId) {
        service.rebuildIndex(tenantId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/merges/{coordinationId}")
    ResponseEntity<PersonMergeCoordinationResponse> getPersonMergeCoordination(
            @PathVariable String coordinationId) {
        // No merge coordination is persisted yet; expose the read side as an explicit
        // not-implemented hook without going through the service so the controller signature is
        // still discoverable in Spring routes for the contract test.
        return ResponseEntity.status(501)
                .body(new PersonMergeCoordinationResponse(coordinationId, "not_implemented",
                        "MVP-MOD-003-BE-002"));
    }

    @PostMapping("/merges")
    ResponseEntity<PersonMergeCoordinationResponse> initiatePersonMergeCoordination(
            @Valid @RequestBody InitiateMergeRequest request) {
        service.initiateMergeCoordination(request.tenantId(), request.sourceRecordId(),
                request.targetRecordId());
        // Unreachable: service throws PeopleCustomRuleNotImplementedException, mapped to HTTP 501
        // by the shared exception handler. Kept as a returnable value for compile safety.
        return ResponseEntity.created(URI.create("/api/people/persons/merges/deferred"))
                .body(new PersonMergeCoordinationResponse("deferred", "not_implemented",
                        "MVP-MOD-003-BE-002"));
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

    record PersonMergeCoordinationResponse(String coordinationId, String status, String backlogItem) {
    }
}
