package com.nexora.hop.platformfoundation.identityaccess.application;

import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.identityaccess.domain.IdentityRepository;
import com.nexora.hop.platformfoundation.identityaccess.domain.RoleAssignment;
import com.nexora.hop.platformfoundation.identityaccess.domain.UserAccount;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.sharedkernel.HopMessages;

@Service
public class IdentityAccessService {

    private static final String CREATED_STATUS = "created";

    /**
     * There is no request-scoped authenticated-principal/session context in this backend yet, so
     * the message locale cannot be resolved from a real {@code Accept-Language} header or a
     * tenant/user preference. es-MX (the platform default locale) is used explicitly until that
     * context exists; see {@link HopMessages}.
     */
    private static final Locale DEFAULT_MESSAGE_LOCALE = Locale.forLanguageTag("es-MX");

    private final IdentityRepository repository;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final HopMessages messages;
    private final Clock clock;

    @Autowired
    public IdentityAccessService(
            IdentityRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            HopMessages messages) {
        this(repository, tenantDirectory, auditRecorder, messages, Clock.systemUTC());
    }

    private IdentityAccessService(
            IdentityRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            HopMessages messages,
            Clock clock) {
        this.repository = repository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.messages = messages;
        this.clock = clock;
    }

    public UserAccount createUser(CreateUserCommand command) {
        String tenantId = requiredText(command.tenantId(), "identityaccess.field.tenantId.required");
        String displayName = requiredText(command.displayName(), "identityaccess.field.displayName.required");
        String email = requiredText(command.email(), "identityaccess.field.email.required");

        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new IdentityEntityNotFoundException(messages.get("identityaccess.tenant.notfound", DEFAULT_MESSAGE_LOCALE));
        }

        Instant now = Instant.now(clock);
        UserAccount user = new UserAccount(newId(), tenantId, displayName, email, CREATED_STATUS, now, now);
        UserAccount saved = repository.saveUser(user);
        recordAudit(saved.tenantId(), "UserCreated", "UserAccount", saved.userId(),
                "{\"email\":\"%s\"}".formatted(jsonText(saved.email())));
        return saved;
    }

    public UserAccount getUser(String userId) {
        return requireUser(requiredText(userId, "identityaccess.field.userId.required"));
    }

    public RoleAssignment assignRole(String userId, AssignRoleCommand command) {
        UserAccount user = requireUser(requiredText(userId, "identityaccess.field.userId.required"));
        String roleCode = requiredText(command.roleCode(), "identityaccess.field.roleCode.required");
        String scopeType = requiredText(command.scopeType(), "identityaccess.field.scopeType.required");
        String scopeId = requiredText(command.scopeId(), "identityaccess.field.scopeId.required");
        String actorUserId = requiredText(command.actorUserId(), "identityaccess.field.actorUserId.required");

        Instant now = Instant.now(clock);
        RoleAssignment assignment = new RoleAssignment(
                newId(), user.userId(), roleCode, scopeType, scopeId, now, actorUserId);
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
                .orElseThrow(() -> new IdentityEntityNotFoundException(
                        messages.get("identityaccess.user.notfound", DEFAULT_MESSAGE_LOCALE)));
    }

    /**
     * @param messageKey a key in the {@code i18n/messages} catalog (see {@link HopMessages}),
     *                    resolved for {@link #DEFAULT_MESSAGE_LOCALE}
     */
    private String requiredText(String value, String messageKey) {
        if (!StringUtils.hasText(value)) {
            throw new InvalidIdentityCommandException(messages.get(messageKey, DEFAULT_MESSAGE_LOCALE));
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
