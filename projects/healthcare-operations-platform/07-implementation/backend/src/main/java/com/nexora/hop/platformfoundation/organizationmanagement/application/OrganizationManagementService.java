package com.nexora.hop.platformfoundation.organizationmanagement.application;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.organizationmanagement.BranchDirectory;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.Branch;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.BranchSnapshot;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.Laboratory;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.OrganizationRepository;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.Tenant;

@Service
public class OrganizationManagementService implements TenantDirectory, BranchDirectory {

    private static final String ACTIVE_STATUS = "active";
    private static final int NAME_MAX_LENGTH = 180;

    private final OrganizationRepository repository;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public OrganizationManagementService(
            OrganizationRepository repository,
            AuditRecorder auditRecorder) {
        this(repository, auditRecorder, Clock.systemUTC());
    }

    private OrganizationManagementService(
            OrganizationRepository repository,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public Tenant createTenant(CreateTenantCommand command) {
        String name = requiredNameText(command.name(), "Tenant name is required.");
        Instant now = Instant.now(clock);
        Tenant tenant = repository.saveTenant(new Tenant(newId(), name, ACTIVE_STATUS, now, now));
        recordAudit(tenant.tenantId(), "TenantCreated", "Tenant", tenant.tenantId(),
                "{\"name\":\"%s\"}".formatted(jsonText(tenant.name())));
        return tenant;
    }

    public Laboratory createLaboratory(CreateLaboratoryCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String name = requiredNameText(command.name(), "Laboratory name is required.");
        requireTenant(tenantId);

        Instant now = Instant.now(clock);
        Laboratory laboratory = new Laboratory(newId(), tenantId, name, ACTIVE_STATUS, now, now);
        Laboratory saved = repository.saveLaboratory(laboratory);
        recordAudit(saved.tenantId(), "LaboratoryCreated", "Laboratory", saved.laboratoryId(),
                "{\"name\":\"%s\"}".formatted(jsonText(saved.name())));
        return saved;
    }

    public Branch createBranch(CreateBranchCommand command) {
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String name = requiredNameText(command.name(), "Branch name is required.");
        Laboratory laboratory = requireLaboratory(laboratoryId);

        Instant now = Instant.now(clock);
        Branch branch = new Branch(newId(), laboratory.tenantId(), laboratory.laboratoryId(), name, ACTIVE_STATUS, now, now);
        Branch saved = repository.saveBranch(branch);
        recordAudit(saved.tenantId(), "BranchCreated", "Branch", saved.branchId(),
                "{\"name\":\"%s\",\"laboratoryId\":\"%s\"}".formatted(jsonText(saved.name()), jsonText(saved.laboratoryId())));
        return saved;
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

    @Override
    public boolean tenantExists(String tenantId) {
        if (!StringUtils.hasText(tenantId)) {
            return false;
        }
        return repository.findTenantById(tenantId).isPresent();
    }

    @Override
    public Optional<BranchSnapshot> findSnapshot(String branchId) {
        if (!StringUtils.hasText(branchId)) {
            return Optional.empty();
        }
        return repository.findBranchById(branchId).map(BranchSnapshot::from);
    }

    @Override
    public boolean branchExists(String branchId) {
        if (!StringUtils.hasText(branchId)) {
            return false;
        }
        return repository.findBranchById(branchId).isPresent();
    }

    @Override
    public boolean isBranchOperational(String branchId) {
        if (!StringUtils.hasText(branchId)) {
            return false;
        }
        return repository.findBranchById(branchId)
                .map(branch -> ACTIVE_STATUS.equals(branch.status()))
                .orElse(false);
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

    /**
     * organization.tenants/laboratories/branches.name are all {@code varchar(180)}; validating the
     * bound here turns an oversized name into a clean 400 instead of an unhandled
     * DataIntegrityViolationException surfacing as a 500 (found via OWASP ZAP DAST,
     * HOP-QA-ALIGN-004).
     */
    private static String requiredNameText(String value, String message) {
        String trimmed = requiredText(value, message);
        if (trimmed.length() > NAME_MAX_LENGTH) {
            throw new InvalidOrganizationCommandException(
                    "Name must not exceed " + NAME_MAX_LENGTH + " characters.");
        }
        return trimmed;
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    private static String jsonText(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void recordAudit(String tenantId, String action, String subjectType, String subjectId, String metadataJson) {
        auditRecorder.recordSystemEvent(tenantId, action, subjectType, subjectId, metadataJson);
    }
}
