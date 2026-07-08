package com.nexora.hop.platformfoundation.identityaccess.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.identityaccess.domain.IdentityRepository;
import com.nexora.hop.platformfoundation.identityaccess.domain.RoleAssignment;
import com.nexora.hop.platformfoundation.identityaccess.domain.UserAccount;

@Repository
@Profile("local")
class JdbcIdentityRepository implements IdentityRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcIdentityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserAccount saveUser(UserAccount user) {
        jdbcTemplate.update("""
                insert into identity.user_accounts (user_id, tenant_id, display_name, email, status, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?)
                """,
                user.userId(),
                user.tenantId(),
                user.displayName(),
                user.email(),
                user.status(),
                Timestamp.from(user.createdAt()),
                Timestamp.from(user.updatedAt()));
        return user;
    }

    @Override
    public RoleAssignment saveRoleAssignment(RoleAssignment roleAssignment) {
        jdbcTemplate.update("""
                insert into identity.role_assignments
                    (role_assignment_id, user_id, role_code, scope_type, scope_id, created_at, created_by)
                values (?, ?, ?, ?, ?, ?, ?)
                """,
                roleAssignment.roleAssignmentId(),
                roleAssignment.userId(),
                roleAssignment.roleCode(),
                roleAssignment.scopeType(),
                roleAssignment.scopeId(),
                Timestamp.from(roleAssignment.createdAt()),
                roleAssignment.createdBy());
        return roleAssignment;
    }

    @Override
    public Optional<UserAccount> findUserById(String userId) {
        return jdbcTemplate.query("""
                select user_id, tenant_id, display_name, email, status, created_at, updated_at
                from identity.user_accounts
                where user_id = ?
                """, JdbcIdentityRepository::mapUser, userId).stream().findFirst();
    }

    private static UserAccount mapUser(ResultSet resultSet, int rowNumber) throws SQLException {
        return new UserAccount(
                resultSet.getString("user_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("display_name"),
                resultSet.getString("email"),
                resultSet.getString("status"),
                instant(resultSet, "created_at"),
                instant(resultSet, "updated_at"));
    }

    private static Instant instant(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toInstant();
    }
}
