package com.nexora.hop.platformfoundation.organizationmanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.organizationmanagement.domain.Branch;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.Laboratory;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.OrganizationRepository;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.Tenant;

@Repository
@Profile("local")
class JdbcOrganizationRepository implements OrganizationRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcOrganizationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Tenant saveTenant(Tenant tenant) {
        jdbcTemplate.update("""
                insert into organization.tenants
                    (tenant_id, code, legal_name, trade_name, tax_id, status, tier, isolation_strategy, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                tenant.tenantId(),
                tenant.code(),
                tenant.legalName(),
                tenant.tradeName(),
                tenant.taxId(),
                tenant.status(),
                tenant.tier(),
                tenant.isolationStrategy(),
                Timestamp.from(tenant.createdAt()),
                Timestamp.from(tenant.updatedAt()));
        return tenant;
    }

    @Override
    public Laboratory saveLaboratory(Laboratory laboratory) {
        jdbcTemplate.update("""
                insert into organization.laboratories (laboratory_id, tenant_id, name, status, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?)
                """,
                laboratory.laboratoryId(),
                laboratory.tenantId(),
                laboratory.name(),
                laboratory.status(),
                Timestamp.from(laboratory.createdAt()),
                Timestamp.from(laboratory.updatedAt()));
        return laboratory;
    }

    @Override
    public Branch saveBranch(Branch branch) {
        jdbcTemplate.update("""
                insert into organization.branches (branch_id, tenant_id, laboratory_id, name, status, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                branch.branchId(),
                branch.tenantId(),
                branch.laboratoryId(),
                branch.name(),
                branch.status(),
                branch.version(),
                Timestamp.from(branch.createdAt()),
                Timestamp.from(branch.updatedAt()));
        return branch;
    }

    @Override
    public Optional<Tenant> findTenantById(String tenantId) {
        return jdbcTemplate.query("""
                select tenant_id, code, legal_name, trade_name, tax_id, status, tier, isolation_strategy, created_at, updated_at
                from organization.tenants
                where tenant_id = ?
                """, JdbcOrganizationRepository::mapTenant, tenantId).stream().findFirst();
    }

    @Override
    public Optional<Tenant> findTenantByCode(String code) {
        return jdbcTemplate.query("""
                select tenant_id, code, legal_name, trade_name, tax_id, status, tier, isolation_strategy, created_at, updated_at
                from organization.tenants
                where code = ?
                """, JdbcOrganizationRepository::mapTenant, code).stream().findFirst();
    }

    @Override
    public List<Tenant> findAllTenants() {
        return jdbcTemplate.query("""
                select tenant_id, code, legal_name, trade_name, tax_id, status, tier, isolation_strategy, created_at, updated_at
                from organization.tenants
                order by created_at
                """, JdbcOrganizationRepository::mapTenant);
    }

    @Override
    public Tenant updateTenantStatus(String tenantId, String status, Instant updatedAt) {
        jdbcTemplate.update("""
                update organization.tenants set status = ?, updated_at = ? where tenant_id = ?
                """, status, Timestamp.from(updatedAt), tenantId);
        return findTenantById(tenantId).orElseThrow();
    }

    @Override
    public Optional<Laboratory> findLaboratoryById(String laboratoryId) {
        return jdbcTemplate.query("""
                select laboratory_id, tenant_id, name, status, created_at, updated_at
                from organization.laboratories
                where laboratory_id = ?
                """, JdbcOrganizationRepository::mapLaboratory, laboratoryId).stream().findFirst();
    }

    @Override
    public Optional<Branch> findBranchById(String branchId) {
        return jdbcTemplate.query("""
                select branch_id, tenant_id, laboratory_id, name, status, version, created_at, updated_at
                from organization.branches
                where branch_id = ?
                """, JdbcOrganizationRepository::mapBranch, branchId).stream().findFirst();
    }

    private static Tenant mapTenant(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Tenant(
                resultSet.getString("tenant_id"),
                resultSet.getString("code"),
                resultSet.getString("legal_name"),
                resultSet.getString("trade_name"),
                resultSet.getString("tax_id"),
                resultSet.getString("status"),
                resultSet.getString("tier"),
                resultSet.getString("isolation_strategy"),
                instant(resultSet, "created_at"),
                instant(resultSet, "updated_at"));
    }

    private static Laboratory mapLaboratory(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Laboratory(
                resultSet.getString("laboratory_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("name"),
                resultSet.getString("status"),
                instant(resultSet, "created_at"),
                instant(resultSet, "updated_at"));
    }

    private static Branch mapBranch(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Branch(
                resultSet.getString("branch_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"),
                resultSet.getString("name"),
                resultSet.getString("status"),
                resultSet.getInt("version"),
                instant(resultSet, "created_at"),
                instant(resultSet, "updated_at"));
    }

    private static Instant instant(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toInstant();
    }
}
