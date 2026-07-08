package com.nexora.hop.platformfoundation.identityaccess.application;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.identityaccess.domain.IdentityRepository;
import com.nexora.hop.platformfoundation.identityaccess.domain.RoleAssignment;
import com.nexora.hop.platformfoundation.identityaccess.domain.UserAccount;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;

@Service
public class IdentityAccessService {

    private static final String CREATED_STATUS = "created";

    private final IdentityRepository repository;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public IdentityAccessService(
            IdentityRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder) {
        this(repository, tenantDirectory, auditRecorder, Clock.systemUTC());
    }

    private IdentityAccessService(
            IdentityRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public UserAccount createUser(CreateUserCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String displayName = requiredText(command.displayName(), "Display name is required.");
        String email = requiredText(command.email(), "Email is required.");

        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new IdentityEntityNotFoundException("Tenant was not found.");
        }

        Instant now = Instant.now(clock);
        UserAccount user = new UserAccount(newId(), tenantId, displayName, email, CREATED_STATUS, now, now);
        UserAccount saved = repository.saveUser(user);
        recordAudit(saved.tenantId(), "UserCreated", "UserAccount", saved.userId(),
                "{\"email\":\"%s\"}".formatted(jsonText(saved.email())));
        return saved;
    }

    public UserAccount getUser(String userId) {
        return requireUser(requiredText(userId, "User id is required."));
    }

    public RoleAssignment assignRole(String userId, AssignRoleCommand command) {
        UserAccount user = requireUser(requiredText(userId, "User id is required."));
        String roleCode = requiredText(command.roleCode(), "Role code is required.");
        String scopeType = requiredText(command.scopeType(), "Scope type is required.");
        String scopeId = requiredText(command.scopeId(), "Scope id is required.");

        Instant now = Instant.now(clock);
        RoleAssignment assignment = new RoleAssignment(
                newId(), user.userId(), roleCode, scopeType, scopeId, now, "system");
        RoleAssignment saved = repository.saveRoleAssignment(assignment);
        recordAudit(user.tenantId(), "RoleAssigned", "RoleAssignment", saved.roleAssignmentId(),
                "{\"userId\":\"%s\",\"roleCode\":\"%s\",\"scopeType\":\"%s\",\"scopeId\":\"%s\"}"
                        .formatted(
                                jsonText(saved.userId()),
                                jsonText(saved.roleCode()),
                                jsonText(saved.scopeType()),
                                jsonText(saved.scopeId())));
        return saved;
    }

    private UserAccount requireUser(String userId) {
        return repository.findUserById(userId)
                .orElseThrow(() -> new IdentityEntityNotFoundException("User was not found."));
    }

    private static String requiredText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new InvalidIdentityCommandException(message);
        }
        return value.trim();
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
