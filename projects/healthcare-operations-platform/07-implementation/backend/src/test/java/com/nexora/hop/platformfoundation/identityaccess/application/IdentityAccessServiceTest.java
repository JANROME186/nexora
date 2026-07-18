package com.nexora.hop.platformfoundation.identityaccess.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.identityaccess.domain.IdentityRepository;
import com.nexora.hop.platformfoundation.identityaccess.domain.RoleAssignment;
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

    private static UserAccount existingUser() {
        Instant now = Instant.now();
        return new UserAccount("user-1", "tenant-1", "Ada Lovelace", "ada@nexora.example", "created", now, now);
    }
}
