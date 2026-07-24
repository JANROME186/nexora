package com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.adapter.in.web;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.application.PackageInstallationService;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.PackageInstallation;

/** Rendered controller for bcm-plt-011/openapi-source.yaml {@code /tenants/{tenantId}/installations} and {@code .../upgrade}. */
@RestController
@RequestMapping("/api/marketplace/installations/{tenantId}")
class PackageInstallationController {

    private final PackageInstallationService service;

    PackageInstallationController(PackageInstallationService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<InstallationResponse>> listInstallations(@PathVariable String tenantId) {
        return ResponseEntity.ok(service.listInstallations(tenantId).stream().map(InstallationResponse::from).toList());
    }

    @PostMapping
    ResponseEntity<InstallationResponse> installPackage(
            @PathVariable String tenantId, @Valid @RequestBody InstallPackageRequest request) {
        PackageInstallation created = service.installPackage(
                tenantId, request.packageId(), request.version(), request.entitlementId(), request.actorId());
        return ResponseEntity.created(
                        URI.create("/api/marketplace/installations/" + tenantId + "/" + created.installationId()))
                .body(InstallationResponse.from(created));
    }

    @PostMapping("/{installationId}/activate")
    ResponseEntity<InstallationResponse> activatePackage(
            @PathVariable String tenantId, @PathVariable String installationId,
            @Valid @RequestBody ActorRequest request) {
        return ResponseEntity.ok(
                InstallationResponse.from(service.activatePackage(tenantId, installationId, request.actorId())));
    }

    @PostMapping("/{installationId}/suspend")
    ResponseEntity<InstallationResponse> suspendPackage(
            @PathVariable String tenantId, @PathVariable String installationId,
            @Valid @RequestBody ActorRequest request) {
        return ResponseEntity.ok(
                InstallationResponse.from(service.suspendPackage(tenantId, installationId, request.actorId())));
    }

    @PostMapping("/{installationId}/uninstall")
    ResponseEntity<InstallationResponse> uninstallPackage(
            @PathVariable String tenantId, @PathVariable String installationId,
            @Valid @RequestBody ActorRequest request) {
        return ResponseEntity.ok(
                InstallationResponse.from(service.uninstallPackage(tenantId, installationId, request.actorId())));
    }

    @PostMapping("/{installationId}/upgrade")
    ResponseEntity<InstallationResponse> upgradePackage(
            @PathVariable String tenantId, @PathVariable String installationId,
            @Valid @RequestBody UpgradePackageRequest request) {
        return ResponseEntity.ok(InstallationResponse.from(
                service.upgradePackage(tenantId, installationId, request.targetVersion(), request.actorId())));
    }

    @PostMapping("/{installationId}/upgrade/rollback")
    ResponseEntity<InstallationResponse> rollbackPackage(
            @PathVariable String tenantId, @PathVariable String installationId,
            @Valid @RequestBody ActorRequest request) {
        return ResponseEntity.ok(
                InstallationResponse.from(service.rollbackPackage(tenantId, installationId, request.actorId())));
    }

    record InstallPackageRequest(
            @NotBlank String packageId, @NotBlank String version, String entitlementId, @NotBlank String actorId) {
    }

    record UpgradePackageRequest(@NotBlank String targetVersion, @NotBlank String actorId) {
    }

    record ActorRequest(@NotBlank String actorId) {
    }

    record InstallationResponse(
            String installationId, String tenantId, String packageId, String entitlementId, String version,
            String lifecycleStatus, String rollbackCheckpointVersion, Instant createdAt, Instant updatedAt) {
        static InstallationResponse from(PackageInstallation entity) {
            return new InstallationResponse(
                    entity.installationId(), entity.tenantId(), entity.packageId(), entity.entitlementId(),
                    entity.version(), entity.lifecycleStatus(), entity.rollbackCheckpointVersion(),
                    entity.audit().createdAt().atZone(ZoneOffset.UTC).toInstant(),
                    entity.audit().updatedAt().atZone(ZoneOffset.UTC).toInstant());
        }
    }
}
