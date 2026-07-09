package com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.adapter.out.jdbc;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceEntry;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceList;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceListRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

@Repository
@Profile("local")
class JdbcPriceListRepository implements PriceListRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcPriceListRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PriceList save(PriceList priceList) {
        jdbcTemplate.update("""
                insert into catalog.price_lists
                    (price_list_id, tenant_id, laboratory_id, code, name_en, name_es, currency, agreement_ref_id,
                     effective_from, effective_to, status, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (price_list_id) do update set
                    code = excluded.code, name_en = excluded.name_en, name_es = excluded.name_es,
                    agreement_ref_id = excluded.agreement_ref_id, effective_from = excluded.effective_from,
                    effective_to = excluded.effective_to, status = excluded.status, version = excluded.version,
                    updated_at = excluded.updated_at
                """,
                priceList.priceListId(), priceList.tenantId(), priceList.laboratoryId(), priceList.code(),
                priceList.name().en(), priceList.name().es(), priceList.currency(), priceList.agreementRefId(),
                priceList.effectiveFrom(), priceList.effectiveTo(), priceList.status(), priceList.version(),
                Timestamp.from(priceList.createdAt()), Timestamp.from(priceList.updatedAt()));
        return priceList;
    }

    @Override
    public Optional<PriceList> findById(String priceListId) {
        return jdbcTemplate.query("""
                select price_list_id, tenant_id, laboratory_id, code, name_en, name_es, currency, agreement_ref_id,
                       effective_from, effective_to, status, version, created_at, updated_at
                from catalog.price_lists
                where price_list_id = ?
                """, JdbcPriceListRepository::mapPriceList, priceListId).stream().findFirst();
    }

    @Override
    public List<PriceList> findByLaboratoryId(String laboratoryId) {
        return jdbcTemplate.query("""
                select price_list_id, tenant_id, laboratory_id, code, name_en, name_es, currency, agreement_ref_id,
                       effective_from, effective_to, status, version, created_at, updated_at
                from catalog.price_lists
                where laboratory_id = ?
                """, JdbcPriceListRepository::mapPriceList, laboratoryId);
    }

    @Override
    public List<PriceList> findByStatus(String status) {
        return jdbcTemplate.query("""
                select price_list_id, tenant_id, laboratory_id, code, name_en, name_es, currency, agreement_ref_id,
                       effective_from, effective_to, status, version, created_at, updated_at
                from catalog.price_lists
                where status = ?
                """, JdbcPriceListRepository::mapPriceList, status);
    }

    @Override
    public boolean existsByCode(String laboratoryId, String code, String excludePriceListId) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(*) from catalog.price_lists
                where laboratory_id = ? and code = ? and price_list_id <> ?
                """, Integer.class, laboratoryId, code, excludePriceListId == null ? "" : excludePriceListId);
        return count != null && count > 0;
    }

    @Override
    public PriceEntry saveEntry(PriceEntry entry) {
        jdbcTemplate.update("""
                insert into catalog.price_entries (entry_id, price_list_id, item_type, item_ref_id, currency, amount)
                values (?, ?, ?, ?, ?, ?)
                """, entry.entryId(), entry.priceListId(), entry.itemType(), entry.itemRefId(),
                entry.price().currency(), entry.price().amount());
        return entry;
    }

    @Override
    public List<PriceEntry> findEntries(String priceListId) {
        return jdbcTemplate.query("""
                select entry_id, price_list_id, item_type, item_ref_id, currency, amount
                from catalog.price_entries
                where price_list_id = ?
                """, (resultSet, rowNumber) -> new PriceEntry(
                        resultSet.getString("entry_id"),
                        resultSet.getString("price_list_id"),
                        resultSet.getString("item_type"),
                        resultSet.getString("item_ref_id"),
                        new Money(resultSet.getString("currency"), resultSet.getBigDecimal("amount"))),
                priceListId);
    }

    private static PriceList mapPriceList(ResultSet resultSet, int rowNumber) throws SQLException {
        return new PriceList(
                resultSet.getString("price_list_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"),
                resultSet.getString("code"),
                new LocalizedText(resultSet.getString("name_en"), resultSet.getString("name_es")),
                resultSet.getString("currency"),
                resultSet.getString("agreement_ref_id"),
                resultSet.getObject("effective_from", java.time.LocalDate.class),
                resultSet.getObject("effective_to", java.time.LocalDate.class),
                resultSet.getString("status"),
                resultSet.getInt("version"),
                instant(resultSet, "created_at"),
                instant(resultSet, "updated_at"));
    }

    private static Instant instant(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toInstant();
    }
}
