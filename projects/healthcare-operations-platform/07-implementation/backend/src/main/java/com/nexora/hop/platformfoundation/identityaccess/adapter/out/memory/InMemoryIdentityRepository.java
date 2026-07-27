package com.nexora.hop.platformfoundation.identityaccess.adapter.out.memory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.identityaccess.domain.IdentityRepository;
import com.nexora.hop.platformfoundation.identityaccess.domain.RoleAssignment;
import com.nexora.hop.platformfoundation.identityaccess.domain.ServiceAccountCredential;
import com.nexora.hop.platformfoundation.identityaccess.domain.UserAccount;

@Repository
@Profile("!local")
class InMemoryIdentityRepository implements IdentityRepository {

    private final Map<String, UserAccount> users = new ConcurrentHashMap<>();
    private final Map<String, RoleAssignment> roleAssignments = new ConcurrentHashMap<>();
    private final Map<String, ServiceAccountCredential> serviceAccountsById = new ConcurrentHashMap<>();
    private final Map<String, String> serviceAccountIdByClientId = new ConcurrentHashMap<>();

    @Override
    public UserAccount saveUser(UserAccount user) {
        users.put(user.userId(), user);
        return user;
    }

    @Override
    public RoleAssignment saveRoleAssignment(RoleAssignment roleAssignment) {
        roleAssignments.put(roleAssignment.roleAssignmentId(), roleAssignment);
        return roleAssignment;
    }

    @Override
    public Optional<UserAccount> findUserById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Optional<UserAccount> findByTenantIdAndUsername(String tenantId, String username) {
        return users.values().stream()
                .filter(u -> tenantId.equals(u.tenantId()) && username.equals(u.username()))
                .findFirst();
    }

    @Override
    public java.util.List<RoleAssignment> findRoleAssignmentsByUserId(String userId) {
        return roleAssignments.values().stream()
                .filter(ra -> userId.equals(ra.userId()))
                .toList();
    }

    @Override
    public void updateUser(UserAccount user) {
        users.put(user.userId(), user);
    }

    @Override
    public ServiceAccountCredential saveServiceAccountCredential(ServiceAccountCredential credential) {
        serviceAccountsById.put(credential.serviceAccountId(), credential);
        serviceAccountIdByClientId.put(credential.clientId(), credential.serviceAccountId());
        return credential;
    }

    @Override
    public Optional<ServiceAccountCredential> findServiceAccountCredentialById(String serviceAccountId) {
        return Optional.ofNullable(serviceAccountsById.get(serviceAccountId));
    }

    @Override
    public Optional<ServiceAccountCredential> findServiceAccountCredentialByClientId(String clientId) {
        return Optional.ofNullable(serviceAccountIdByClientId.get(clientId))
                .map(serviceAccountsById::get);
    }
}
