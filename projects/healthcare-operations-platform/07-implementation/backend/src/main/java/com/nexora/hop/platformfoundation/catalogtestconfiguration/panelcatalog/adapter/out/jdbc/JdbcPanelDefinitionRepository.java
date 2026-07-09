package com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain.PanelDefinition;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain.PanelDefinitionRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain.PanelMember;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;

@Repository
@Profile("local")
class JdbcPanelDefinitionRepository implements PanelDefinitionRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcPanelDefinitionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PanelDefinition save(PanelDefinition panel) {
        jdbcTemplate.update("""
                insert into catalog.panel_definitions
                    (panel_id, tenant_id, laboratory_id, code, name_en, name_es, status, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (panel_id) do update set
                    code = excluded.code, name_en = excluded.name_en, name_es = excluded.name_es,
                    status = excluded.status, version = excluded.version, updated_at = excluded.updated_at
                """,
                panel.panelId(), panel.tenantId(), panel.laboratoryId(), panel.code(),
                panel.name().en(), panel.name().es(), panel.status(), panel.version(),
                Timestamp.from(panel.createdAt()), Timestamp.from(panel.updatedAt()));
        return panel;
    }

    @Override
    public Optional<PanelDefinition> findById(String panelId) {
        return jdbcTemplate.query("""
                select panel_id, tenant_id, laboratory_id, code, name_en, name_es, status, version, created_at, updated_at
                from catalog.panel_definitions
                where panel_id = ?
                """, JdbcPanelDefinitionRepository::mapPanel, panelId).stream().findFirst();
    }

    @Override
    public List<PanelDefinition> findByLaboratoryId(String laboratoryId) {
        return jdbcTemplate.query("""
                select panel_id, tenant_id, laboratory_id, code, name_en, name_es, status, version, created_at, updated_at
                from catalog.panel_definitions
                where laboratory_id = ?
                """, JdbcPanelDefinitionRepository::mapPanel, laboratoryId);
    }

    @Override
    public boolean existsByCode(String laboratoryId, String code, String excludePanelId) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(*) from catalog.panel_definitions
                where laboratory_id = ? and code = ? and panel_id <> ?
                """, Integer.class, laboratoryId, code, excludePanelId == null ? "" : excludePanelId);
        return count != null && count > 0;
    }

    @Override
    @Transactional
    public void replaceMembers(String panelId, List<PanelMember> members) {
        jdbcTemplate.update("delete from catalog.panel_members where panel_id = ?", panelId);
        for (PanelMember member : members) {
            jdbcTemplate.update("""
                    insert into catalog.panel_members (member_id, panel_id, test_ref_id, display_order, mandatory)
                    values (?, ?, ?, ?, ?)
                    """, member.memberId(), panelId, member.testRefId(), member.displayOrder(), member.mandatory());
        }
    }

    @Override
    public List<PanelMember> findMembers(String panelId) {
        return jdbcTemplate.query("""
                select member_id, panel_id, test_ref_id, display_order, mandatory
                from catalog.panel_members
                where panel_id = ?
                """, (resultSet, rowNumber) -> {
            int displayOrder = resultSet.getInt("display_order");
            return new PanelMember(
                    resultSet.getString("member_id"),
                    resultSet.getString("panel_id"),
                    resultSet.getString("test_ref_id"),
                    resultSet.wasNull() ? null : displayOrder,
                    resultSet.getBoolean("mandatory"));
        }, panelId);
    }

    private static PanelDefinition mapPanel(ResultSet resultSet, int rowNumber) throws SQLException {
        return new PanelDefinition(
                resultSet.getString("panel_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"),
                resultSet.getString("code"),
                new LocalizedText(resultSet.getString("name_en"), resultSet.getString("name_es")),
                resultSet.getString("status"),
                resultSet.getInt("version"),
                instant(resultSet, "created_at"),
                instant(resultSet, "updated_at"));
    }

    private static Instant instant(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toInstant();
    }
}
