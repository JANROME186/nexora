package com.nexora.hop.platformfoundation.organizationmanagement.application;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nexora.hop.platformfoundation.organizationmanagement.domain.Branch;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.Laboratory;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.OrganizationRepository;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.Tenant;

@Service
public class OrganizationManagementService {

    private static final String ACTIVE_STATUS = "active";

    private final OrganizationRepository repository;
    private final Clock clock;

    @Autowired
    public OrganizationManagementService(OrganizationRepository repository) {
        this(repository, Clock.systemUTC());
    }

    private OrganizationManagementService(OrganizationRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public Tenant createTenant(CreateTenantCommand command) {
        String name = requiredText(command.name(), "Tenant name is required.");
        Instant now = Instant.now(clock);
        return repository.saveTenant(new Tenant(newId(), name, ACTIVE_STATUS, now, now));
    }

    public Laboratory createLaboratory(CreateLaboratoryCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String name = requiredText(command.name(), "Laboratory name is required.");
        requireTenant(tenantId);

        Instant now = Instant.now(clock);
        Laboratory laboratory = new Laboratory(newId(), tenantId, name, ACTIVE_STATUS, now, now);
        return repository.saveLaboratory(laboratory);
    }

    public Branch createBranch(CreateBranchCommand command) {
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String name = requiredText(command.name(), "Branch name is required.");
        Laboratory laboratory = requireLaboratory(laboratoryId);

        Instant now = Instant.now(clock);
        Branch branch = new Branch(newId(), laboratory.tenantId(), laboratory.laboratoryId(), name, ACTIVE_STATUS, now, now);
        return repository.saveBranch(branch);
    }

    public Tenant getTenant(String tenantId) {
        return repository.findTenantById(requiredText(tenantId, "Tenant id is required."))
                .orElseThrow(() -> new OrganizationEntityNotFoundException("Tenant was not found."));
    }

    public Laboratory getLaboratory(String laboratoryId) {
        return requireLaboratory(requiredText(laboratoryId, "Laboratory id is required."));
    }

    public Branch getBranch(String branchId) {
        return repository.findBranchById(requiredText(branchId, "Branch id is required."))
                .orElseThrow(() -> new OrganizationEntityNotFoundException("Branch was not found."));
    }

    private Tenant requireTenant(String tenantId) {
        return repository.findTenantById(tenantId)
                .orElseThrow(() -> new OrganizationEntityNotFoundException("Tenant was not found."));
    }

    private Laboratory requireLaboratory(String laboratoryId) {
        return repository.findLaboratoryById(laboratoryId)
                .orElseThrow(() -> new OrganizationEntityNotFoundException("Laboratory was not found."));
    }

    private static String requiredText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new InvalidOrganizationCommandException(message);
        }
        return value.trim();
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
