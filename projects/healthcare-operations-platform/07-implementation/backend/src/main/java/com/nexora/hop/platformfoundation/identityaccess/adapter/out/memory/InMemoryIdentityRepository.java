package com.nexora.hop.platformfoundation.identityaccess.adapter.out.memory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.identityaccess.domain.IdentityRepository;
import com.nexora.hop.platformfoundation.identityaccess.domain.RoleAssignment;
import com.nexora.hop.platformfoundation.identityaccess.domain.UserAccount;

@Repository
@Profile("!local")
class InMemoryIdentityRepository implements IdentityRepository {

    private final Map<String, UserAccount> users = new ConcurrentHashMap<>();
    private final Map<String, RoleAssignment> roleAssignments = new ConcurrentHashMap<>();

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
}
