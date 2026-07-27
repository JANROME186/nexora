package com.nexora.hop.platformfoundation.identityaccess.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.identityaccess.domain.IdentityRepository;
import com.nexora.hop.platformfoundation.identityaccess.domain.RoleAssignment;
import com.nexora.hop.platformfoundation.identityaccess.domain.ServiceAccountCredential;
import com.nexora.hop.platformfoundation.identityaccess.domain.UserAccount;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.sharedkernel.HopMessages;
import com.nexora.hop.platformfoundation.sharedkernel.LocalizationConfig;

/**
 * Unit tests for {@link IdentityAccessService}, exercised against mocked ports (no Spring
 * context) so message-catalog resolution ({@link HopMessages}) is verified end to end.
 */
class IdentityAccessServiceTest {

    private IdentityRepository repository;
    private TenantDirectory tenantDirectory;
    private AuditRecorder auditRecorder;
    private IdentityAccessService service;

    @BeforeEach
    void setUp() {
        repository = mock(IdentityRepository.class);
        tenantDirectory = mock(TenantDirectory.class);
        auditRecorder = mock(AuditRecorder.class);
        HopMessages messages = new HopMessages(new LocalizationConfig().messageSource());
        service = new IdentityAccessService(repository, tenantDirectory, auditRecorder, messages);

        when(repository.saveRoleAssignment(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void assignRolePersistsTheSuppliedActorAsCreatedByInsteadOfAHardcodedSystemActor() {
        UserAccount user = existingUser();
        when(repository.findUserById("user-1")).thenReturn(Optional.of(user));

        RoleAssignment assignment = service.assignRole(
                "user-1", new AssignRoleCommand("tenant-admin", "tenant", "tenant-1", "tester-1"));

        assertThat(assignment.createdBy()).isEqualTo("tester-1");
        assertThat(assignment.createdBy()).isNotEqualTo("system");
    }

    @Test
    void assignRoleRejectsBlankActorUserId() {
        UserAccount user = existingUser();
        when(repository.findUserById("user-1")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.assignRole(
                "user-1", new AssignRoleCommand("tenant-admin", "tenant", "tenant-1", "   ")))
                .isInstanceOf(InvalidIdentityCommandException.class)
                .hasMessage("El identificador del usuario que realiza la acción es obligatorio.");
    }

    @Test
    void assignRoleRejectsMissingActorUserId() {
        UserAccount user = existingUser();
        when(repository.findUserById("user-1")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.assignRole(
                "user-1", new AssignRoleCommand("tenant-admin", "tenant", "tenant-1", null)))
                .isInstanceOf(InvalidIdentityCommandException.class);
    }

    @Test
    void getUserThrowsNotFoundWithResolvedMessageWhenUserIsMissing() {
        when(repository.findUserById("missing-user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUser("missing-user"))
                .isInstanceOf(IdentityEntityNotFoundException.class)
                .hasMessage("No se encontró el usuario.");
    }

    @Test
    void createUserRejectsMissingTenantIdWithResolvedMessage() {
        assertThatThrownBy(() -> service.createUser(new CreateUserCommand("", "Ada Lovelace", "ada@nexora.example")))
                .isInstanceOf(InvalidIdentityCommandException.class)
                .hasMessage("El identificador del inquilino es obligatorio.");
    }

    @Test
    void createUserThrowsNotFoundWhenTenantDoesNotExist() {
        when(tenantDirectory.tenantExists("missing-tenant")).thenReturn(false);

        assertThatThrownBy(() -> service.createUser(
                new CreateUserCommand("missing-tenant", "Ada Lovelace", "ada@nexora.example")))
                .isInstanceOf(IdentityEntityNotFoundException.class)
                .hasMessage("No se encontró el inquilino.");
    }

    @Test
    void enrollMfaGeneratesASecretAndPersistsAMfaEnabledUser() {
        UserAccount user = existingUser();
        when(repository.findUserById("user-1")).thenReturn(Optional.of(user));

        String secret = service.enrollMfa("user-1");

        assertThat(secret).isNotBlank();
        org.mockito.ArgumentCaptor<UserAccount> captor = org.mockito.ArgumentCaptor.forClass(UserAccount.class);
        org.mockito.Mockito.verify(repository).updateUser(captor.capture());
        assertThat(captor.getValue().mfaEnabled()).isTrue();
        assertThat(captor.getValue().mfaSecret()).isEqualTo(secret);
    }

    @Test
    void loginRequiresAnMfaCodeWhenMfaIsEnabledForTheUser() {
        PasswordHashingService passwordHashingService = new PasswordHashingService();
        String secret = new TotpService().generateSecret();
        UserAccount user = existingUser().withPasswordHash(passwordHashingService.hash("secret123")).withMfaEnrollment(secret);

        when(tenantDirectory.tenantExists("tenant-1")).thenReturn(true);
        when(repository.findByTenantIdAndUsername("tenant-1", "ada@nexora.example")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.login("tenant-1", "ada@nexora.example", "secret123", Locale.forLanguageTag("es-MX"), null))
                .isInstanceOf(MfaRequiredException.class);
    }

    @Test
    void loginRejectsAnInvalidMfaCode() {
        PasswordHashingService passwordHashingService = new PasswordHashingService();
        String secret = new TotpService().generateSecret();
        UserAccount user = existingUser().withPasswordHash(passwordHashingService.hash("secret123")).withMfaEnrollment(secret);

        when(tenantDirectory.tenantExists("tenant-1")).thenReturn(true);
        when(repository.findByTenantIdAndUsername("tenant-1", "ada@nexora.example")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.login("tenant-1", "ada@nexora.example", "secret123", Locale.forLanguageTag("es-MX"), "000000"))
                .isInstanceOf(MfaVerificationFailedException.class);
    }

    @Test
    void loginSucceedsWithAValidMfaCode() {
        PasswordHashingService passwordHashingService = new PasswordHashingService();
        String secret = new TotpService().generateSecret();
        UserAccount user = existingUser().withPasswordHash(passwordHashingService.hash("secret123")).withMfaEnrollment(secret);

        when(tenantDirectory.tenantExists("tenant-1")).thenReturn(true);
        when(repository.findByTenantIdAndUsername("tenant-1", "ada@nexora.example")).thenReturn(Optional.of(user));

        String token = service.login(
                "tenant-1", "ada@nexora.example", "secret123", Locale.forLanguageTag("es-MX"), rfc6238Code(secret));

        assertThat(token).isEqualTo("local-session:tenant-1:user-1");
    }

    @Test
    void createServiceAccountPersistsAHashedClientSecretAndIsAuthenticatable() {
        when(tenantDirectory.tenantExists("tenant-1")).thenReturn(true);
        org.mockito.ArgumentCaptor<ServiceAccountCredential> captor =
                org.mockito.ArgumentCaptor.forClass(ServiceAccountCredential.class);
        when(repository.saveServiceAccountCredential(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ServiceAccountCredential created = service.createServiceAccount(
                "tenant-1", "integration-client", "super-secret-value", "MARKETPLACE_OPERATOR");

        assertThat(created.clientSecretHash()).isNotEqualTo("super-secret-value");
        assertThat(captor.getValue().roleCode()).isEqualTo("MARKETPLACE_OPERATOR");

        when(repository.findServiceAccountCredentialByClientId("integration-client"))
                .thenReturn(Optional.of(created));

        String token = service.authenticateServiceAccount("integration-client", "super-secret-value");

        assertThat(token).isEqualTo("service-session:tenant-1:" + created.serviceAccountId());
    }

    @Test
    void authenticateServiceAccountRejectsAnIncorrectClientSecret() {
        when(tenantDirectory.tenantExists("tenant-1")).thenReturn(true);
        when(repository.saveServiceAccountCredential(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ServiceAccountCredential created = service.createServiceAccount(
                "tenant-1", "integration-client", "super-secret-value", "MARKETPLACE_OPERATOR");
        when(repository.findServiceAccountCredentialByClientId("integration-client"))
                .thenReturn(Optional.of(created));

        assertThatThrownBy(() -> service.authenticateServiceAccount("integration-client", "wrong-secret"))
                .isInstanceOf(AuthenticationFailedException.class);
    }

    @Test
    void authenticateServiceAccountRejectsASuspendedServiceAccount() {
        when(tenantDirectory.tenantExists("tenant-1")).thenReturn(true);
        when(repository.saveServiceAccountCredential(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ServiceAccountCredential created = service.createServiceAccount(
                "tenant-1", "integration-client", "super-secret-value", "MARKETPLACE_OPERATOR");
        ServiceAccountCredential suspended = created.withStatus("suspended");
        when(repository.findServiceAccountCredentialByClientId("integration-client"))
                .thenReturn(Optional.of(suspended));

        assertThatThrownBy(() -> service.authenticateServiceAccount("integration-client", "super-secret-value"))
                .isInstanceOf(AccountSuspendedException.class);
    }

    private static String rfc6238Code(String base32Secret) {
        try {
            long timeStep = Instant.now().getEpochSecond() / 30;
            byte[] key = base32Decode(base32Secret);
            byte[] stepBytes = new byte[8];
            long value = timeStep;
            for (int i = 7; i >= 0; i--) {
                stepBytes[i] = (byte) (value & 0xff);
                value >>= 8;
            }
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(stepBytes);
            int offset = hash[hash.length - 1] & 0x0f;
            int binary =
                    ((hash[offset] & 0x7f) << 24)
                            | ((hash[offset + 1] & 0xff) << 16)
                            | ((hash[offset + 2] & 0xff) << 8)
                            | (hash[offset + 3] & 0xff);
            int otp = binary % 1_000_000;
            return String.format(Locale.ROOT, "%06d", otp);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static byte[] base32Decode(String base32) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        String cleaned = base32.trim().toUpperCase(Locale.ROOT).replace("=", "");
        int buffer = 0;
        int bitsLeft = 0;
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        for (char c : cleaned.toCharArray()) {
            int value = alphabet.indexOf(c);
            if (value < 0) {
                continue;
            }
            buffer = (buffer << 5) | value;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                out.write((buffer >> (bitsLeft - 8)) & 0xff);
                bitsLeft -= 8;
            }
        }
        return out.toByteArray();
    }

    private static UserAccount existingUser() {
        Instant now = Instant.now();
        return new UserAccount("user-1", "tenant-1", "Ada Lovelace", "ada@nexora.example", "created", now, now);
    }
}
