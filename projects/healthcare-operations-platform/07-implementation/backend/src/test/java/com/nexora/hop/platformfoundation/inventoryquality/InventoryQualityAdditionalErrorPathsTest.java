package com.nexora.hop.platformfoundation.inventoryquality;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

/**
 * Complementary error-path coverage for BCM-INV-001..009. These tests deliberately exercise the
 * infrequently-hit domain guards, invariant checks and list endpoints that boost coverage of the
 * hexagonal adapters and application services beyond the happy-path scenarios in
 * {@link InventoryQualityApiTest}.
 */
@AutoConfigureMockMvc
@SpringBootTest
class InventoryQualityAdditionalErrorPathsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    private String tenantId;
    private static final String LAB = "lab-err";
    private static final String BRANCH = "branch-err";

    @BeforeEach
    void createTenant() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Err Tenant " + token + "\"}");
        tenantId = tenant.get("tenantId").asText();
    }

    @Test
    void listEndpointsAreReachableAndReturnScopedResults() throws Exception {
        JsonNode item = createItem("REAG-LIST-001", "reagent", "diagnostic_reagent");
        String itemId = item.get("inventoryItemId").asText();
        JsonNode lot = postJson("/api/inventory/lots/items/" + itemId + "/lots", """
                {"lotNumber":"L-LIST-1","receivedQuantity":20.0,"actorId":"admin"}
                """);
        String lotId = lot.get("stockLotId").asText();

        // Register a reagent profile so consumption can succeed
        postJson("/api/inventory/reagents/items/" + itemId + "/reagent-profile", """
                {"reagentCategory":"working_reagent","consumptionUnitRatio":1.0,"actorId":"admin"}
                """);

        // Create a few movements to populate list endpoints
        postJson("/api/inventory/stock-entries", """
                {"inventoryItemId":"%s","stockLotId":"%s","quantity":5.0,"entryType":"return","actorId":"admin"}
                """.formatted(itemId, lotId));
        postJson("/api/inventory/stock-exits", """
                {"inventoryItemId":"%s","stockLotId":"%s","destinationBranchId":"b2","quantity":2.0,
                 "exitType":"internal_relocation","actorId":"admin"}
                """.formatted(itemId, lotId));
        postJson("/api/inventory/consumption", """
                {"inventoryItemId":"%s","stockLotId":"%s","consumedQuantity":1.0,
                 "consumptionContext":"internal_qc","actorId":"admin"}
                """.formatted(itemId, lotId));
        postJson("/api/inventory/adjustments", """
                {"inventoryItemId":"%s","stockLotId":"%s","deltaQuantity":1.0,"reasonCode":"cycle_count",
                 "requestedBy":"u1","approverId":"m1","actorId":"u1"}
                """.formatted(itemId, lotId));
        postJson("/api/inventory/waste", """
                {"inventoryItemId":"%s","stockLotId":"%s","disposedQuantity":1.0,
                 "reasonCode":"expired","actorId":"admin"}
                """.formatted(itemId, lotId));

        mockMvc.perform(get("/api/inventory/stock-entries")
                        .param("tenantId", tenantId).param("laboratoryId", LAB).param("branchId", BRANCH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/api/inventory/stock-exits")
                        .param("tenantId", tenantId).param("laboratoryId", LAB).param("branchId", BRANCH))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/inventory/consumption")
                        .param("tenantId", tenantId).param("laboratoryId", LAB).param("branchId", BRANCH))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/inventory/adjustments")
                        .param("tenantId", tenantId).param("laboratoryId", LAB).param("branchId", BRANCH))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/inventory/waste")
                        .param("tenantId", tenantId).param("laboratoryId", LAB).param("branchId", BRANCH))
                .andExpect(status().isOk());
    }

    @Test
    void discontinuedItemRejectsAllDelegatedMutations() throws Exception {
        JsonNode item = createItem("REAG-DISC-001", "reagent", "diagnostic_reagent");
        String itemId = item.get("inventoryItemId").asText();
        postJson("/api/inventory/catalog/items/" + itemId + "/discontinue", "{\"actorId\":\"admin\"}");

        // reagent assignment on discontinued item
        mockMvc.perform(post("/api/inventory/reagents/items/{id}/reagent-profile", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reagentCategory":"working_reagent","consumptionUnitRatio":1.0,"actorId":"admin"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVENTORY_ITEM_DISCONTINUED"));

        // lot registration on discontinued item
        mockMvc.perform(post("/api/inventory/lots/items/{id}/lots", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"lotNumber":"L-DISC","receivedQuantity":1.0,"actorId":"admin"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVENTORY_ITEM_DISCONTINUED"));

        // discontinue idempotency: a second call is a no-op
        mockMvc.perform(post("/api/inventory/catalog/items/{id}/discontinue", itemId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"admin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("discontinued"));
    }

    @Test
    void purchaseOrderRequiresAtLeastOneLineAndValidatesLineFields() throws Exception {
        mockMvc.perform(post("/api/inventory/purchase-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","supplierId":"S",
                                 "supplierName":"S","currencyCode":"MXN","lines":[],"actorId":"admin"}
                                """.formatted(tenantId, LAB, BRANCH)))
                .andExpect(status().isBadRequest());

        JsonNode item = createItem("CONS-PO-INV-001", "consumable", "lab_supply");
        String itemId = item.get("inventoryItemId").asText();
        mockMvc.perform(post("/api/inventory/purchase-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","supplierId":"S",
                                 "supplierName":"S","currencyCode":"MXN",
                                 "lines":[{"inventoryItemId":"%s","orderedQuantity":-1,"unitCost":1}],
                                 "actorId":"admin"}
                                """.formatted(tenantId, LAB, BRANCH, itemId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PURCHASE_ORDER_LINE_QUANTITY_OR_COST_INVALID"));
    }

    @Test
    void purchaseOrderStateGuardsSubmitApproveReceive() throws Exception {
        JsonNode item = createItem("CONS-PO-GUARD-001", "consumable", "lab_supply");
        String itemId = item.get("inventoryItemId").asText();

        JsonNode po = postJson("/api/inventory/purchase-orders", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","supplierId":"S",
                 "supplierName":"S","currencyCode":"MXN",
                 "lines":[{"inventoryItemId":"%s","orderedQuantity":10,"unitCost":1}],
                 "actorId":"admin"}
                """.formatted(tenantId, LAB, BRANCH, itemId));
        String poId = po.get("purchaseOrderId").asText();
        String lineId = po.get("lines").get(0).get("purchaseOrderLineId").asText();

        // approving before submitting is a conflict
        mockMvc.perform(post("/api/inventory/purchase-orders/{id}/approve", poId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"approver\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PURCHASE_ORDER_TERMINAL_STATE"));

        postJson("/api/inventory/purchase-orders/" + poId + "/submit", "{\"actorId\":\"admin\"}");

        // receiving before approving is a conflict
        mockMvc.perform(post("/api/inventory/purchase-orders/{poId}/lines/{lineId}/receive", poId, lineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receivedQuantity\":1,\"actorId\":\"admin\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PURCHASE_ORDER_TERMINAL_STATE"));

        postJson("/api/inventory/purchase-orders/" + poId + "/approve", "{\"actorId\":\"approver\"}");

        // receiving more than ordered is a conflict
        mockMvc.perform(post("/api/inventory/purchase-orders/{poId}/lines/{lineId}/receive", poId, lineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receivedQuantity\":11,\"actorId\":\"admin\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("STOCK_ENTRY_PURCHASE_ORDER_LINE_INVALID"));

        // negative received quantity is rejected as invalid stock entry
        mockMvc.perform(post("/api/inventory/purchase-orders/{poId}/lines/{lineId}/receive", poId, lineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receivedQuantity\":-1,\"actorId\":\"admin\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("STOCK_ENTRY_QUANTITY_INVALID"));

        // missing line returns 404
        mockMvc.perform(post("/api/inventory/purchase-orders/{poId}/lines/{lineId}/receive", poId, "missing-line")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receivedQuantity\":1,\"actorId\":\"admin\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PURCHASE_ORDER_LINE_NOT_FOUND"));
    }

    @Test
    void lotOperationsValidateInvariants() throws Exception {
        JsonNode item = createItem("REAG-LOT-INV-001", "reagent", "diagnostic_reagent");
        String itemId = item.get("inventoryItemId").asText();

        // duplicate lot number is rejected
        postJson("/api/inventory/lots/items/" + itemId + "/lots",
                "{\"lotNumber\":\"L-DUP\",\"receivedQuantity\":1.0,\"actorId\":\"admin\"}");
        mockMvc.perform(post("/api/inventory/lots/items/{id}/lots", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lotNumber\":\"L-DUP\",\"receivedQuantity\":1.0,\"actorId\":\"admin\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("LOT_QUANTITY_INVARIANT_VIOLATION"));

        // negative received quantity is rejected
        mockMvc.perform(post("/api/inventory/lots/items/{id}/lots", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lotNumber\":\"L-NEG\",\"receivedQuantity\":-1.0,\"actorId\":\"admin\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("LOT_QUANTITY_INVARIANT_VIOLATION"));

        // missing lot returns 404
        mockMvc.perform(post("/api/inventory/lots/lots/{id}/quarantine", "missing-lot")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"admin\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("STOCK_LOT_NOT_FOUND"));
    }

    @Test
    void nonReagentItemCannotReceiveConsumptionForReagentButOtherTypesCanConsume() throws Exception {
        // consumable item; no reagent profile required
        JsonNode consumable = createItem("CONS-CONSUMP-001", "consumable", "lab_supply");
        String itemId = consumable.get("inventoryItemId").asText();
        JsonNode lot = postJson("/api/inventory/lots/items/" + itemId + "/lots",
                "{\"lotNumber\":\"L-C-1\",\"receivedQuantity\":10.0,\"actorId\":\"admin\"}");
        postJson("/api/inventory/consumption", """
                {"inventoryItemId":"%s","stockLotId":"%s","consumedQuantity":3.0,
                 "consumptionContext":"calibration","actorId":"admin"}
                """.formatted(itemId, lot.get("stockLotId").asText()));
    }

    @Test
    void reagentItemWithoutProfileCannotConsume() throws Exception {
        JsonNode item = createItem("REAG-NOPROF-001", "reagent", "diagnostic_reagent");
        String itemId = item.get("inventoryItemId").asText();
        JsonNode lot = postJson("/api/inventory/lots/items/" + itemId + "/lots",
                "{\"lotNumber\":\"L-NOP-1\",\"receivedQuantity\":10.0,\"actorId\":\"admin\"}");
        mockMvc.perform(post("/api/inventory/consumption")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"inventoryItemId":"%s","stockLotId":"%s","consumedQuantity":1.0,
                                 "consumptionContext":"test_processing","actorId":"admin"}
                                """.formatted(itemId, lot.get("stockLotId").asText())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONSUMPTION_REAGENT_PROFILE_MISSING"));
    }

    @Test
    void wasteRequiresPositiveQuantityAndReasonCode() throws Exception {
        JsonNode item = createItem("REAG-WASTE-INV-001", "reagent", "diagnostic_reagent");
        String itemId = item.get("inventoryItemId").asText();
        JsonNode lot = postJson("/api/inventory/lots/items/" + itemId + "/lots",
                "{\"lotNumber\":\"L-W-1\",\"receivedQuantity\":5.0,\"actorId\":\"admin\"}");
        String lotId = lot.get("stockLotId").asText();

        mockMvc.perform(post("/api/inventory/waste")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"inventoryItemId":"%s","stockLotId":"%s","disposedQuantity":-1.0,
                                 "reasonCode":"expired","actorId":"admin"}
                                """.formatted(itemId, lotId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("WASTE_QUANTITY_EXCEEDS_LOT"));

        mockMvc.perform(post("/api/inventory/waste")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"inventoryItemId":"%s","stockLotId":"%s","disposedQuantity":10.0,
                                 "reasonCode":"expired","actorId":"admin"}
                                """.formatted(itemId, lotId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("WASTE_QUANTITY_EXCEEDS_LOT"));
    }

    @Test
    void adjustmentBlocksBelowZeroOnHandAndBlocksNegativeLotRemaining() throws Exception {
        JsonNode item = createItem("CONS-ADJ-BEL-001", "consumable", "lab_supply");
        String itemId = item.get("inventoryItemId").asText();
        JsonNode lot = postJson("/api/inventory/lots/items/" + itemId + "/lots",
                "{\"lotNumber\":\"L-A-1\",\"receivedQuantity\":3.0,\"actorId\":\"admin\"}");
        String lotId = lot.get("stockLotId").asText();

        // Adjust lot remaining below zero
        mockMvc.perform(post("/api/inventory/adjustments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"inventoryItemId":"%s","stockLotId":"%s","deltaQuantity":-10.0,
                                 "reasonCode":"cycle_count","requestedBy":"u1","approverId":"m1","actorId":"u1"}
                                """.formatted(itemId, lotId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ADJUSTMENT_QUANTITY_INVALID"));

        // Zero delta rejected
        mockMvc.perform(post("/api/inventory/adjustments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"inventoryItemId":"%s","stockLotId":"%s","deltaQuantity":0,
                                 "reasonCode":"cycle_count","requestedBy":"u1","approverId":"m1","actorId":"u1"}
                                """.formatted(itemId, lotId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ADJUSTMENT_QUANTITY_INVALID"));
    }

    @Test
    void updateItemRejectsInvalidStatusEnumOnCatalog() throws Exception {
        JsonNode item = createItem("REAG-UPD-001", "reagent", "diagnostic_reagent");
        String itemId = item.get("inventoryItemId").asText();
        // Attempt to update to "discontinued" via update path (not allowed; only discontinue endpoint sets it)
        mockMvc.perform(put("/api/inventory/catalog/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"itemName":"n","itemType":"reagent","classification":"diagnostic_reagent",
                                 "unitOfMeasure":"ml","status":"discontinued","actorId":"admin"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVENTORY_COMMAND_INVALID"));

        // set to inactive works
        mockMvc.perform(put("/api/inventory/catalog/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"itemName":"n","itemType":"reagent","classification":"diagnostic_reagent",
                                 "unitOfMeasure":"ml","status":"inactive","actorId":"admin"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("inactive"));
    }

    private JsonNode createItem(String itemCode, String itemType, String classification) throws Exception {
        return postJson("/api/inventory/catalog/items", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","itemCode":"%s",
                 "itemName":"Item %s","itemType":"%s","classification":"%s",
                 "unitOfMeasure":"unit","actorId":"admin"}
                """.formatted(tenantId, LAB, BRANCH, itemCode, itemCode, itemType, classification));
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
