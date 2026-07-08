package com.nexora.hop.platformfoundation.organizationmanagement.adapter.out.memory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.organizationmanagement.domain.Branch;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.Laboratory;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.OrganizationRepository;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.Tenant;

@Repository
@Profile("!local")
class InMemoryOrganizationRepository implements OrganizationRepository {

    private final Map<String, Tenant> tenants = new ConcurrentHashMap<>();
    private final Map<String, Laboratory> laboratories = new ConcurrentHashMap<>();
    private final Map<String, Branch> branches = new ConcurrentHashMap<>();

    @Override
    public Tenant saveTenant(Tenant tenant) {
        tenants.put(tenant.tenantId(), tenant);
        return tenant;
    }

    @Override
    public Laboratory saveLaboratory(Laboratory laboratory) {
        laboratories.put(laboratory.laboratoryId(), laboratory);
        return laboratory;
    }

    @Override
    public Branch saveBranch(Branch branch) {
        branches.put(branch.branchId(), branch);
        return branch;
    }

    @Override
    public Optional<Tenant> findTenantById(String tenantId) {
        return Optional.ofNullable(tenants.get(tenantId));
    }

    @Override
    public Optional<Laboratory> findLaboratoryById(String laboratoryId) {
        return Optional.ofNullable(laboratories.get(laboratoryId));
    }

    @Override
    public Optional<Branch> findBranchById(String branchId) {
        return Optional.ofNullable(branches.get(branchId));
    }
}
