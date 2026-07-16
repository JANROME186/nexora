package com.nexora.hop.platformfoundation.cashsales;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
class CashSalesApiTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tenantId;
    private String laboratoryId;
    private String branchId;

    @BeforeEach
    void createOrganizationScope() throws Exception {
        String runToken = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Cash Sales Tenant " + runToken + "\"}");
        tenantId = tenant.get("tenantId").asText();
        JsonNode laboratory = postJson("/api/organization/laboratories",
                "{\"tenantId\":\"%s\",\"name\":\"Cash Sales Lab\"}".formatted(tenantId));
        laboratoryId = laboratory.get("laboratoryId").asText();
        JsonNode branch = postJson("/api/organization/branches",
                "{\"laboratoryId\":\"%s\",\"name\":\"Cash Sales Branch\"}".formatted(laboratoryId));
        branchId = branch.get("branchId").asText();
    }

    @Test
    void cashierCanCreateSaleFromAcceptedOrderAndRegisterCashPayment() throws Exception {
        String orderId = createAcceptedPricedOrder("64.50");

        JsonNode sale = postJson("/api/revenue/cashier/sales", """
                {"tenantId":"%s","sourceType":"diagnostic_order","sourceReferenceId":"%s","actorId":"cashier-1"}
                """.formatted(tenantId, orderId));
        String saleId = sale.get("saleId").asText();
        assertThat(sale.get("status").asText()).isEqualTo("payable");
        assertThat(sale.get("totals").get("outstandingAmount").get("amount").asDouble()).isEqualTo(64.50);

        JsonNode session = postJson("/api/revenue/cashier/sessions", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","openedBy":"cashier-1",
                 "openingAmount":10.00,"currency":"USD"}
                """.formatted(tenantId, laboratoryId, branchId));
        String sessionId = session.get("sessionId").asText();

        mockMvc.perform(post("/api/revenue/cashier/sales/{saleId}/payments", saleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":64.50,"currency":"USD","method":"cash","sessionId":"%s",
                                 "registeredBy":"cashier-1"}
                                """.formatted(sessionId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.method").value("cash"));

        mockMvc.perform(get("/api/revenue/cashier/sales/{saleId}", saleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("paid"))
                .andExpect(jsonPath("$.totals.outstandingAmount.amount").value(0.0));
    }

    @Test
    void cashierRejectsOverpaymentAndRequiresVarianceReasonOnClose() throws Exception {
        String orderId = createAcceptedPricedOrder("25.00");
        String saleId = postJson("/api/revenue/cashier/sales", """
                {"tenantId":"%s","sourceType":"diagnostic_order","sourceReferenceId":"%s","actorId":"cashier-1"}
                """.formatted(tenantId, orderId)).get("saleId").asText();

        mockMvc.perform(post("/api/revenue/cashier/sales/{saleId}/payments", saleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":26.00,\"currency\":\"USD\",\"method\":\"card\",\"registeredBy\":\"cashier-1\"}"))
                .andExpect(status().isConflict());

        String sessionId = postJson("/api/revenue/cashier/sessions", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","openedBy":"cashier-1",
                 "openingAmount":5.00,"currency":"USD"}
                """.formatted(tenantId, laboratoryId, branchId)).get("sessionId").asText();

        mockMvc.perform(post("/api/revenue/cashier/sessions/{sessionId}/close", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"countedAmount\":6.00,\"currency\":\"USD\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void partialPaymentKeepsSalePayableForRemainingBalance() throws Exception {
        String orderId = createAcceptedPricedOrder("50.00");
        String saleId = postJson("/api/revenue/cashier/sales", """
                {"tenantId":"%s","sourceType":"diagnostic_order","sourceReferenceId":"%s","actorId":"cashier-1"}
                """.formatted(tenantId, orderId)).get("saleId").asText();

        postJson("/api/revenue/cashier/sales/" + saleId + "/payments",
                "{\"amount\":20.00,\"currency\":\"USD\",\"method\":\"card\",\"registeredBy\":\"cashier-1\"}");

        mockMvc.perform(get("/api/revenue/cashier/sales/{saleId}", saleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("partially_paid"))
                .andExpect(jsonPath("$.totals.paidAmount.amount").value(20.0))
                .andExpect(jsonPath("$.totals.outstandingAmount.amount").value(30.0));
    }

    @Test
    void paidSaleCanCreateBillingRequestButAdapterActionsRemainExplicitBoundary() throws Exception {
        String orderId = createAcceptedPricedOrder("40.00");
        String saleId = postJson("/api/revenue/cashier/sales", """
                {"tenantId":"%s","sourceType":"diagnostic_order","sourceReferenceId":"%s","actorId":"cashier-1"}
                """.formatted(tenantId, orderId)).get("saleId").asText();
        postJson("/api/revenue/cashier/sales/" + saleId + "/payments",
                "{\"amount\":40.00,\"currency\":\"USD\",\"method\":\"card\",\"registeredBy\":\"cashier-1\"}");

        JsonNode invoice = postJson("/api/revenue/billing-requests", """
                {"saleId":"%s","legalName":"Ada Lovelace","taxIdentifier":"TAX-123",
                 "fiscalAddress":"Main Street 1","fiscalRegime":"general","taxCode":"VAT","taxRate":16.00,
                 "actorId":"cashier-1"}
                """.formatted(saleId));
        String invoiceRequestId = invoice.get("invoiceRequestId").asText();
        assertThat(invoice.get("status").asText()).isEqualTo("requested");

        mockMvc.perform(get("/api/revenue/billing-requests/{invoiceRequestId}/tax-lines", invoiceRequestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taxCode").value("VAT"));

        mockMvc.perform(post("/api/revenue/billing-requests/{invoiceRequestId}/submit", invoiceRequestId))
                .andExpect(status().isConflict());
    }

    @Test
    void cashierCanListSaleLinesPaymentsSessionsAndCloseBalancedSession() throws Exception {
        String orderId = createAcceptedPricedOrder("30.00");
        String saleId = postJson("/api/revenue/cashier/sales", """
                {"tenantId":"%s","sourceType":"diagnostic_order","sourceReferenceId":"%s","actorId":"cashier-1"}
                """.formatted(tenantId, orderId)).get("saleId").asText();
        String sessionId = postJson("/api/revenue/cashier/sessions", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","openedBy":"cashier-1",
                 "openingAmount":10.00,"currency":"USD"}
                """.formatted(tenantId, laboratoryId, branchId)).get("sessionId").asText();

        mockMvc.perform(get("/api/revenue/cashier/sessions/{sessionId}", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("open"));
        mockMvc.perform(post("/api/revenue/cashier/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","openedBy":"cashier-2",
                                 "openingAmount":1.00,"currency":"USD"}
                                """.formatted(tenantId, laboratoryId, branchId)))
                .andExpect(status().isConflict());

        postJson("/api/revenue/cashier/sales/" + saleId + "/payments", """
                {"amount":30.00,"currency":"USD","method":"cash","sessionId":"%s","registeredBy":"cashier-1"}
                """.formatted(sessionId));

        mockMvc.perform(get("/api/revenue/cashier/sales").param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].saleId").exists());
        mockMvc.perform(get("/api/revenue/cashier/sales/{saleId}/lines", saleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].catalogItemKind").value("panel"));
        mockMvc.perform(get("/api/revenue/cashier/sales/{saleId}/payments", saleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].method").value("cash"));
        mockMvc.perform(get("/api/revenue/cashier/sessions").param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sessionId").value(sessionId));

        mockMvc.perform(post("/api/revenue/cashier/sessions/{sessionId}/close", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"countedAmount\":40.00,\"currency\":\"USD\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("closed"));
    }

    @Test
    void saleCanBeCreatedFromAcceptedQuotationAndCancelledBeforePayment() throws Exception {
        String quotationId = createAcceptedQuotation("22.00");

        JsonNode sale = postJson("/api/revenue/cashier/sales", """
                {"tenantId":"%s","sourceType":"quotation","sourceReferenceId":"%s","actorId":"cashier-1"}
                """.formatted(tenantId, quotationId));
        String saleId = sale.get("saleId").asText();
        assertThat(sale.get("sourceType").asText()).isEqualTo("quotation");

        mockMvc.perform(post("/api/revenue/cashier/sales/{saleId}/cancel", saleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reasonCode\":\"customer_declined\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("cancelled"));

        mockMvc.perform(post("/api/revenue/cashier/sales/{saleId}/payments", saleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":1.00,\"currency\":\"USD\",\"method\":\"card\",\"registeredBy\":\"cashier-1\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void salesAndBillingRejectInvalidLifecycleTransitions() throws Exception {
        String orderId = createDraftPricedOrder("18.00");
        mockMvc.perform(post("/api/revenue/cashier/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","sourceType":"diagnostic_order","sourceReferenceId":"%s"}
                                """.formatted(tenantId, orderId)))
                .andExpect(status().isConflict());

        String acceptedOrderId = createAcceptedPricedOrder("18.00");
        String saleId = postJson("/api/revenue/cashier/sales", """
                {"tenantId":"%s","sourceType":"diagnostic_order","sourceReferenceId":"%s"}
                """.formatted(tenantId, acceptedOrderId)).get("saleId").asText();

        mockMvc.perform(post("/api/revenue/billing-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"saleId":"%s","legalName":"Ada Lovelace","taxIdentifier":"TAX-123",
                                 "fiscalAddress":"Main Street 1"}
                                """.formatted(saleId)))
                .andExpect(status().isConflict());
    }

    @Test
    void billingRequestListGetRetryAndCancelUseProviderAgnosticBoundary() throws Exception {
        String orderId = createAcceptedPricedOrder("41.00");
        String saleId = postJson("/api/revenue/cashier/sales", """
                {"tenantId":"%s","sourceType":"diagnostic_order","sourceReferenceId":"%s"}
                """.formatted(tenantId, orderId)).get("saleId").asText();
        postJson("/api/revenue/cashier/sales/" + saleId + "/payments",
                "{\"amount\":41.00,\"currency\":\"USD\",\"method\":\"transfer\",\"registeredBy\":\"cashier-1\"}");
        String invoiceRequestId = postJson("/api/revenue/billing-requests", """
                {"saleId":"%s","legalName":"Ada Lovelace","taxIdentifier":"TAX-123",
                 "fiscalAddress":"Main Street 1"}
                """.formatted(saleId)).get("invoiceRequestId").asText();

        mockMvc.perform(get("/api/revenue/billing-requests").param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].invoiceRequestId").value(invoiceRequestId));
        mockMvc.perform(get("/api/revenue/billing-requests/{invoiceRequestId}", invoiceRequestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saleId").value(saleId));
        mockMvc.perform(post("/api/revenue/billing-requests/{invoiceRequestId}/retry", invoiceRequestId))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/revenue/billing-requests/{invoiceRequestId}/cancel", invoiceRequestId))
                .andExpect(status().isConflict());
    }

    @Test
    void cashierAndBillingEndpointsReturnConsistentProblemDetails() throws Exception {
        mockMvc.perform(post("/api/revenue/cashier/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","sourceType":"unsupported","sourceReferenceId":"source-1"}
                                """.formatted(tenantId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid cash sales command"));

        mockMvc.perform(get("/api/revenue/cashier/sales/{saleId}", "missing-sale"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Cash sales entity not found"));

        mockMvc.perform(get("/api/revenue/billing-requests/{invoiceRequestId}", "missing-invoice-request"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Cash sales entity not found"));
    }

    private String createAcceptedPricedOrder(String amount) throws Exception {
        String orderId = createDraftPricedOrder(amount);
        mockMvc.perform(post("/api/clinical-operations/diagnostic-orders/{orderId}/accept", orderId)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        return orderId;
    }

    private String createDraftPricedOrder(String amount) throws Exception {
        String patientId = registerPatient();
        String panelId = publishPanel();
        publishPriceListForPanel(panelId, amount);
        JsonNode order = postJson("/api/clinical-operations/diagnostic-orders", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                 "patientId":"%s","actorId":"receptionist-1",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, patientId, panelId));
        String orderId = order.get("orderId").asText();
        postJson("/api/clinical-operations/diagnostic-orders/" + orderId + "/price", "{}");
        return orderId;
    }

    private String createAcceptedQuotation(String amount) throws Exception {
        String patientId = registerPatient();
        String panelId = publishPanel();
        publishPriceListForPanel(panelId, amount);
        JsonNode quotation = postJson("/api/care-delivery/quotations", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, patientId, panelId));
        String quotationId = quotation.get("quotationId").asText();
        postJson("/api/care-delivery/quotations/" + quotationId + "/issue", "{}");
        mockMvc.perform(post("/api/care-delivery/quotations/{quotationId}/accept", quotationId))
                .andExpect(status().isOk());
        return quotationId;
    }

    private String registerPatient() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode patient = postJson("/api/people/patients", """
                {"tenantId":"%s","laboratoryId":"%s","patientCode":"CASH-%s",
                 "givenName":"Ada","familyName":"Lovelace","birthDate":"%s","sexAtBirth":"female",
                 "primaryDocumentType":"national_id","primaryDocumentNumber":"DOC-%s"}
                """.formatted(tenantId, laboratoryId, token, LocalDate.of(1980, 1, 1), token));
        return patient.get("patientId").asText();
    }

    private String publishPanel() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode panel = postJson("/api/catalog/panels", """
                {"tenantId":"%s","laboratoryId":"%s","code":"CASH-PNL-%s",
                 "nameEn":"Cash Panel","nameEs":"Panel Caja",
                 "members":[{"testRefId":"test-1","mandatory":true},{"testRefId":"test-2","mandatory":false}]}
                """.formatted(tenantId, laboratoryId, token));
        String panelId = panel.get("panelId").asText();
        mockMvc.perform(post("/api/catalog/panels/{panelId}/publish", panelId)).andExpect(status().isOk());
        return panelId;
    }

    private void publishPriceListForPanel(String panelId, String amount) throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode priceList = postJson("/api/catalog/price-lists", """
                {"tenantId":"%s","laboratoryId":"%s","code":"CASH-PRC-%s",
                 "nameEn":"Cash Standard","nameEs":"Caja Estandar","currency":"USD",
                 "agreementRefId":"CASH-AGR-%s","effectiveFrom":"2026-01-01"}
                """.formatted(tenantId, laboratoryId, token, token));
        String priceListId = priceList.get("priceListId").asText();
        mockMvc.perform(post("/api/catalog/price-lists/{priceListId}/entries", priceListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemType\":\"panel\",\"itemRefId\":\"%s\",\"amount\":%s}"
                                .formatted(panelId, amount)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/catalog/price-lists/{priceListId}/publish", priceListId))
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
}
