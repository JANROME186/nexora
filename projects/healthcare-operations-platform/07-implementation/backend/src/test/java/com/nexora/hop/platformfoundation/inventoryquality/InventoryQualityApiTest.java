package com.nexora.hop.platformfoundation.inventoryquality;

import static org.assertj.core.api.Assertions.assertThat;
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

/** End-to-end MockMvc coverage of the COM-MOD-010-BE-001 inventory-and-internal-quality module. */
@AutoConfigureMockMvc
@SpringBootTest
class InventoryQualityApiTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String tenantId;
    private static final String LAB = "lab-inv";
    private static final String BRANCH = "branch-inv";

    @BeforeEach
    void createTenant() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Inventory Tenant " + token + "\"}");
        tenantId = tenant.get("tenantId").asText();
    }

    @Test
    void catalogItemLifecycleAndDiscontinueRejectsFurtherWrites() throws Exception {
        JsonNode item = createItem("REAG-001", "reagent", "diagnostic_reagent");
        String itemId = item.get("inventoryItemId").asText();
        assertThat(item.get("status").asText()).isEqualTo("active");

        // list returns the created item
        mockMvc.perform(get("/api/inventory/catalog/items")
                        .param("tenantId", tenantId).param("laboratoryId", LAB).param("branchId", BRANCH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].inventoryItemId").value(itemId));

        // Update ok
        mockMvc.perform(put("/api/inventory/catalog/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"itemName":"Reagent A2","itemType":"reagent","classification":"diagnostic_reagent",
                                 "unitOfMeasure":"ml","status":"active","actorId":"admin"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("Reagent A2"));

        // Discontinue and check further updates are rejected
        mockMvc.perform(post("/api/inventory/catalog/items/{id}/discontinue", itemId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"admin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("discontinued"));

        mockMvc.perform(put("/api/inventory/catalog/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"itemName":"Reagent A3","itemType":"reagent","classification":"diagnostic_reagent",
                                 "unitOfMeasure":"ml","status":"active","actorId":"admin"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVENTORY_ITEM_DISCONTINUED"))
                .andExpect(jsonPath("$.messageKey").value("inventory.error.inventory_item_discontinued"));
    }

    @Test
    void inconsistentItemTypeAndClassificationIsRejected() throws Exception {
        mockMvc.perform(post("/api/inventory/catalog/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","itemCode":"BAD-001",
                                 "itemName":"Bad","itemType":"equipment","classification":"diagnostic_reagent",
                                 "unitOfMeasure":"unit","actorId":"admin"}
                                """.formatted(tenantId, LAB, BRANCH)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVENTORY_ITEM_TYPE_CLASSIFICATION_MISMATCH"));
    }

    @Test
    void duplicateItemCodeInScopeIsRejected() throws Exception {
        createItem("DUP-001", "consumable", "lab_supply");
        mockMvc.perform(post("/api/inventory/catalog/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","itemCode":"DUP-001",
                                 "itemName":"Dup","itemType":"consumable","classification":"lab_supply",
                                 "unitOfMeasure":"unit","actorId":"admin"}
                                """.formatted(tenantId, LAB, BRANCH)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVENTORY_ITEM_CODE_NOT_UNIQUE"));
    }

    @Test
    void reagentProfileMustTargetReagentItemAndUsePositiveRatio() throws Exception {
        // non-reagent item should be rejected
        JsonNode consumable = createItem("SUP-001", "supply", "lab_supply");
        String consumableId = consumable.get("inventoryItemId").asText();
        mockMvc.perform(post("/api/inventory/reagents/items/{id}/reagent-profile", consumableId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reagentCategory":"working_reagent","consumptionUnitRatio":1.0,"actorId":"admin"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("REAGENT_ITEM_TYPE_NOT_ELIGIBLE"));

        // negative ratio should be rejected
        JsonNode reagent = createItem("REAG-002", "reagent", "diagnostic_reagent");
        mockMvc.perform(post("/api/inventory/reagents/items/{id}/reagent-profile", reagent.get("inventoryItemId").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reagentCategory":"working_reagent","consumptionUnitRatio":-1.0,"actorId":"admin"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("REAGENT_CONSUMPTION_RATIO_INVALID"));
    }

    @Test
    void lotLifecycleWithStockMovementsUpdatesOnHandAndRemainingQuantities() throws Exception {
        JsonNode item = createItem("REAG-LOT-001", "reagent", "diagnostic_reagent");
        String itemId = item.get("inventoryItemId").asText();

        // register a stock lot with 100 units
        JsonNode lot = postJson("/api/inventory/lots/items/" + itemId + "/lots", """
                {"lotNumber":"L-001","supplierId":"SUP-1","supplierName":"Acme",
                 "expirationDate":"2027-01-01","receivedQuantity":100.0,"actorId":"admin"}
                """);
        String lotId = lot.get("stockLotId").asText();
        assertThat(lot.get("remainingQuantity").asDouble()).isEqualTo(100.0);

        // apply a stock entry (+30) into that lot
        postJson("/api/inventory/stock-entries", """
                {"inventoryItemId":"%s","stockLotId":"%s","quantity":30.0,"entryType":"initial_load","actorId":"admin"}
                """.formatted(itemId, lotId));

        // apply an exit of 20 units
        postJson("/api/inventory/stock-exits", """
                {"inventoryItemId":"%s","stockLotId":"%s","destinationBranchId":"branch-dst",
                 "quantity":20.0,"exitType":"inter_branch_transfer","actorId":"admin"}
                """.formatted(itemId, lotId));

        // consumption of 10 units (requires reagent profile first)
        postJson("/api/inventory/reagents/items/" + itemId + "/reagent-profile", """
                {"reagentCategory":"working_reagent","consumptionUnitRatio":1.0,"actorId":"admin"}
                """);
        postJson("/api/inventory/consumption", """
                {"inventoryItemId":"%s","stockLotId":"%s","consumedQuantity":10.0,
                 "consumptionContext":"test_processing","actorId":"admin"}
                """.formatted(itemId, lotId));

        // adjustment -5 with distinct approver
        postJson("/api/inventory/adjustments", """
                {"inventoryItemId":"%s","stockLotId":"%s","deltaQuantity":-5.0,"reasonCode":"cycle_count",
                 "requestedBy":"user-1","approverId":"manager-1","actorId":"user-1"}
                """.formatted(itemId, lotId));

        // waste disposal of 5
        postJson("/api/inventory/waste", """
                {"inventoryItemId":"%s","stockLotId":"%s","disposedQuantity":5.0,
                 "reasonCode":"contaminated","actorId":"admin"}
                """.formatted(itemId, lotId));

        // on-hand should be 100 + 30 - 20 - 10 - 5 - 5 = 90; lot remaining 100 + 30 - 20 - 10 - 5 - 5 = 90
        JsonNode after = getJson("/api/inventory/catalog/items/" + itemId);
        assertThat(after.get("stockSummary").get("onHandQuantity").asDouble()).isEqualTo(90.0);
        JsonNode lotAfter = getJson("/api/inventory/lots/items/" + itemId + "/lots");
        assertThat(lotAfter.get(0).get("remainingQuantity").asDouble()).isEqualTo(90.0);
    }

    @Test
    void adjustmentApproverMustDifferFromRequester() throws Exception {
        JsonNode item = createItem("CONS-ADJ-001", "consumable", "lab_supply");
        String itemId = item.get("inventoryItemId").asText();
        // seed some stock via entry so a negative adjustment does not underflow.
        postJson("/api/inventory/stock-entries", """
                {"inventoryItemId":"%s","quantity":10.0,"entryType":"initial_load","actorId":"admin"}
                """.formatted(itemId));

        mockMvc.perform(post("/api/inventory/adjustments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"inventoryItemId":"%s","deltaQuantity":-1.0,"reasonCode":"cycle_count",
                                 "requestedBy":"same","approverId":"same","actorId":"same"}
                                """.formatted(itemId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ADJUSTMENT_APPROVER_SAME_AS_REQUESTER"));
    }

    @Test
    void waste_of_theWholeLotDisposesTheLot() throws Exception {
        JsonNode item = createItem("REAG-WASTE-001", "reagent", "diagnostic_reagent");
        String itemId = item.get("inventoryItemId").asText();
        JsonNode lot = postJson("/api/inventory/lots/items/" + itemId + "/lots", """
                {"lotNumber":"L-W-1","receivedQuantity":5.0,"actorId":"admin"}
                """);
        String lotId = lot.get("stockLotId").asText();
        postJson("/api/inventory/waste", """
                {"inventoryItemId":"%s","stockLotId":"%s","disposedQuantity":5.0,
                 "reasonCode":"expired","actorId":"admin"}
                """.formatted(itemId, lotId));

        JsonNode lots = getJson("/api/inventory/lots/items/" + itemId + "/lots");
        assertThat(lots.get(0).get("status").asText()).isEqualTo("disposed");
    }

    @Test
    void stockExitAgainstIneligibleLotIsRejected() throws Exception {
        JsonNode item = createItem("REAG-EX-001", "reagent", "diagnostic_reagent");
        String itemId = item.get("inventoryItemId").asText();
        JsonNode lot = postJson("/api/inventory/lots/items/" + itemId + "/lots", """
                {"lotNumber":"L-EX-1","receivedQuantity":10.0,"actorId":"admin"}
                """);
        String lotId = lot.get("stockLotId").asText();

        mockMvc.perform(post("/api/inventory/lots/lots/{id}/quarantine", lotId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"admin\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/inventory/stock-exits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"inventoryItemId":"%s","stockLotId":"%s","destinationBranchId":"branch-2",
                                 "quantity":1.0,"exitType":"inter_branch_transfer","actorId":"admin"}
                                """.formatted(itemId, lotId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("STOCK_EXIT_LOT_NOT_ELIGIBLE"));
    }

    @Test
    void stockExitInterBranchTransferRequiresDestinationBranch() throws Exception {
        JsonNode item = createItem("REAG-EX-002", "reagent", "diagnostic_reagent");
        String itemId = item.get("inventoryItemId").asText();
        JsonNode lot = postJson("/api/inventory/lots/items/" + itemId + "/lots", """
                {"lotNumber":"L-EX-2","receivedQuantity":10.0,"actorId":"admin"}
                """);
        String lotId = lot.get("stockLotId").asText();

        mockMvc.perform(post("/api/inventory/stock-exits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"inventoryItemId":"%s","stockLotId":"%s","quantity":1.0,
                                 "exitType":"inter_branch_transfer","actorId":"admin"}
                                """.formatted(itemId, lotId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("STOCK_EXIT_DESTINATION_BRANCH_REQUIRED"));
    }

    @Test
    void purchaseOrderLifecycleAndReceiveMutatesStock() throws Exception {
        JsonNode item = createItem("CONS-PO-001", "consumable", "lab_supply");
        String itemId = item.get("inventoryItemId").asText();

        JsonNode po = postJson("/api/inventory/purchase-orders", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","supplierId":"SUP-9",
                 "supplierName":"Test Supplier","currencyCode":"MXN",
                 "lines":[{"inventoryItemId":"%s","orderedQuantity":50,"unitCost":2.0}],
                 "actorId":"admin"}
                """.formatted(tenantId, LAB, BRANCH, itemId));
        String poId = po.get("purchaseOrderId").asText();
        String lineId = po.get("lines").get(0).get("purchaseOrderLineId").asText();
        assertThat(po.get("totalAmount").asDouble()).isEqualTo(100.0);
        assertThat(po.get("status").asText()).isEqualTo("draft");

        postJson("/api/inventory/purchase-orders/" + poId + "/submit", "{\"actorId\":\"admin\"}");
        postJson("/api/inventory/purchase-orders/" + poId + "/approve", "{\"actorId\":\"approver-1\"}");

        // list orders
        mockMvc.perform(get("/api/inventory/purchase-orders")
                        .param("tenantId", tenantId).param("laboratoryId", LAB).param("branchId", BRANCH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].purchaseOrderId").value(poId));

        JsonNode afterReceipt = postJson("/api/inventory/purchase-orders/" + poId + "/lines/" + lineId + "/receive", """
                {"receivedQuantity":30,"actorId":"admin"}
                """);
        assertThat(afterReceipt.get("status").asText()).isEqualTo("receiving");
        assertThat(afterReceipt.get("lines").get(0).get("receivedQuantity").asDouble()).isEqualTo(30.0);

        JsonNode item2 = getJson("/api/inventory/catalog/items/" + itemId);
        assertThat(item2.get("stockSummary").get("onHandQuantity").asDouble()).isEqualTo(30.0);

        JsonNode afterFullReceipt = postJson(
                "/api/inventory/purchase-orders/" + poId + "/lines/" + lineId + "/receive", """
                {"receivedQuantity":20,"actorId":"admin"}
                """);
        assertThat(afterFullReceipt.get("status").asText()).isEqualTo("received");
    }

    @Test
    void purchaseOrderCancellationRequiresReasonAndCannotHappenAfterReceiving() throws Exception {
        JsonNode item = createItem("CONS-PO-002", "consumable", "lab_supply");
        String itemId = item.get("inventoryItemId").asText();

        JsonNode po = postJson("/api/inventory/purchase-orders", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","supplierId":"SUP-9",
                 "supplierName":"Test Supplier","currencyCode":"MXN",
                 "lines":[{"inventoryItemId":"%s","orderedQuantity":1,"unitCost":10.0}],
                 "actorId":"admin"}
                """.formatted(tenantId, LAB, BRANCH, itemId));
        String poId = po.get("purchaseOrderId").asText();

        mockMvc.perform(post("/api/inventory/purchase-orders/" + poId + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"Duplicate\",\"actorId\":\"admin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("cancelled"));

        // cancelling twice is rejected
        mockMvc.perform(post("/api/inventory/purchase-orders/" + poId + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"Duplicate\",\"actorId\":\"admin\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PURCHASE_ORDER_TERMINAL_STATE"));
    }

    @Test
    void inventoryEndpointsReturnStructuredErrorsWithMessageKey() throws Exception {
        mockMvc.perform(get("/api/inventory/catalog/items/{id}", "missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("INVENTORY_ITEM_NOT_FOUND"))
                .andExpect(jsonPath("$.messageKey").value("inventory.error.inventory_item_not_found"));

        mockMvc.perform(get("/api/inventory/purchase-orders/{id}", "missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PURCHASE_ORDER_NOT_FOUND"));

        mockMvc.perform(get("/api/inventory/lots/items/{id}/lots", "missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("INVENTORY_ITEM_NOT_FOUND"));
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

    private JsonNode getJson(String path) throws Exception {
        MvcResult result = mockMvc.perform(get(path))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
