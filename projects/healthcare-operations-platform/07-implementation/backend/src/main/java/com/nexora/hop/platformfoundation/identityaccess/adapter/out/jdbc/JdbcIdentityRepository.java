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
                insert into identity.user_accounts (
                    user_id, tenant_id, display_name, email, status,
                    username, password_hash, failed_login_attempts, locked_until, last_login_at,
                    created_at, updated_at
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                user.userId(),
                user.tenantId(),
                user.displayName(),
                user.email(),
                user.status(),
                user.username(),
                user.passwordHash(),
                user.failedLoginAttempts(),
                user.lockedUntil() != null ? Timestamp.from(user.lockedUntil()) : null,
                user.lastLoginAt() != null ? Timestamp.from(user.lastLoginAt()) : null,
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
                select user_id, tenant_id, display_name, email, status,
                       username, password_hash, failed_login_attempts, locked_until, last_login_at,
                       created_at, updated_at
                from identity.user_accounts
                where user_id = ?
                """, JdbcIdentityRepository::mapUser, userId).stream().findFirst();
    }

    @Override
    public Optional<UserAccount> findByTenantIdAndUsername(String tenantId, String username) {
        return jdbcTemplate.query("""
                select user_id, tenant_id, display_name, email, status,
                       username, password_hash, failed_login_attempts, locked_until, last_login_at,
                       created_at, updated_at
                from identity.user_accounts
                where tenant_id = ? and username = ?
                """, JdbcIdentityRepository::mapUser, tenantId, username).stream().findFirst();
    }

    @Override
    public java.util.List<RoleAssignment> findRoleAssignmentsByUserId(String userId) {
        return jdbcTemplate.query("""
                select role_assignment_id, user_id, role_code, scope_type, scope_id, created_at, created_by
                from identity.role_assignments
                where user_id = ?
                """, JdbcIdentityRepository::mapRoleAssignment, userId);
    }

    @Override
    public void updateUser(UserAccount user) {
        jdbcTemplate.update("""
                update identity.user_accounts
                set display_name = ?, email = ?, status = ?,
                    username = ?, password_hash = ?, failed_login_attempts = ?,
                    locked_until = ?, last_login_at = ?, updated_at = ?
                where user_id = ?
                """,
                user.displayName(),
                user.email(),
                user.status(),
                user.username(),
                user.passwordHash(),
                user.failedLoginAttempts(),
                user.lockedUntil() != null ? Timestamp.from(user.lockedUntil()) : null,
                user.lastLoginAt() != null ? Timestamp.from(user.lastLoginAt()) : null,
                Timestamp.from(user.updatedAt()),
                user.userId());
    }

    private static UserAccount mapUser(ResultSet resultSet, int rowNumber) throws SQLException {
        Timestamp lockedUntilTs = resultSet.getTimestamp("locked_until");
        Timestamp lastLoginAtTs = resultSet.getTimestamp("last_login_at");
        return new UserAccount(
                resultSet.getString("user_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("display_name"),
                resultSet.getString("email"),
                resultSet.getString("status"),
                instant(resultSet, "created_at"),
                instant(resultSet, "updated_at"),
                resultSet.getString("username"),
                resultSet.getString("password_hash"),
                resultSet.getInt("failed_login_attempts"),
                lockedUntilTs != null ? lockedUntilTs.toInstant() : null,
                lastLoginAtTs != null ? lastLoginAtTs.toInstant() : null);
    }

    private static RoleAssignment mapRoleAssignment(ResultSet resultSet, int rowNumber) throws SQLException {
        return new RoleAssignment(
                resultSet.getString("role_assignment_id"),
                resultSet.getString("user_id"),
                resultSet.getString("role_code"),
                resultSet.getString("scope_type"),
                resultSet.getString("scope_id"),
                instant(resultSet, "created_at"),
                resultSet.getString("created_by"));
    }

    private static Instant instant(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toInstant();
    }
}
