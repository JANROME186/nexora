package com.nexora.hop.platformfoundation.inventoryquality;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * PostgreSQL-backed integration test for the BCM-QLT-001/003/004/005 capabilities added by
 * COM-MOD-010-BE-002. Executed only when {@code -Dhop.local-db-tests=true} is set (typically
 * together with a running {@code compose.local.yml}). Mirrors
 * {@code InventoryQualityLocalDatabaseTest} (which only exercised BCM-INV-001..009) so the four
 * quality-capability JDBC adapters (internal quality controls, calibration, equipment,
 * maintenance) get exercised end-to-end against a real PostgreSQL database.
 */
@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class InventoryQualityControlsLocalDatabaseTest {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void internalQualityQuarterSchemaIsInitializedInPostgres() {
        Integer tableCount = jdbcTemplate.queryForObject("""
                select count(*)
                  from information_schema.tables
                 where table_schema = 'inventory_quality'
                   and table_name in (
                       'quality_control_runs', 'calibration_events',
                       'equipment_availability_changes', 'maintenance_events')
                """, Integer.class);
        assertThat(tableCount).isEqualTo(4);
    }

    @Test
    void internalQualityControlRunRoundTripAgainstRealPostgres() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"JDBC QLT Tenant " + token + "\"}");
        String tenantId = tenant.get("tenantId").asText();
        String lab = "lab-jdbc-qlt-" + token;
        String branch = "branch-jdbc-qlt-" + token;

        JsonNode controlItem = postJson("/api/inventory/catalog/items", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","itemCode":"QC-MAT-%s",
                 "itemName":"JDBC Control Material","itemType":"reagent",
                 "classification":"calibrator_control_material","unitOfMeasure":"ml",
                 "actorId":"admin"}
                """.formatted(tenantId, lab, branch, token));
        String controlItemId = controlItem.get("inventoryItemId").asText();
        JsonNode lot = postJson("/api/inventory/lots/items/" + controlItemId + "/lots", """
                {"lotNumber":"QC-LOT-%s","receivedQuantity":10.0,"actorId":"admin"}
                """.formatted(token));
        String lotId = lot.get("stockLotId").asText();

        JsonNode qcRun = postJson("/api/quality/internal-controls", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","testDefinitionId":"TST-GLU-01",
                 "controlMaterialStockLotId":"%s","measuredValue":100.0,"expectedMin":90.0,
                 "expectedMax":110.0,"performedBy":"tech-1","evidenceReference":"EVID-1"}
                """.formatted(tenantId, lab, branch, lotId));
        String qcRunId = qcRun.get("qcRunId").asText();
        assertThat(qcRun.get("acceptanceDecision").asText()).isEqualTo("accepted");

        mockMvc.perform(get("/api/quality/internal-controls/{id}", qcRunId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qcRunId").value(qcRunId));

        postJson("/api/quality/internal-controls/" + qcRunId + "/override", """
                {"acceptanceDecision":"repeat_required","overrideReason":"Recheck required",
                 "supervisorId":"sup-1","supervisorScoped":true}
                """);
        mockMvc.perform(get("/api/quality/internal-controls/{id}", qcRunId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acceptanceDecision").value("repeat_required"));

        mockMvc.perform(get("/api/quality/internal-controls")
                        .param("tenantId", tenantId).param("laboratoryId", lab).param("branchId", branch))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].qcRunId").value(qcRunId));
    }

    @Test
    void equipmentCalibrationAndMaintenanceRoundTripAgainstRealPostgres() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"JDBC Equipment Tenant " + token + "\"}");
        String tenantId = tenant.get("tenantId").asText();
        String lab = "lab-jdbc-eq-" + token;
        String branch = "branch-jdbc-eq-" + token;

        JsonNode equipmentItem = postJson("/api/inventory/catalog/items", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","itemCode":"EQP-%s",
                 "itemName":"JDBC Analyzer","itemType":"equipment","classification":"capital_equipment",
                 "unitOfMeasure":"unit","actorId":"admin"}
                """.formatted(tenantId, lab, branch, token));
        String equipmentItemId = equipmentItem.get("inventoryItemId").asText();

        // equipment profile
        postJson("/api/quality/equipment/items/" + equipmentItemId + "/equipment-profile", """
                {"assetTag":"ASSET-%s","serialNumber":"SN-1","manufacturer":"Acme",
                 "model":"X100","location":"Bench 1","availabilityStatus":"available",
                 "actorId":"admin"}
                """.formatted(token));
        mockMvc.perform(get("/api/quality/equipment/items/{id}/equipment-profile", equipmentItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetTag").value("ASSET-" + token));

        // availability change
        postJson("/api/quality/equipment/items/" + equipmentItemId + "/availability", """
                {"newStatus":"out_of_service","reasonCode":"maintenance_scheduled","actorId":"admin"}
                """);
        mockMvc.perform(get("/api/quality/equipment/items/{id}/availability", equipmentItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].newStatus").value("out_of_service"));

        // calibration
        JsonNode calibration = postJson("/api/quality/calibrations/items/" + equipmentItemId + "/calibrations", """
                {"calibrationStandardRef":"STD-1","performedBy":"tech-1","result":"pass",
                 "nextDueDate":"2027-01-01","certificateReference":"CERT-1"}
                """);
        assertThat(calibration.get("result").asText()).isEqualTo("pass");
        mockMvc.perform(get("/api/quality/calibrations/items/{id}/calibrations", equipmentItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].calibrationStandardRef").value("STD-1"));

        // maintenance record + complete
        JsonNode maintenance = postJson("/api/quality/maintenance/items/" + equipmentItemId + "/maintenance", """
                {"maintenanceType":"preventive","performedBy":"tech-1",
                 "description":"Routine annual service","startedAt":"2026-07-01T08:00:00"}
                """);
        String maintenanceEventId = maintenance.get("maintenanceEventId").asText();
        postJson("/api/quality/maintenance/maintenance/" + maintenanceEventId + "/complete", """
                {"actorId":"tech-1","completedAt":"2026-07-01T10:00:00","downtimeMinutes":120}
                """);
        mockMvc.perform(get("/api/quality/maintenance/items/{id}/maintenance", equipmentItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].maintenanceEventId").value(maintenanceEventId))
                .andExpect(jsonPath("$[0].downtimeMinutes").value(120));
    }

    private JsonNode postJson(String path, String json) throws Exception {
        MvcResult result = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
