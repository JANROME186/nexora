package com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.adapter.in.web;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.application.PackageCatalogService;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.MarketplacePackage;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersion;

/** Rendered controller for bcm-plt-011/openapi-source.yaml {@code /packages} and {@code /packages/{packageId}/versions/{version}}. */
@RestController
@RequestMapping("/api/marketplace/packages")
class PackageCatalogController {

    private final PackageCatalogService service;

    PackageCatalogController(PackageCatalogService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<PackageResponse> submitPackage(@Valid @RequestBody SubmitPackageRequest request) {
        MarketplacePackage created = service.submitPackage(
                request.code(), request.name(), request.category(), request.capabilityMappings(),
                request.initialVersion(), request.actorId());
        return ResponseEntity.created(URI.create("/api/marketplace/packages/" + created.packageId()))
                .body(PackageResponse.from(created));
    }

    @GetMapping
    ResponseEntity<List<PackageResponse>> listPublishedPackages() {
        return ResponseEntity.ok(service.listPublishedPackages().stream().map(PackageResponse::from).toList());
    }

    @GetMapping("/{packageId}")
    ResponseEntity<PackageResponse> getPackage(@PathVariable String packageId) {
        return ResponseEntity.ok(PackageResponse.from(service.getPackage(packageId)));
    }

    @PostMapping("/{packageId}/publish")
    ResponseEntity<PackageResponse> publishPackage(
            @PathVariable String packageId, @Valid @RequestBody PublishPackageRequest request) {
        return ResponseEntity.ok(
                PackageResponse.from(service.publishPackage(packageId, request.version(), request.actorId())));
    }

    @GetMapping("/{packageId}/versions/{version}")
    ResponseEntity<PackageVersionResponse> getPackageVersion(
            @PathVariable String packageId, @PathVariable String version) {
        return ResponseEntity.ok(PackageVersionResponse.from(service.getPackageVersion(packageId, version)));
    }

    @PostMapping("/{packageId}/versions/{version}/certify")
    ResponseEntity<PackageVersionResponse> certifyPackageVersion(
            @PathVariable String packageId, @PathVariable String version,
            @Valid @RequestBody CertifyVersionRequest request) {
        return ResponseEntity.ok(PackageVersionResponse.from(service.certifyPackageVersion(
                packageId, version, request.compatibilityApproved(), request.securityReviewApproved(),
                request.supportModelApproved(), request.telemetryModelApproved(), request.actorId())));
    }

    @PostMapping("/{packageId}/versions/{version}/retire")
    ResponseEntity<PackageVersionResponse> retirePackageVersion(
            @PathVariable String packageId, @PathVariable String version,
            @Valid @RequestBody ActorRequest request) {
        return ResponseEntity.ok(
                PackageVersionResponse.from(service.retirePackageVersion(packageId, version, request.actorId())));
    }

    record SubmitPackageRequest(
            @NotBlank String code, @NotBlank String name, @NotBlank String category,
            @NotEmpty List<String> capabilityMappings, @NotBlank String initialVersion, @NotBlank String actorId) {
    }

    record PublishPackageRequest(@NotBlank String version, @NotBlank String actorId) {
    }

    record CertifyVersionRequest(
            boolean compatibilityApproved, boolean securityReviewApproved, boolean supportModelApproved,
            boolean telemetryModelApproved, @NotBlank String actorId) {
    }

    record ActorRequest(@NotBlank String actorId) {
    }

    record PackageResponse(
            String packageId, String code, String name, String category, List<String> capabilityMappings,
            String status, Instant createdAt, Instant updatedAt) {
        static PackageResponse from(MarketplacePackage entity) {
            return new PackageResponse(
                    entity.packageId(), entity.code(), entity.name(), entity.category(), entity.capabilityMappings(),
                    entity.status(), entity.audit().createdAt().atZone(ZoneOffset.UTC).toInstant(),
                    entity.audit().updatedAt().atZone(ZoneOffset.UTC).toInstant());
        }
    }

    record PackageVersionResponse(
            String versionId, String packageId, String version, String lifecycleStatus,
            boolean compatibilityApproved, boolean securityReviewApproved, boolean supportModelApproved,
            boolean telemetryModelApproved) {
        static PackageVersionResponse from(PackageVersion entity) {
            return new PackageVersionResponse(
                    entity.versionId(), entity.packageId(), entity.version(), entity.lifecycleStatus(),
                    entity.compatibilityApproved(), entity.securityReviewApproved(), entity.supportModelApproved(),
                    entity.telemetryModelApproved());
        }
    }
}
