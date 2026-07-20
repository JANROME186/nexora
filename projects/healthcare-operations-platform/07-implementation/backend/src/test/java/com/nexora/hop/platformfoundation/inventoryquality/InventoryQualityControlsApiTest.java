package com.nexora.hop.platformfoundation.inventoryquality;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/** End-to-end coverage for COM-MOD-010-BE-002 QLT capability outputs. */
@AutoConfigureMockMvc
@SpringBootTest
class InventoryQualityControlsApiTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String tenantId;
    private static final String LAB = "lab-qlt";
    private static final String BRANCH = "branch-qlt";

    @BeforeEach
    void createTenant() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Quality Tenant " + token + "\"}");
        tenantId = tenant.get("tenantId").asText();
    }

    @Test
    void equipmentProfileAndRetiredTransitionRulesAreEnforced() throws Exception {
        JsonNode reagent = createItem("QLT-REAG-001", "reagent", "diagnostic_reagent");
        mockMvc.perform(post("/api/quality/equipment/items/{id}/equipment-profile", reagent.get("inventoryItemId").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"assetTag":"TAG-R","availabilityStatus":"available","actorId":"admin"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EQUIPMENT_ITEM_TYPE_NOT_ELIGIBLE"));

        JsonNode equipment = createEquipment("QLT-EQ-001");
        String equipmentId = equipment.get("inventoryItemId").asText();
        postJson("/api/quality/equipment/items/" + equipmentId + "/equipment-profile", """
                {"assetTag":"TAG-1","serialNumber":"SN-1","manufacturer":"Acme","model":"MX",
                 "availabilityStatus":"available","actorId":"admin"}
                """);
        postJson("/api/quality/equipment/items/" + equipmentId + "/availability", """
                {"newStatus":"retired","reasonCode":"decommissioned","actorId":"admin"}
                """);

        mockMvc.perform(post("/api/quality/equipment/items/{id}/availability", equipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"newStatus":"available","reasonCode":"routine","actorId":"admin"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EQUIPMENT_RETIRED_TRANSITION_FORBIDDEN"));
    }

    @Test
    void failedCalibrationTransitionsEquipmentOutOfService() throws Exception {
        JsonNode equipment = createEquipmentWithProfile("QLT-EQ-CAL-001");
        String equipmentId = equipment.get("inventoryItemId").asText();

        JsonNode calibration = postJson("/api/quality/calibrations/items/" + equipmentId + "/calibrations", """
                {"calibrationStandardRef":"STD-1","performedBy":"cal-tech","performedAt":"2026-07-20T10:00:00",
                 "result":"fail","nextDueDate":"2026-07-21","certificateReference":"CERT-1"}
                """);
        assertThat(calibration.get("result").asText()).isEqualTo("fail");

        JsonNode profile = getJson("/api/quality/equipment/items/" + equipmentId + "/equipment-profile");
        assertThat(profile.get("availabilityStatus").asText()).isEqualTo("out_of_service");
    }

    @Test
    void maintenanceScheduleAndCompletionTransitionEquipmentAvailability() throws Exception {
        JsonNode equipment = createEquipmentWithProfile("QLT-EQ-MAINT-001");
        String equipmentId = equipment.get("inventoryItemId").asText();

        JsonNode maintenance = postJson("/api/quality/maintenance/items/" + equipmentId + "/maintenance", """
                {"maintenanceType":"preventive","performedBy":"maint-tech","description":"Quarterly review",
                 "startedAt":"2026-07-20T09:00:00"}
                """);
        String maintenanceId = maintenance.get("maintenanceEventId").asText();
        assertThat(getJson("/api/quality/equipment/items/" + equipmentId + "/equipment-profile")
                .get("availabilityStatus").asText()).isEqualTo("out_of_service");

        mockMvc.perform(post("/api/quality/maintenance/maintenance/{id}/complete", maintenanceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"actorId":"maint-tech","completedAt":"2026-07-20T08:59:00"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("MAINTENANCE_COMPLETED_BEFORE_STARTED"));

        postJson("/api/quality/maintenance/maintenance/" + maintenanceId + "/complete", """
                {"actorId":"maint-tech","completedAt":"2026-07-20T11:00:00"}
                """);
        assertThat(getJson("/api/quality/equipment/items/" + equipmentId + "/equipment-profile")
                .get("availabilityStatus").asText()).isEqualTo("available");
    }

    @Test
    void qualityControlRunRequiresEligibleControlMaterialAndSupervisorOverride() throws Exception {
        JsonNode reagent = createItem("QLT-REAG-BAD-001", "reagent", "diagnostic_reagent");
        String reagentLotId = createLot(reagent.get("inventoryItemId").asText(), "BAD-LOT").get("stockLotId").asText();

        mockMvc.perform(post("/api/quality/internal-controls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(qcRunJson(reagentLotId, "5.0", "1.0", "10.0")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("QC_CONTROL_MATERIAL_LOT_INELIGIBLE"));

        JsonNode control = createItem("QLT-CTRL-001", "reagent", "calibrator_control_material");
        String controlLotId = createLot(control.get("inventoryItemId").asText(), "CTRL-LOT").get("stockLotId").asText();
        JsonNode outOfControlRun =
                postJson("/api/quality/internal-controls", qcRunJson(controlLotId, "15.0", "1.0", "10.0"));
        String qcRunId = outOfControlRun.get("qcRunId").asText();
        assertThat(outOfControlRun.get("ruleEvaluation").asText()).isEqualTo("out_of_control");
        assertThat(outOfControlRun.get("acceptanceDecision").asText()).isEqualTo("repeat_required");

        mockMvc.perform(post("/api/quality/internal-controls/{id}/override", qcRunId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"acceptanceDecision":"accepted","overrideReason":"Known control drift",
                                 "supervisorId":"sup-1","supervisorScoped":false}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("QC_OVERRIDE_NOT_AUTHORIZED"));

        JsonNode override = postJson("/api/quality/internal-controls/" + qcRunId + "/override", """
                {"acceptanceDecision":"accepted","overrideReason":"Known control drift",
                 "supervisorId":"sup-1","supervisorScoped":true}
                """);
        assertThat(override.get("acceptanceDecision").asText()).isEqualTo("accepted");
        assertThat(override.get("overrideBy").asText()).isEqualTo("sup-1");
    }

    private JsonNode createEquipmentWithProfile(String itemCode) throws Exception {
        JsonNode equipment = createEquipment(itemCode);
        postJson("/api/quality/equipment/items/" + equipment.get("inventoryItemId").asText() + "/equipment-profile", """
                {"assetTag":"%s-TAG","serialNumber":"SN","manufacturer":"Acme","model":"MX",
                 "availabilityStatus":"available","actorId":"admin"}
                """.formatted(itemCode));
        return equipment;
    }

    private JsonNode createEquipment(String itemCode) throws Exception {
        return createItem(itemCode, "equipment", "capital_equipment");
    }

    private JsonNode createItem(String itemCode, String itemType, String classification) throws Exception {
        return postJson("/api/inventory/catalog/items", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","itemCode":"%s",
                 "itemName":"Item %s","itemType":"%s","classification":"%s",
                 "unitOfMeasure":"unit","actorId":"admin"}
                """.formatted(tenantId, LAB, BRANCH, itemCode, itemCode, itemType, classification));
    }

    private JsonNode createLot(String itemId, String lotNumber) throws Exception {
        return postJson("/api/inventory/lots/items/" + itemId + "/lots", """
                {"lotNumber":"%s","receivedQuantity":10.0,"actorId":"admin"}
                """.formatted(lotNumber));
    }

    private String qcRunJson(String stockLotId, String measured, String expectedMin, String expectedMax) {
        return """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","testDefinitionId":"test-cbc",
                 "controlMaterialStockLotId":"%s","measuredValue":%s,"expectedMin":%s,"expectedMax":%s,
                 "linkedLaboratoryResultIds":["result-1"],"performedBy":"qc-tech",
                 "performedAt":"2026-07-20T08:00:00","evidenceReference":"qc-evidence"}
                """.formatted(tenantId, LAB, BRANCH, stockLotId, measured, expectedMin, expectedMax);
    }

    private JsonNode postJson(String path, String json) throws Exception {
        MvcResult result = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private JsonNode getJson(String path) throws Exception {
        MvcResult result = mockMvc.perform(get(path))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
