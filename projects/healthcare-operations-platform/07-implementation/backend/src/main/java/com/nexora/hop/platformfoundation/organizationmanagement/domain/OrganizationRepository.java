package com.nexora.hop.platformfoundation.organizationmanagement.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrganizationRepository {

    Tenant saveTenant(Tenant tenant);

    Laboratory saveLaboratory(Laboratory laboratory);

    Branch saveBranch(Branch branch);

    Optional<Tenant> findTenantById(String tenantId);

    Optional<Tenant> findTenantByCode(String code);

    List<Tenant> findAllTenants();

    Tenant updateTenantStatus(String tenantId, String status, Instant updatedAt);

    Optional<Laboratory> findLaboratoryById(String laboratoryId);

    Optional<Branch> findBranchById(String branchId);
}
