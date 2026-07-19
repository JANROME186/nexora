package com.nexora.hop.platformfoundation.identityaccess.domain;

import java.util.Optional;

public interface IdentityRepository {

    UserAccount saveUser(UserAccount user);

    RoleAssignment saveRoleAssignment(RoleAssignment roleAssignment);

    Optional<UserAccount> findUserById(String userId);

    Optional<UserAccount> findByTenantIdAndUsername(String tenantId, String username);

    java.util.List<RoleAssignment> findRoleAssignmentsByUserId(String userId);

    void updateUser(UserAccount user);
}
