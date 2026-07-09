package com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.adapter.in.web;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.application.CreateTestDefinitionCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.application.TestCatalogService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.application.UpdateTestDefinitionCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestDefinition;

/** Rendered controller for bcm-svc-002-test-catalog/openapi-source.yaml (base path /api/catalog/tests). */
@RestController
@RequestMapping("/api/catalog/tests")
class TestDefinitionController {

    private final TestCatalogService service;

    TestDefinitionController(TestCatalogService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<TestDefinitionResponse>> listTests(@RequestParam String laboratoryId) {
        return ResponseEntity.ok(service.list(laboratoryId).stream().map(TestDefinitionResponse::from).toList());
    }

    @GetMapping("/{testId}")
    ResponseEntity<TestDefinitionResponse> getTest(@PathVariable String testId) {
        return ResponseEntity.ok(TestDefinitionResponse.from(service.get(testId)));
    }

    @PostMapping
    ResponseEntity<TestDefinitionResponse> createTest(@Valid @RequestBody CreateTestDefinitionRequest request) {
        TestDefinition created = service.create(new CreateTestDefinitionCommand(
                request.tenantId(), request.laboratoryId(), request.code(), request.nameEn(), request.nameEs(),
                request.methodology(), request.measurementUnit(), request.resultType(),
                request.turnaroundTimeHours(), request.analyteRefIds(), request.sampleRequirementRefIds()));
        return ResponseEntity.created(URI.create("/api/catalog/tests/" + created.testDefinitionId()))
                .body(TestDefinitionResponse.from(created));
    }

    @PutMapping("/{testId}")
    ResponseEntity<TestDefinitionResponse> updateTest(
            @PathVariable String testId, @Valid @RequestBody UpdateTestDefinitionRequest request) {
        TestDefinition updated = service.update(testId, new UpdateTestDefinitionCommand(
                request.code(), request.nameEn(), request.nameEs(), request.methodology(),
                request.measurementUnit(), request.resultType(), request.turnaroundTimeHours(),
                request.analyteRefIds(), request.sampleRequirementRefIds()));
        return ResponseEntity.ok(TestDefinitionResponse.from(updated));
    }

    @PostMapping("/{testId}/publish")
    ResponseEntity<TestDefinitionResponse> publishTest(@PathVariable String testId) {
        return ResponseEntity.ok(TestDefinitionResponse.from(service.publish(testId)));
    }

    @PostMapping("/{testId}/deprecate")
    ResponseEntity<TestDefinitionResponse> deprecateTest(@PathVariable String testId) {
        return ResponseEntity.ok(TestDefinitionResponse.from(service.deprecate(testId)));
    }

    @GetMapping("/{testId}/published-snapshot")
    ResponseEntity<TestDefinitionResponse> getPublishedTestSnapshot(@PathVariable String testId) {
        return ResponseEntity.ok(TestDefinitionResponse.from(service.getPublishedSnapshot(testId)));
    }

    record CreateTestDefinitionRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String code,
            @NotBlank String nameEn,
            @NotBlank String nameEs,
            String methodology,
            String measurementUnit,
            @NotBlank String resultType,
            Integer turnaroundTimeHours,
            List<String> analyteRefIds,
            List<String> sampleRequirementRefIds) {
    }

    record UpdateTestDefinitionRequest(
            @NotBlank String code,
            @NotBlank String nameEn,
            @NotBlank String nameEs,
            String methodology,
            String measurementUnit,
            @NotBlank String resultType,
            Integer turnaroundTimeHours,
            List<String> analyteRefIds,
            List<String> sampleRequirementRefIds) {
    }

    record TestDefinitionResponse(
            String testDefinitionId,
            String tenantId,
            String laboratoryId,
            String code,
            String nameEn,
            String nameEs,
            String methodology,
            String measurementUnit,
            String resultType,
            Integer turnaroundTimeHours,
            String status,
            int version,
            Instant createdAt,
            Instant updatedAt) {
        static TestDefinitionResponse from(TestDefinition entity) {
            return new TestDefinitionResponse(
                    entity.testDefinitionId(),
                    entity.tenantId(),
                    entity.laboratoryId(),
                    entity.code(),
                    entity.name().en(),
                    entity.name().es(),
                    entity.methodology(),
                    entity.measurementUnit(),
                    entity.resultType(),
                    entity.turnaroundTimeHours(),
                    entity.status(),
                    entity.version(),
                    entity.createdAt(),
                    entity.updatedAt());
        }
    }
}
