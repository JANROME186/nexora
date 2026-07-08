package com.nexora.hop.platformfoundation.organizationmanagement.domain;

import java.util.Optional;

public interface OrganizationRepository {

    Tenant saveTenant(Tenant tenant);

    Laboratory saveLaboratory(Laboratory laboratory);

    Branch saveBranch(Branch branch);

    Optional<Tenant> findTenantById(String tenantId);

    Optional<Laboratory> findLaboratoryById(String laboratoryId);

    Optional<Branch> findBranchById(String branchId);
}
