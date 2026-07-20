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
 * PostgreSQL-backed integration test for the inventory-and-internal-quality module. Executed only
 * when {@code -Dhop.local-db-tests=true} is set (typically together with a running
 * {@code compose.local.yml}). Mirrors {@code IntegrationInteroperabilityLocalDatabaseTest} so the
 * JDBC adapters (BCM-INV-001..009) get exercised end-to-end against a real PostgreSQL database.
 */
@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class InventoryQualityLocalDatabaseTest {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void inventoryQualitySchemaIsInitializedInPostgres() {
        Integer tableCount = jdbcTemplate.queryForObject("""
                select count(*)
                  from information_schema.tables
                 where table_schema = 'inventory_quality'
                   and table_name in (
                       'inventory_items', 'stock_lots', 'purchase_orders', 'purchase_order_lines',
                       'stock_entries', 'stock_exits', 'consumption_records',
                       'inventory_adjustments', 'waste_records')
                """, Integer.class);
        assertThat(tableCount).isEqualTo(9);
    }

    @Test
    void inventoryModuleEndToEndRoundTripAgainstRealPostgres() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"JDBC Inventory Tenant " + token + "\"}");
        String tenantId = tenant.get("tenantId").asText();
        String lab = "lab-jdbc-inv-" + token;
        String branch = "branch-jdbc-inv-" + token;

        // 1. product catalog
        JsonNode item = postJson("/api/inventory/catalog/items", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","itemCode":"REAG-JDBC-%s",
                 "itemName":"JDBC Reagent","itemType":"reagent","classification":"diagnostic_reagent",
                 "unitOfMeasure":"ml","actorId":"admin"}
                """.formatted(tenantId, lab, branch, token));
        String itemId = item.get("inventoryItemId").asText();

        // list
        mockMvc.perform(get("/api/inventory/catalog/items")
                        .param("tenantId", tenantId).param("laboratoryId", lab).param("branchId", branch))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].inventoryItemId").value(itemId));

        // 2. reagent profile
        postJson("/api/inventory/reagents/items/" + itemId + "/reagent-profile", """
                {"reagentCategory":"working_reagent","consumptionUnitRatio":1.5,"actorId":"admin"}
                """);
        mockMvc.perform(get("/api/inventory/reagents/items/{id}/reagent-profile", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reagentCategory").value("working_reagent"));

        // 3. lot management
        JsonNode lot = postJson("/api/inventory/lots/items/" + itemId + "/lots", """
                {"lotNumber":"L-JDBC-1","supplierId":"SUP-1","supplierName":"JDBC Supplier",
                 "receivedQuantity":100.0,"actorId":"admin"}
                """);
        String lotId = lot.get("stockLotId").asText();
        mockMvc.perform(get("/api/inventory/lots/items/{id}/lots", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockLotId").value(lotId));

        // 4. purchase order lifecycle
        JsonNode po = postJson("/api/inventory/purchase-orders", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","supplierId":"SUP-1",
                 "supplierName":"JDBC Supplier","currencyCode":"MXN",
                 "lines":[{"inventoryItemId":"%s","orderedQuantity":20,"unitCost":5.0}],
                 "actorId":"admin"}
                """.formatted(tenantId, lab, branch, itemId));
        String poId = po.get("purchaseOrderId").asText();
        String lineId = po.get("lines").get(0).get("purchaseOrderLineId").asText();
        postJson("/api/inventory/purchase-orders/" + poId + "/submit", "{\"actorId\":\"admin\"}");
        postJson("/api/inventory/purchase-orders/" + poId + "/approve", "{\"actorId\":\"approver-1\"}");
        postJson("/api/inventory/purchase-orders/" + poId + "/lines/" + lineId + "/receive",
                "{\"receivedQuantity\":20,\"actorId\":\"admin\"}");
        mockMvc.perform(get("/api/inventory/purchase-orders/{id}", poId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("received"));

        // 5. exit -10
        postJson("/api/inventory/stock-exits", """
                {"inventoryItemId":"%s","stockLotId":"%s","destinationBranchId":"branch-2",
                 "quantity":10.0,"exitType":"inter_branch_transfer","actorId":"admin"}
                """.formatted(itemId, lotId));

        // 6. consumption -10
        postJson("/api/inventory/consumption", """
                {"inventoryItemId":"%s","stockLotId":"%s","consumedQuantity":10.0,
                 "consumptionContext":"test_processing","actorId":"admin"}
                """.formatted(itemId, lotId));

        // 7. adjustment +5 with distinct approver
        postJson("/api/inventory/adjustments", """
                {"inventoryItemId":"%s","stockLotId":"%s","deltaQuantity":5.0,
                 "reasonCode":"cycle_count","requestedBy":"user-1","approverId":"manager-1","actorId":"user-1"}
                """.formatted(itemId, lotId));

        // 8. waste -5
        postJson("/api/inventory/waste", """
                {"inventoryItemId":"%s","stockLotId":"%s","disposedQuantity":5.0,
                 "reasonCode":"expired","actorId":"admin"}
                """.formatted(itemId, lotId));

        // final on-hand: 100 (lot) + 20 (PO receive) - 10 - 10 + 5 - 5 = 100
        JsonNode final_ = getJson("/api/inventory/catalog/items/" + itemId);
        assertThat(final_.get("stockSummary").get("onHandQuantity").asDouble()).isEqualTo(100.0);

        // discontinue and confirm the update path is JDBC-persisted
        mockMvc.perform(post("/api/inventory/catalog/items/{id}/discontinue", itemId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"admin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("discontinued"));

        // lot quarantine and expire
        JsonNode item2 = postJson("/api/inventory/catalog/items", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","itemCode":"REAG-JDBC-2-%s",
                 "itemName":"JDBC Reagent 2","itemType":"reagent","classification":"diagnostic_reagent",
                 "unitOfMeasure":"ml","actorId":"admin"}
                """.formatted(tenantId, lab, branch, token));
        String item2Id = item2.get("inventoryItemId").asText();
        JsonNode lot2 = postJson("/api/inventory/lots/items/" + item2Id + "/lots", """
                {"lotNumber":"L-JDBC-2","receivedQuantity":50.0,"actorId":"admin"}
                """);
        String lot2Id = lot2.get("stockLotId").asText();
        postJson("/api/inventory/lots/lots/" + lot2Id + "/quarantine", "{\"actorId\":\"admin\"}");
        mockMvc.perform(post("/api/inventory/lots/lots/{id}/expire", lot2Id)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"admin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("expired"));

        // list endpoints exercise the JDBC scope-scoped queries
        mockMvc.perform(get("/api/inventory/stock-entries")
                        .param("tenantId", tenantId).param("laboratoryId", lab).param("branchId", branch))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/inventory/stock-exits")
                        .param("tenantId", tenantId).param("laboratoryId", lab).param("branchId", branch))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/inventory/consumption")
                        .param("tenantId", tenantId).param("laboratoryId", lab).param("branchId", branch))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/inventory/adjustments")
                        .param("tenantId", tenantId).param("laboratoryId", lab).param("branchId", branch))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/inventory/waste")
                        .param("tenantId", tenantId).param("laboratoryId", lab).param("branchId", branch))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/inventory/purchase-orders")
                        .param("tenantId", tenantId).param("laboratoryId", lab).param("branchId", branch))
                .andExpect(status().isOk());
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
