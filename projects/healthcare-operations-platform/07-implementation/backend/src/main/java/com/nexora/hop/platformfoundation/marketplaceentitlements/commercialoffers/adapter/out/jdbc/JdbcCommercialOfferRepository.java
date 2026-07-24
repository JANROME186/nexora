package com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.domain.CommercialOffer;
import com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.domain.CommercialOfferRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcCommercialOfferRepository implements CommercialOfferRepository {

    private static final String SELECT_SQL = """
            select offer_id, package_id, package_version, offer_code, offer_type, lifecycle_status,
                   tier_codes_text, trial_period_days, billing_event_rules_summary, effective_version,
                   created_by, created_at, updated_by, updated_at
            from marketplace_entitlements.commercial_offers
            """;

    private final JdbcTemplate jdbcTemplate;

    JdbcCommercialOfferRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CommercialOffer save(CommercialOffer offer) {
        jdbcTemplate.update("""
                insert into marketplace_entitlements.commercial_offers
                    (offer_id, package_id, package_version, offer_code, offer_type, lifecycle_status,
                     tier_codes_text, trial_period_days, billing_event_rules_summary, effective_version,
                     created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (offer_id) do update set
                    lifecycle_status = excluded.lifecycle_status, effective_version = excluded.effective_version,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                offer.offerId(), offer.packageId(), offer.packageVersion(), offer.offerCode(), offer.offerType(),
                offer.lifecycleStatus(), String.join(",", offer.tierCodes()), offer.trialPeriodDays(),
                offer.billingEventRulesSummary(), offer.effectiveVersion(), offer.audit().createdBy(),
                Timestamp.valueOf(offer.audit().createdAt()), offer.audit().updatedBy(),
                Timestamp.valueOf(offer.audit().updatedAt()));
        return offer;
    }

    @Override
    public Optional<CommercialOffer> findById(String offerId) {
        return jdbcTemplate.query(SELECT_SQL + " where offer_id = ?",
                JdbcCommercialOfferRepository::map, offerId).stream().findFirst();
    }

    @Override
    public List<CommercialOffer> findByPackageId(String packageId) {
        return jdbcTemplate.query(SELECT_SQL + " where package_id = ?",
                JdbcCommercialOfferRepository::map, packageId);
    }

    @Override
    public List<CommercialOffer> findAll() {
        return jdbcTemplate.query(SELECT_SQL, JdbcCommercialOfferRepository::map);
    }

    private static CommercialOffer map(ResultSet resultSet, int rowNumber) throws SQLException {
        String tiersText = resultSet.getString("tier_codes_text");
        List<String> tiers = tiersText == null || tiersText.isBlank() ? List.of() : Arrays.asList(tiersText.split(","));
        int trialDays = resultSet.getInt("trial_period_days");
        return new CommercialOffer(
                resultSet.getString("offer_id"),
                resultSet.getString("package_id"),
                resultSet.getString("package_version"),
                resultSet.getString("offer_code"),
                resultSet.getString("offer_type"),
                resultSet.getString("lifecycle_status"),
                tiers,
                resultSet.wasNull() ? null : trialDays,
                resultSet.getString("billing_event_rules_summary"),
                resultSet.getInt("effective_version"),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
