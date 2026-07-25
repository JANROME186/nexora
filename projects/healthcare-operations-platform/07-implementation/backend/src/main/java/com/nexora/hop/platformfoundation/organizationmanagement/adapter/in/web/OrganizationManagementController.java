package com.nexora.hop.platformfoundation.organizationmanagement.adapter.in.web;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.organizationmanagement.application.CreateBranchCommand;
import com.nexora.hop.platformfoundation.organizationmanagement.application.CreateLaboratoryCommand;
import com.nexora.hop.platformfoundation.organizationmanagement.application.OrganizationManagementService;
import com.nexora.hop.platformfoundation.organizationmanagement.application.ProvisionTenantCommand;
import com.nexora.hop.platformfoundation.organizationmanagement.application.UpdateTenantStatusCommand;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.Branch;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.Laboratory;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.Tenant;

@RestController
@RequestMapping("/api")
class OrganizationManagementController {

    private final OrganizationManagementService service;

    OrganizationManagementController(OrganizationManagementService service) {
        this.service = service;
    }

    /**
     * BCM-ORG-001 {@code provisionTenant}. {@code legalName} falls back to the pre-BE-001 {@code
     * name} field, and {@code code} is auto-derived from the resolved legal name when omitted, so
     * every pre-existing module test fixture that still bootstraps a tenant with a bare {@code
     * name} keeps working unchanged against the richer BCM-ORG-001 model.
     */
    @PostMapping("/platform/tenants")
    ResponseEntity<TenantResponse> provisionTenant(@Valid @RequestBody ProvisionTenantRequest request) {
        String legalName = StringUtils.hasText(request.legalName()) ? request.legalName() : request.name();
        String code = StringUtils.hasText(request.code()) ? request.code() : synthesizeCode(legalName);
        Tenant tenant = service.provisionTenant(
                new ProvisionTenantCommand(code, legalName, request.tradeName(), request.taxId(), request.tier()));
        return ResponseEntity.created(URI.create("/api/platform/tenants/" + tenant.tenantId()))
                .body(TenantResponse.from(tenant));
    }

    private static String synthesizeCode(String legalName) {
        String base = StringUtils.hasText(legalName)
                ? legalName.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+|-+$)", "")
                : "";
        String slug = StringUtils.hasText(base) ? base : "tenant";
        return slug + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /** BCM-ORG-001 {@code listTenants}. */
    @GetMapping("/platform/tenants")
    ResponseEntity<List<TenantResponse>> listTenants() {
        return ResponseEntity.ok(service.listTenants().stream().map(TenantResponse::from).toList());
    }

    @GetMapping("/platform/tenants/{tenantId}")
    ResponseEntity<TenantResponse> getTenant(@PathVariable String tenantId) {
        return ResponseEntity.ok(TenantResponse.from(service.getTenant(tenantId)));
    }

    /**
     * BCM-ORG-001 {@code updateTenantStatus}: the tenant-impact-triage-runbook.md operational
     * control used to suspend or archive a tenant (COM-MOD-012-BE-001).
     */
    @PutMapping("/platform/tenants/{tenantId}/status")
    ResponseEntity<TenantResponse> updateTenantStatus(
            @PathVariable String tenantId, @Valid @RequestBody UpdateTenantStatusRequest request) {
        Tenant tenant = service.updateTenantStatus(
                new UpdateTenantStatusCommand(tenantId, request.status(), request.reason()));
        return ResponseEntity.ok(TenantResponse.from(tenant));
    }

    @PostMapping("/organization/laboratories")
    ResponseEntity<LaboratoryResponse> createLaboratory(@Valid @RequestBody CreateLaboratoryRequest request) {
        Laboratory laboratory = service.createLaboratory(new CreateLaboratoryCommand(request.tenantId(), request.name()));
        return ResponseEntity.created(URI.create("/api/organization/laboratories/" + laboratory.laboratoryId()))
                .body(LaboratoryResponse.from(laboratory));
    }

    @GetMapping("/organization/laboratories/{laboratoryId}")
    ResponseEntity<LaboratoryResponse> getLaboratory(@PathVariable String laboratoryId) {
        return ResponseEntity.ok(LaboratoryResponse.from(service.getLaboratory(laboratoryId)));
    }

    @PostMapping("/organization/branches")
    ResponseEntity<BranchResponse> createBranch(@Valid @RequestBody CreateBranchRequest request) {
        Branch branch = service.createBranch(new CreateBranchCommand(request.laboratoryId(), request.name()));
        return ResponseEntity.created(URI.create("/api/organization/branches/" + branch.branchId()))
                .body(BranchResponse.from(branch));
    }

    @GetMapping("/organization/branches/{branchId}")
    ResponseEntity<BranchResponse> getBranch(@PathVariable String branchId) {
        return ResponseEntity.ok(BranchResponse.from(service.getBranch(branchId)));
    }

    /**
     * {@code code} and {@code legalName} are validated as required by {@code
     * OrganizationManagementService.provisionTenant} rather than by bean validation here, because
     * {@code name} (pre-BE-001 compatibility field) is an acceptable substitute for {@code
     * legalName} and {@code code} can be auto-derived; see {@link #provisionTenant}.
     */
    record ProvisionTenantRequest(
            String code, String legalName, String tradeName, String taxId, String tier, String name) {
    }

    record UpdateTenantStatusRequest(@NotBlank String status, String reason) {
    }

    record CreateLaboratoryRequest(@NotBlank String tenantId, @NotBlank String name) {
    }

    record CreateBranchRequest(@NotBlank String laboratoryId, @NotBlank String name) {
    }

    record TenantResponse(
            String tenantId,
            String code,
            String legalName,
            String tradeName,
            String taxId,
            String status,
            String tier,
            String isolationStrategy,
            Instant createdAt,
            Instant updatedAt) {
        static TenantResponse from(Tenant tenant) {
            return new TenantResponse(
                    tenant.tenantId(),
                    tenant.code(),
                    tenant.legalName(),
                    tenant.tradeName(),
                    tenant.taxId(),
                    tenant.status(),
                    tenant.tier(),
                    tenant.isolationStrategy(),
                    tenant.createdAt(),
                    tenant.updatedAt());
        }
    }

    record LaboratoryResponse(
            String laboratoryId,
            String tenantId,
            String name,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        static LaboratoryResponse from(Laboratory laboratory) {
            return new LaboratoryResponse(
                    laboratory.laboratoryId(),
                    laboratory.tenantId(),
                    laboratory.name(),
                    laboratory.status(),
                    laboratory.createdAt(),
                    laboratory.updatedAt());
        }
    }

    record BranchResponse(
            String branchId,
            String tenantId,
            String laboratoryId,
            String name,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        static BranchResponse from(Branch branch) {
            return new BranchResponse(
                    branch.branchId(),
                    branch.tenantId(),
                    branch.laboratoryId(),
                    branch.name(),
                    branch.status(),
                    branch.createdAt(),
                    branch.updatedAt());
        }
    }
}
