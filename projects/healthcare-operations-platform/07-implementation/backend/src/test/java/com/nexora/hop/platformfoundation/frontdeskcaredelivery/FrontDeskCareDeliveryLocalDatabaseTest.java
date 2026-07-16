package com.nexora.hop.platformfoundation.frontdeskcaredelivery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Validates that the JDBC adapters compiled for frontdeskcaredelivery persist to the real
 * Postgres schema created by {@code db/front-desk-care-delivery/schema.sql}. Runs only when the
 * system property {@code hop.local-db-tests=true} is set.
 */
@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class FrontDeskCareDeliveryLocalDatabaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void diagnosticOrderAppointmentReceptionAdmissionAndQuotationPersistInPostgres() throws Exception {
        String runToken = UUID.randomUUID().toString().substring(0, 8);

        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Front Desk DB Tenant\"}");
        String tenantId = tenant.get("tenantId").asText();
        JsonNode laboratory = postJson("/api/organization/laboratories",
                "{\"tenantId\":\"%s\",\"name\":\"Front Desk DB Lab\"}".formatted(tenantId));
        String laboratoryId = laboratory.get("laboratoryId").asText();
        JsonNode branch = postJson("/api/organization/branches",
                "{\"laboratoryId\":\"%s\",\"name\":\"Front Desk DB Branch\"}".formatted(laboratoryId));
        String branchId = branch.get("branchId").asText();

        JsonNode patient = postJson("/api/people/patients", """
                {"tenantId":"%s","laboratoryId":"%s","patientCode":"DB-FD-%s",
                 "givenName":"Dorothy","familyName":"Vaughan","birthDate":"1988-03-03","sexAtBirth":"female",
                 "primaryDocumentType":"national_id","primaryDocumentNumber":"DB-FD-DOC-%s"}
                """.formatted(tenantId, laboratoryId, runToken, runToken));
        String patientId = patient.get("patientId").asText();

        JsonNode panel = postJson("/api/catalog/panels", """
                {"tenantId":"%s","laboratoryId":"%s","code":"DB-PNL-%s","nameEn":"DB Panel","nameEs":"Panel DB",
                 "members":[{"testRefId":"test-1","mandatory":true},{"testRefId":"test-2","mandatory":false}]}
                """.formatted(tenantId, laboratoryId, runToken));
        String panelId = panel.get("panelId").asText();
        mockMvc.perform(post("/api/catalog/panels/{id}/publish", panelId)).andExpect(status().isOk());

        JsonNode priceList = postJson("/api/catalog/price-lists", """
                {"tenantId":"%s","laboratoryId":"%s","code":"DB-PRC-%s","nameEn":"DB Standard","nameEs":"DB Estandar",
                 "currency":"USD","effectiveFrom":"2026-01-01"}
                """.formatted(tenantId, laboratoryId, runToken));
        String priceListId = priceList.get("priceListId").asText();
        mockMvc.perform(post("/api/catalog/price-lists/{id}/entries", priceListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemType\":\"panel\",\"itemRefId\":\"%s\",\"amount\":33.00}".formatted(panelId)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/catalog/price-lists/{id}/publish", priceListId)).andExpect(status().isOk());

        JsonNode order = postJson("/api/clinical-operations/diagnostic-orders", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in","patientId":"%s",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, patientId, panelId));
        String orderId = order.get("orderId").asText();
        postJson("/api/clinical-operations/diagnostic-orders/" + orderId + "/price", "{}");

        JsonNode appointment = postJson("/api/care-delivery/appointments", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s",
                 "scheduledStart":"2026-08-01","scheduledEnd":"2026-08-01","channel":"phone"}
                """.formatted(tenantId, laboratoryId, branchId, patientId));

        JsonNode visit = postJson("/api/care-delivery/reception-visits", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s","intakeChannel":"walk_in"}
                """.formatted(tenantId, laboratoryId, branchId, patientId));

        JsonNode quotation = postJson("/api/care-delivery/quotations", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, patientId, panelId));

        Integer orderCount = jdbcTemplate.queryForObject(
                "select count(*) from care_delivery.diagnostic_orders where order_id = ?", Integer.class, orderId);
        Integer orderLineCount = jdbcTemplate.queryForObject(
                "select count(*) from care_delivery.diagnostic_order_lines where order_id = ?", Integer.class, orderId);
        Integer appointmentCount = jdbcTemplate.queryForObject(
                "select count(*) from care_delivery.appointments where appointment_id = ?", Integer.class,
                appointment.get("appointmentId").asText());
        Integer visitCount = jdbcTemplate.queryForObject(
                "select count(*) from care_delivery.reception_visits where visit_id = ?", Integer.class,
                visit.get("visitId").asText());
        Integer quotationCount = jdbcTemplate.queryForObject(
                "select count(*) from care_delivery.quotations where quotation_id = ?", Integer.class,
                quotation.get("quotationId").asText());

        assertThat(orderCount).isOne();
        assertThat(orderLineCount).isGreaterThanOrEqualTo(1);
        assertThat(appointmentCount).isOne();
        assertThat(visitCount).isOne();
        assertThat(quotationCount).isOne();

        String persistedPatientFullName = jdbcTemplate.queryForObject(
                "select patient_full_name from care_delivery.diagnostic_orders where order_id = ?",
                String.class, orderId);
        assertThat(persistedPatientFullName).isEqualTo("Dorothy Vaughan");
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
