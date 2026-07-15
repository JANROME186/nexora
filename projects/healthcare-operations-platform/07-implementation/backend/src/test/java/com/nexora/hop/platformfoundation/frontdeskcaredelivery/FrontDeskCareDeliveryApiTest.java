package com.nexora.hop.platformfoundation.frontdeskcaredelivery;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Functional coverage for the compiled MVP-MOD-004-BE-001 outputs across the five Front Desk and
 * Care Delivery capabilities: BCM-LAB-001 (order create/price/accept/complete), BCM-ATT-001
 * (appointment request/confirm/check-in/overlap), BCM-ATT-003/BCM-ATT-004 (reception to admission
 * to order commit) and BCM-ATT-006 (quotation issue/accept/convert). Every endpoint responds
 * without a 501; the baseline pricing/discount/overlap logic implemented here is intentionally
 * simple and documented as a BE-002 refinement target on each service class.
 */
@AutoConfigureMockMvc
@SpringBootTest
class FrontDeskCareDeliveryApiTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tenantId;
    private String laboratoryId;
    private String branchId;

    @BeforeEach
    void createOrganizationScope() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Front Desk Tenant\"}");
        tenantId = tenant.get("tenantId").asText();
        JsonNode laboratory = postJson("/api/organization/laboratories",
                "{\"tenantId\":\"%s\",\"name\":\"Front Desk Lab\"}".formatted(tenantId));
        laboratoryId = laboratory.get("laboratoryId").asText();
        JsonNode branch = postJson("/api/organization/branches",
                "{\"laboratoryId\":\"%s\",\"name\":\"Front Desk Branch\"}".formatted(laboratoryId));
        branchId = branch.get("branchId").asText();
    }

    @Test
    void diagnosticOrderCanBeCreatedPricedAcceptedAndCompletedWithDoctorSnapshot() throws Exception {
        String patientId = registerPatient("Ada", "Lovelace");
        String doctorId = registerDoctor("Grace", "Hopper");
        String panelId = publishPanel();
        publishPriceListForPanel(panelId, "45.00");

        JsonNode order = postJson("/api/clinical-operations/diagnostic-orders", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                 "patientId":"%s","doctorId":"%s","actorId":"receptionist-1",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, patientId, doctorId, panelId));
        String orderId = order.get("orderId").asText();
        assertThat(order.get("status").asText()).isEqualTo("draft");
        assertThat(order.get("patientSnapshot").get("fullName").asText()).isEqualTo("Ada Lovelace");
        assertThat(order.get("doctorSnapshot").get("fullName").asText()).isEqualTo("Grace Hopper");
        assertThat(order.get("branchSnapshot").get("name").asText()).isEqualTo("Front Desk Branch");

        JsonNode priced = postJson("/api/clinical-operations/diagnostic-orders/" + orderId + "/price", "{}");
        assertThat(priced.get("status").asText()).isEqualTo("priced");
        assertThat(priced.get("pricingSnapshot").get("totalAmount").get("amount").asDouble()).isEqualTo(45.00);

        mockMvc.perform(post("/api/clinical-operations/diagnostic-orders/{id}/accept", orderId)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("accepted"));

        mockMvc.perform(post("/api/clinical-operations/diagnostic-orders/{id}/complete", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("completed"));

        mockMvc.perform(post("/api/clinical-operations/diagnostic-orders/{id}/cancel", orderId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reasonCode\":\"test\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/clinical-operations/diagnostic-orders").param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").exists());
    }

    @Test
    void diagnosticOrderRejectsUnpublishedCatalogItem() throws Exception {
        String patientId = registerPatient("John", "Doe");
        JsonNode draftPanel = postJson("/api/catalog/panels", """
                {"tenantId":"%s","laboratoryId":"%s","code":"PNL-DRAFT","nameEn":"Draft Panel","nameEs":"Panel Borrador",
                 "members":[{"testRefId":"test-1","mandatory":true},{"testRefId":"test-2","mandatory":false}]}
                """.formatted(tenantId, laboratoryId));
        String draftPanelId = draftPanel.get("panelId").asText();

        mockMvc.perform(post("/api/clinical-operations/diagnostic-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                                 "patientId":"%s",
                                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                                """.formatted(tenantId, laboratoryId, branchId, patientId, draftPanelId)))
                .andExpect(status().isConflict());
    }

    @Test
    void appointmentCanBeRequestedConfirmedAndCheckedIn() throws Exception {
        String patientId = registerPatient("Marie", "Curie");
        LocalDate start = LocalDate.now().plusDays(1);

        JsonNode appointment = postJson("/api/care-delivery/appointments", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s",
                 "scheduledStart":"%s","scheduledEnd":"%s","channel":"employee_portal","actorId":"receptionist-1"}
                """.formatted(tenantId, laboratoryId, branchId, patientId, start, start));
        String appointmentId = appointment.get("appointmentId").asText();
        assertThat(appointment.get("status").asText()).isEqualTo("requested");

        mockMvc.perform(post("/api/care-delivery/appointments/{id}/confirm", appointmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("confirmed"));

        mockMvc.perform(post("/api/care-delivery/appointments/{id}/check-in", appointmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("checked_in"));
    }

    @Test
    void appointmentConfirmRejectsOverlapForSamePatientAndBranch() throws Exception {
        String patientId = registerPatient("Rosalind", "Franklin");
        LocalDate start = LocalDate.now().plusDays(2);

        JsonNode first = postJson("/api/care-delivery/appointments", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s",
                 "scheduledStart":"%s","scheduledEnd":"%s","channel":"phone"}
                """.formatted(tenantId, laboratoryId, branchId, patientId, start, start));
        mockMvc.perform(post("/api/care-delivery/appointments/{id}/confirm", first.get("appointmentId").asText()))
                .andExpect(status().isOk());

        JsonNode second = postJson("/api/care-delivery/appointments", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s",
                 "scheduledStart":"%s","scheduledEnd":"%s","channel":"phone"}
                """.formatted(tenantId, laboratoryId, branchId, patientId, start, start));
        mockMvc.perform(post("/api/care-delivery/appointments/{id}/confirm", second.get("appointmentId").asText()))
                .andExpect(status().isConflict());
    }

    @Test
    void receptionAndAdmissionFlowCommitsADiagnosticOrder() throws Exception {
        String patientId = registerPatient("Katherine", "Johnson");
        String panelId = publishPanel();
        publishPriceListForPanel(panelId, "60.00");

        JsonNode visit = postJson("/api/care-delivery/reception-visits", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s","intakeChannel":"walk_in"}
                """.formatted(tenantId, laboratoryId, branchId, patientId));
        String visitId = visit.get("visitId").asText();

        mockMvc.perform(post("/api/care-delivery/reception-visits/{id}/advance-to-admission", visitId))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/care-delivery/reception-visits/{id}/confirm-identity", visitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identityConfirmationMethod\":\"document_check\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identityConfirmed").value(true));

        mockMvc.perform(post("/api/care-delivery/reception-visits/{id}/advance-to-admission", visitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queueStatus").value("in_admission"));

        JsonNode admission = postJson("/api/care-delivery/admission-requests", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","visitId":"%s","patientId":"%s"}
                """.formatted(tenantId, laboratoryId, branchId, visitId, patientId));
        String admissionId = admission.get("admissionId").asText();

        mockMvc.perform(post("/api/care-delivery/admission-requests/{id}/mark-ready", admissionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"catalogSelection":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                                """.formatted(panelId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.admissionStatus").value("ready_for_order"));

        mockMvc.perform(post("/api/care-delivery/admission-requests/{id}/commit", admissionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"consentConfirmed\":true,\"sampleRequirementsAcknowledged\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.admissionStatus").value("order_created"))
                .andExpect(jsonPath("$.createdOrderId").exists());
    }

    @Test
    void quotationCanBeIssuedAcceptedAndConvertedIntoADiagnosticOrder() throws Exception {
        String patientId = registerPatient("Chien-Shiung", "Wu");
        String panelId = publishPanel();
        publishPriceListForPanel(panelId, "80.00");

        JsonNode quotation = postJson("/api/care-delivery/quotations", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, patientId, panelId));
        String quotationId = quotation.get("quotationId").asText();

        JsonNode issued = postJson("/api/care-delivery/quotations/" + quotationId + "/issue",
                "{\"discountKind\":\"percentage\",\"discountValue\":10}");
        assertThat(issued.get("status").asText()).isEqualTo("issued");
        assertThat(issued.get("totalAmount").get("amount").asDouble()).isEqualTo(72.00);

        mockMvc.perform(post("/api/care-delivery/quotations/{id}/accept", quotationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("accepted"));

        mockMvc.perform(post("/api/care-delivery/quotations/{id}/convert", quotationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("converted"))
                .andExpect(jsonPath("$.convertedOrderId").exists());
    }

    @Test
    void quotationRejectsDiscountBeyondStandardPolicyLimit() throws Exception {
        String panelId = publishPanel();
        publishPriceListForPanel(panelId, "100.00");

        JsonNode quotation = postJson("/api/care-delivery/quotations", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, panelId));

        mockMvc.perform(post("/api/care-delivery/quotations/{id}/issue", quotation.get("quotationId").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"discountKind\":\"percentage\",\"discountValue\":90}"))
                .andExpect(status().isConflict());
    }

    private String registerPatient(String givenName, String familyName) throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        JsonNode patient = postJson("/api/people/patients", """
                {"tenantId":"%s","laboratoryId":"%s","patientCode":"P-%s",
                 "givenName":"%s","familyName":"%s","birthDate":"1980-01-01","sexAtBirth":"female",
                 "primaryDocumentType":"national_id","primaryDocumentNumber":"DOC-%s"}
                """.formatted(tenantId, laboratoryId, suffix, givenName, familyName, suffix));
        return patient.get("patientId").asText();
    }

    private String registerDoctor(String givenName, String familyName) throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        JsonNode doctor = postJson("/api/people/doctors", """
                {"tenantId":"%s","laboratoryId":"%s","doctorCode":"D-%s",
                 "givenName":"%s","familyName":"%s","doctorType":"referring_external",
                 "primaryDocumentType":"professional_license","primaryDocumentNumber":"MD-%s"}
                """.formatted(tenantId, laboratoryId, suffix, givenName, familyName, suffix));
        return doctor.get("doctorId").asText();
    }

    private String publishPanel() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        JsonNode panel = postJson("/api/catalog/panels", """
                {"tenantId":"%s","laboratoryId":"%s","code":"PNL-%s","nameEn":"Panel %s","nameEs":"Panel %s",
                 "members":[{"testRefId":"test-1","mandatory":true},{"testRefId":"test-2","mandatory":false}]}
                """.formatted(tenantId, laboratoryId, suffix, suffix, suffix));
        String panelId = panel.get("panelId").asText();
        mockMvc.perform(post("/api/catalog/panels/{id}/publish", panelId)).andExpect(status().isOk());
        return panelId;
    }

    private void publishPriceListForPanel(String panelId, String amount) throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        JsonNode priceList = postJson("/api/catalog/price-lists", """
                {"tenantId":"%s","laboratoryId":"%s","code":"PRC-%s","nameEn":"Standard","nameEs":"Estandar",
                 "currency":"USD","effectiveFrom":"2026-01-01"}
                """.formatted(tenantId, laboratoryId, suffix));
        String priceListId = priceList.get("priceListId").asText();
        mockMvc.perform(post("/api/catalog/price-lists/{id}/entries", priceListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemType\":\"panel\",\"itemRefId\":\"%s\",\"amount\":%s}".formatted(panelId, amount)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/catalog/price-lists/{id}/publish", priceListId)).andExpect(status().isOk());
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
