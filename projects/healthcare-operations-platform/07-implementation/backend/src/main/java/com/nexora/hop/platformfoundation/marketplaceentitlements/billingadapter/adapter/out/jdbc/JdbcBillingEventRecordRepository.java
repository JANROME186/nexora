package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingEventRecord;
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingEventRecordRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcBillingEventRecordRepository implements BillingEventRecordRepository {

    private static final String SELECT_SQL = """
            select billing_event_id, tenant_id, entitlement_id, event_type, amount_minor_units, currency,
                   provider_reference, adapter_status, retry_count, created_by, created_at, updated_by, updated_at
            from marketplace_entitlements.billing_event_records
            """;

    private final JdbcTemplate jdbcTemplate;

    JdbcBillingEventRecordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BillingEventRecord save(BillingEventRecord record) {
        jdbcTemplate.update("""
                insert into marketplace_entitlements.billing_event_records
                    (billing_event_id, tenant_id, entitlement_id, event_type, amount_minor_units, currency,
                     provider_reference, adapter_status, retry_count, created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (billing_event_id) do update set
                    adapter_status = excluded.adapter_status, retry_count = excluded.retry_count,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                record.billingEventId(), record.tenantId(), record.entitlementId(), record.eventType(),
                record.amountMinorUnits(), record.currency(), record.providerReference(), record.adapterStatus(),
                record.retryCount(), record.audit().createdBy(), Timestamp.valueOf(record.audit().createdAt()),
                record.audit().updatedBy(), Timestamp.valueOf(record.audit().updatedAt()));
        return record;
    }

    @Override
    public List<BillingEventRecord> findByTenantId(String tenantId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ?",
                JdbcBillingEventRecordRepository::map, tenantId);
    }

    @Override
    public Optional<BillingEventRecord> findByTenantIdAndProviderReference(String tenantId, String providerReference) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and provider_reference = ?",
                JdbcBillingEventRecordRepository::map, tenantId, providerReference).stream().findFirst();
    }

    private static BillingEventRecord map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new BillingEventRecord(
                resultSet.getString("billing_event_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("entitlement_id"),
                resultSet.getString("event_type"),
                resultSet.getLong("amount_minor_units"),
                resultSet.getString("currency"),
                resultSet.getString("provider_reference"),
                resultSet.getString("adapter_status"),
                resultSet.getInt("retry_count"),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
