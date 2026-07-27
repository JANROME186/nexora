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
import com.nexora.hop.platformfoundation.identityaccess.domain.ServiceAccountCredential;
import com.nexora.hop.platformfoundation.identityaccess.domain.UserAccount;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.sharedkernel.HopMessages;

@Service
public class IdentityAccessService {

    private static final String CREATED_STATUS = "created";
    private static final String ACTIVE_STATUS = "active";

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
    private final PasswordHashingService passwordHashingService;
    private final TotpService totpService;
    private final Clock clock;

    @Autowired
    public IdentityAccessService(
            IdentityRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            HopMessages messages,
            PasswordHashingService passwordHashingService,
            TotpService totpService) {
        this(repository, tenantDirectory, auditRecorder, messages, passwordHashingService, totpService, Clock.systemUTC());
    }

    public IdentityAccessService(
            IdentityRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            HopMessages messages) {
        this(repository, tenantDirectory, auditRecorder, messages, new PasswordHashingService(), new TotpService(), Clock.systemUTC());
    }

    private IdentityAccessService(
            IdentityRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            HopMessages messages,
            PasswordHashingService passwordHashingService,
            TotpService totpService,
            Clock clock) {
        this.repository = repository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.messages = messages;
        this.passwordHashingService = passwordHashingService;
        this.totpService = totpService;
        this.clock = clock;
    }

    public UserAccount createUser(CreateUserCommand command) {
        return createUser(command, command.email(), "");
    }

    public UserAccount createUser(CreateUserCommand command, String username, String password) {
        String tenantId = requiredText(command.tenantId(), "identityaccess.field.tenantId.required");
        String displayName = requiredText(command.displayName(), "identityaccess.field.displayName.required");
        String email = requiredText(command.email(), "identityaccess.field.email.required");

        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new IdentityEntityNotFoundException(messages.get("identityaccess.tenant.notfound", DEFAULT_MESSAGE_LOCALE));
        }

        Instant now = Instant.now(clock);
        String finalUsername = StringUtils.hasText(username) ? username.trim() : email;
        String passwordHash = StringUtils.hasText(password) ? passwordHashingService.hash(password) : "";

        UserAccount user = new UserAccount(
                newId(), tenantId, displayName, email, CREATED_STATUS, now, now,
                finalUsername, passwordHash, 0, null, null);
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

    public String login(String tenantId, String username, String password, Locale locale) {
        return login(tenantId, username, password, locale, null);
    }

    public String login(String tenantId, String username, String password, Locale locale, String mfaCode) {
        Locale activeLocale = locale != null ? locale : DEFAULT_MESSAGE_LOCALE;

        String cleanTenantId = requiredText(tenantId, "identityaccess.field.tenantId.required");
        String cleanUsername = requiredText(username, "identityaccess.field.username.required");
        String cleanPassword = requiredText(password, "identityaccess.field.password.required");

        if (!tenantDirectory.tenantExists(cleanTenantId)) {
            throw new AuthenticationFailedException(messages.get("identityaccess.tenant.notfound", activeLocale));
        }

        UserAccount user = repository.findByTenantIdAndUsername(cleanTenantId, cleanUsername)
                .orElseThrow(() -> new AuthenticationFailedException(messages.get("identityaccess.login.invalid", activeLocale)));

        Instant now = Instant.now(clock);

        if ("suspended".equalsIgnoreCase(user.status())) {
            throw new AccountSuspendedException(messages.get("identityaccess.login.suspended", activeLocale));
        }

        if ("locked".equalsIgnoreCase(user.status())) {
            if (user.lockedUntil() != null && user.lockedUntil().isAfter(now)) {
                throw new AccountLockedException(messages.get("identityaccess.login.locked", activeLocale));
            } else {
                user = user.withStatus("active").withFailedLoginAttempts(0).withLockedUntil(null);
            }
        }

        if (passwordHashingService.matches(cleanPassword, user.passwordHash())) {
            if (user.hasMfaEnabled()) {
                String cleanMfaCode = mfaCode == null ? null : mfaCode.trim();
                if (cleanMfaCode == null || cleanMfaCode.isEmpty()) {
                    throw new MfaRequiredException(messages.get("identityaccess.mfa.required", activeLocale));
                }
                if (!totpService.verifyCode(user.mfaSecret(), cleanMfaCode)) {
                    recordAudit(user.tenantId(), "UserMfaVerificationFailed", "UserAccount", user.userId(), "{}");
                    throw new MfaVerificationFailedException(messages.get("identityaccess.mfa.invalid", activeLocale));
                }
            }
            user = user.withFailedLoginAttempts(0).withLockedUntil(null).withLastLoginAt(now);
            repository.updateUser(user);
            recordAudit(user.tenantId(), "UserLoggedIn", "UserAccount", user.userId(), "{}");
            return "local-session:" + user.tenantId() + ":" + user.userId();
        } else {
            int attempts = user.failedLoginAttempts() + 1;
            if (attempts >= 5) {
                user = user.withStatus("locked")
                        .withFailedLoginAttempts(attempts)
                        .withLockedUntil(now.plus(java.time.Duration.ofMinutes(15)));
            } else {
                user = user.withFailedLoginAttempts(attempts);
            }
            repository.updateUser(user);
            recordAudit(user.tenantId(), "UserAuthenticationFailed", "UserAccount", user.userId(), "{}");
            throw new AuthenticationFailedException(messages.get("identityaccess.login.invalid", activeLocale));
        }
    }

    public void logout(String userId, String tenantId) {
        recordAudit(tenantId, "UserLoggedOut", "UserAccount", userId, "{}");
    }

    public String initiateAssistance(String assistedUserId, String ticketReference, String actorUserId) {
        String cleanAssistedUserId = requiredText(assistedUserId, "identityaccess.field.userId.required");
        String cleanTicketReference = requiredText(ticketReference, "identityaccess.field.ticketReference.required");
        String cleanActorUserId = requiredText(actorUserId, "identityaccess.field.actorUserId.required");

        UserAccount assistedUser = repository.findUserById(cleanAssistedUserId)
                .orElseThrow(() -> new IdentityEntityNotFoundException(
                        messages.get("identityaccess.user.notfound", DEFAULT_MESSAGE_LOCALE)));

        recordAudit(assistedUser.tenantId(), "SupportSessionAssisted", "UserAccount", assistedUser.userId(),
                "{\"actorUserId\":\"%s\",\"ticketReference\":\"%s\"}"
                        .formatted(jsonText(cleanActorUserId), jsonText(cleanTicketReference)));

        return "assistance-session:" + assistedUser.tenantId() + ":" + assistedUser.userId() + ":" + cleanActorUserId;
    }

    /**
     * Enrolls the second MFA factor (TD-IAM-003) for {@code userId}, generating a new Base32 TOTP
     * secret and enabling MFA enforcement on subsequent {@link #login} calls. Returns the raw
     * secret once so the caller can provision an authenticator app; it is not returned again.
     */
    public String enrollMfa(String userId) {
        UserAccount user = requireUser(requiredText(userId, "identityaccess.field.userId.required"));
        String secret = totpService.generateSecret();
        repository.updateUser(user.withMfaEnrollment(secret));
        recordAudit(user.tenantId(), "UserMfaEnrolled", "UserAccount", user.userId(), "{}");
        return secret;
    }

    /**
     * Provisions a non-interactive service-account principal (TD-IAM-003) authenticated by a
     * client id/secret pair instead of a human username/password session.
     */
    public ServiceAccountCredential createServiceAccount(
            String tenantId, String clientId, String clientSecret, String roleCode) {
        String cleanTenantId = requiredText(tenantId, "identityaccess.field.tenantId.required");
        String cleanClientId = requiredText(clientId, "identityaccess.field.clientId.required");
        String cleanClientSecret = requiredText(clientSecret, "identityaccess.field.clientSecret.required");
        String cleanRoleCode = requiredText(roleCode, "identityaccess.field.roleCode.required");

        if (!tenantDirectory.tenantExists(cleanTenantId)) {
            throw new IdentityEntityNotFoundException(messages.get("identityaccess.tenant.notfound", DEFAULT_MESSAGE_LOCALE));
        }

        ServiceAccountCredential credential = new ServiceAccountCredential(
                newId(),
                cleanTenantId,
                cleanClientId,
                passwordHashingService.hash(cleanClientSecret),
                cleanRoleCode,
                ACTIVE_STATUS,
                Instant.now(clock));
        ServiceAccountCredential saved = repository.saveServiceAccountCredential(credential);
        recordAudit(cleanTenantId, "ServiceAccountCreated", "ServiceAccountCredential", saved.serviceAccountId(), "{}");
        return saved;
    }

    /**
     * Authenticates a service account by client id/secret (TD-IAM-003), returning a
     * {@code service-session:} token {@link com.nexora.hop.platformfoundation.identityaccess.security.HopAuthenticationResolver}
     * resolves without any human session ever existing.
     */
    public String authenticateServiceAccount(String clientId, String clientSecret) {
        String cleanClientId = requiredText(clientId, "identityaccess.field.clientId.required");
        String cleanClientSecret = requiredText(clientSecret, "identityaccess.field.clientSecret.required");

        ServiceAccountCredential credential = repository.findServiceAccountCredentialByClientId(cleanClientId)
                .orElseThrow(() -> new AuthenticationFailedException(
                        messages.get("identityaccess.login.invalid", DEFAULT_MESSAGE_LOCALE)));

        if (!credential.isActive()) {
            throw new AccountSuspendedException(messages.get("identityaccess.login.suspended", DEFAULT_MESSAGE_LOCALE));
        }

        if (!passwordHashingService.matches(cleanClientSecret, credential.clientSecretHash())) {
            recordAudit(credential.tenantId(), "ServiceAccountAuthenticationFailed", "ServiceAccountCredential",
                    credential.serviceAccountId(), "{}");
            throw new AuthenticationFailedException(messages.get("identityaccess.login.invalid", DEFAULT_MESSAGE_LOCALE));
        }

        recordAudit(credential.tenantId(), "ServiceAccountAuthenticated", "ServiceAccountCredential",
                credential.serviceAccountId(), "{}");
        return "service-session:" + credential.tenantId() + ":" + credential.serviceAccountId();
    }

    private UserAccount requireUser(String userId) {
        return repository.findUserById(userId)
                .orElseThrow(() -> new IdentityEntityNotFoundException(
                        messages.get("identityaccess.user.notfound", DEFAULT_MESSAGE_LOCALE)));
    }

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
