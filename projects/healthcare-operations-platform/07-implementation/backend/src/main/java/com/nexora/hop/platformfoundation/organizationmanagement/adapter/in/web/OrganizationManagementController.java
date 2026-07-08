package com.nexora.hop.platformfoundation.organizationmanagement.adapter.in.web;

import java.net.URI;
import java.time.Instant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.organizationmanagement.application.CreateBranchCommand;
import com.nexora.hop.platformfoundation.organizationmanagement.application.CreateLaboratoryCommand;
import com.nexora.hop.platformfoundation.organizationmanagement.application.CreateTenantCommand;
import com.nexora.hop.platformfoundation.organizationmanagement.application.OrganizationManagementService;
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

    @PostMapping("/platform/tenants")
    ResponseEntity<TenantResponse> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        Tenant tenant = service.createTenant(new CreateTenantCommand(request.name()));
        return ResponseEntity.created(URI.create("/api/platform/tenants/" + tenant.tenantId()))
                .body(TenantResponse.from(tenant));
    }

    @GetMapping("/platform/tenants/{tenantId}")
    ResponseEntity<TenantResponse> getTenant(@PathVariable String tenantId) {
        return ResponseEntity.ok(TenantResponse.from(service.getTenant(tenantId)));
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

    record CreateTenantRequest(@NotBlank String name) {
    }

    record CreateLaboratoryRequest(@NotBlank String tenantId, @NotBlank String name) {
    }

    record CreateBranchRequest(@NotBlank String laboratoryId, @NotBlank String name) {
    }

    record TenantResponse(String tenantId, String name, String status, Instant createdAt, Instant updatedAt) {
        static TenantResponse from(Tenant tenant) {
            return new TenantResponse(
                    tenant.tenantId(),
                    tenant.name(),
                    tenant.status(),
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
