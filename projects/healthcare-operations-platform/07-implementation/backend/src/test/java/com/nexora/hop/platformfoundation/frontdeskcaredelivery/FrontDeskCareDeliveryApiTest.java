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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskPolicyStore;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.ReferringDoctorAuthorizationPort;

/**
 * Functional coverage for the compiled MVP-MOD-004-BE-001 outputs and the MVP-MOD-004-BE-002
 * custom rules across the five Front Desk and Care Delivery capabilities: BCM-LAB-001 (order
 * create/price/accept/cancel/complete, referring-doctor eligibility, per-line pricing), BCM-ATT-001
 * (appointment request/confirm/check-in/overlap/capacity/no-show/preparation surfacing),
 * BCM-ATT-003/BCM-ATT-004 (reception queue ordering, admission commit and its configurable
 * acknowledgement policy) and BCM-ATT-006 (quotation issue/accept/convert, per-line pricing,
 * tenant-configurable discount policy).
 */
@AutoConfigureMockMvc
@SpringBootTest
class FrontDeskCareDeliveryApiTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FrontDeskPolicyStore policyStore;

    @Autowired
    private ReferringDoctorAuthorizationPort referringDoctorAuthorizationPort;

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
    void doctorIdFilterReturnsOnlyOrdersReferredByThatDoctorAndAuthorizationPortAgrees() throws Exception {
        String patientA = registerPatient("Ada", "Lovelace");
        String patientB = registerPatient("Marie", "Curie");
        String doctorOne = registerDoctor("Grace", "Hopper");
        String doctorTwo = registerDoctor("Alan", "Turing");
        String panelId = publishPanel();
        publishPriceListForPanel(panelId, "45.00");

        postJson("/api/clinical-operations/diagnostic-orders", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                 "patientId":"%s","doctorId":"%s","actorId":"receptionist-1",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, patientA, doctorOne, panelId));
        postJson("/api/clinical-operations/diagnostic-orders", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                 "patientId":"%s","doctorId":"%s","actorId":"receptionist-1",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, patientB, doctorTwo, panelId));

        // COM-MOD-009-PORTAL-002: real server-side filtering, the caller never receives
        // another doctor's orders even though both orders exist in the same tenant.
        mockMvc.perform(get("/api/clinical-operations/diagnostic-orders")
                        .param("tenantId", tenantId).param("doctorId", doctorOne))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].patientSnapshot.fullName").value("Ada Lovelace"))
                .andExpect(jsonPath("$[0].doctorSnapshot.fullName").value("Grace Hopper"));

        assertThat(referringDoctorAuthorizationPort.isPatientReferredByDoctor(tenantId, doctorOne, patientA)).isTrue();
        assertThat(referringDoctorAuthorizationPort.isPatientReferredByDoctor(tenantId, doctorOne, patientB)).isFalse();
    }

    @Test
    void diagnosticOrderPatientSnapshotRemainsImmutableAfterPatientProfileChanges() throws Exception {
        String patientId = registerPatient("Ada", "Lovelace");
        String panelId = publishPanel();
        publishPriceListForPanel(panelId, "45.00");

        JsonNode order = postJson("/api/clinical-operations/diagnostic-orders", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                 "patientId":"%s","actorId":"receptionist-1",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, patientId, panelId));
        String orderId = order.get("orderId").asText();
        String capturedDocumentNumberMasked = order.get("patientSnapshot").get("documentNumberMasked").asText();
        assertThat(order.get("patientSnapshot").get("fullName").asText()).isEqualTo("Ada Lovelace");
        assertThat(capturedDocumentNumberMasked).isNotBlank();
        assertThat(order.get("patientSnapshot").get("sourceVersion").asInt()).isEqualTo(1);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                        "/api/people/patients/{id}", patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"givenName":"Ada","familyName":"Byron","birthDate":"1980-01-01",
                                 "sexAtBirth":"female","primaryDocumentType":"national_id",
                                 "primaryDocumentNumber":"DOC-5678"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.familyName").value("Byron"))
                .andExpect(jsonPath("$.primaryDocumentNumberMasked").value(org.hamcrest.Matchers.endsWith("5678")))
                .andExpect(jsonPath("$.version").value(2));

        mockMvc.perform(get("/api/clinical-operations/diagnostic-orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientSnapshot.fullName").value("Ada Lovelace"))
                .andExpect(jsonPath("$.patientSnapshot.documentNumberMasked").value(capturedDocumentNumberMasked))
                .andExpect(jsonPath("$.patientSnapshot.sourceVersion").value(1));
    }

    @Test
    void diagnosticOrderRejectsIneligibleReferringDoctor() throws Exception {
        String patientId = registerPatient("Chien-Shiung", "Wu");
        String doctorId = registerDoctor("Unverified", "Physician", false);
        String panelId = publishPanel();

        mockMvc.perform(post("/api/clinical-operations/diagnostic-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                                 "patientId":"%s","doctorId":"%s",
                                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                                """.formatted(tenantId, laboratoryId, branchId, patientId, doctorId, panelId)))
                .andExpect(status().isConflict());
    }

    @Test
    void diagnosticOrderPricesEachLineFromItsOwnPriceList() throws Exception {
        String patientId = registerPatient("Rosalind", "Franklin");
        String panelA = publishPanel();
        publishPriceListForPanel(panelA, "30.00");
        String panelB = publishPanel();
        publishPriceListForItem(panelB, "70.00", "AGREEMENT-" + UUID.randomUUID());

        JsonNode order = postJson("/api/clinical-operations/diagnostic-orders", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                 "patientId":"%s","actorId":"receptionist-1",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1},
                          {"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, patientId, panelA, panelB));
        String orderId = order.get("orderId").asText();

        JsonNode priced = postJson("/api/clinical-operations/diagnostic-orders/" + orderId + "/price", "{}");
        assertThat(priced.get("pricingSnapshot").get("totalAmount").get("amount").asDouble()).isEqualTo(100.00);
    }

    @Test
    void diagnosticOrderCancellationRequiresOverrideJustificationOnceAccepted() throws Exception {
        String patientId = registerPatient("Barbara", "McClintock");
        String panelId = publishPanel();
        publishPriceListForPanel(panelId, "50.00");

        JsonNode draftOrder = postJson("/api/clinical-operations/diagnostic-orders", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                 "patientId":"%s","actorId":"receptionist-1",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, patientId, panelId));
        mockMvc.perform(post("/api/clinical-operations/diagnostic-orders/{id}/cancel", draftOrder.get("orderId").asText())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reasonCode\":\"patient_request\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("cancelled"));

        JsonNode acceptedOrder = postJson("/api/clinical-operations/diagnostic-orders", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                 "patientId":"%s","actorId":"receptionist-1",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, patientId, panelId));
        String acceptedOrderId = acceptedOrder.get("orderId").asText();
        postJson("/api/clinical-operations/diagnostic-orders/" + acceptedOrderId + "/price", "{}");
        mockMvc.perform(post("/api/clinical-operations/diagnostic-orders/{id}/accept", acceptedOrderId)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/clinical-operations/diagnostic-orders/{id}/cancel", acceptedOrderId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reasonCode\":\"patient_request\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/clinical-operations/diagnostic-orders/{id}/cancel", acceptedOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reasonCode\":\"patient_request\","
                                + "\"overrideJustification\":\"Doctor authorized cancellation after review\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("cancelled"));
    }

    @Test
    void diagnosticOrderCancellationRequiresOverrideOnceARealSampleIsCollectedRegardlessOfOrderStatus() throws Exception {
        String patientId = registerPatient("Rita", "Levi-Montalcini");
        String panelId = publishPanel();
        publishPriceListForPanel(panelId, "40.00");

        JsonNode order = postJson("/api/clinical-operations/diagnostic-orders", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                 "patientId":"%s","actorId":"receptionist-1",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, patientId, panelId));
        String orderId = order.get("orderId").asText();
        postJson("/api/clinical-operations/diagnostic-orders/" + orderId + "/price", "{}");

        MvcResult linesResult = mockMvc.perform(get("/api/clinical-operations/diagnostic-orders/{id}/lines", orderId))
                .andExpect(status().isOk()).andReturn();
        String orderLineId = objectMapper.readTree(linesResult.getResponse().getContentAsString()).get(0)
                .get("orderLineId").asText();

        // TD-BE-010: a real Sample already exists for this order, even though the order itself
        // has not left "priced" status yet — the order-status tier alone would have allowed a
        // plain cancel here, but the SampleReadPort-backed check now requires an override.
        postJson("/api/clinical-operations/samples", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","orderId":"%s","orderLineId":"%s",
                 "collectorId":"phlebotomist-1","collectionMethod":"venipuncture","containerUsed":"EDTA tube",
                 "patientId":"%s","patientFullName":"Rita Levi-Montalcini","patientBirthDate":"1980-01-01"}
                """.formatted(tenantId, laboratoryId, branchId, orderId, orderLineId, patientId));

        mockMvc.perform(post("/api/clinical-operations/diagnostic-orders/{id}/cancel", orderId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reasonCode\":\"patient_request\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/clinical-operations/diagnostic-orders/{id}/cancel", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reasonCode\":\"patient_request\","
                                + "\"overrideJustification\":\"Sample already collected, lab notified\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("cancelled"));
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
    void appointmentConfirmRejectsWhenBranchDailyCapacityIsExceeded() throws Exception {
        policyStore.setBranchDailyAppointmentCapacity(tenantId, 1);
        LocalDate start = LocalDate.now().plusDays(3);

        JsonNode first = postJson("/api/care-delivery/appointments", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s",
                 "scheduledStart":"%s","scheduledEnd":"%s","channel":"phone"}
                """.formatted(tenantId, laboratoryId, branchId, registerPatient("Grace", "Hopper"), start, start));
        mockMvc.perform(post("/api/care-delivery/appointments/{id}/confirm", first.get("appointmentId").asText()))
                .andExpect(status().isOk());

        JsonNode second = postJson("/api/care-delivery/appointments", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s",
                 "scheduledStart":"%s","scheduledEnd":"%s","channel":"phone"}
                """.formatted(tenantId, laboratoryId, branchId, registerPatient("Ada", "Byron"), start, start));
        mockMvc.perform(post("/api/care-delivery/appointments/{id}/confirm", second.get("appointmentId").asText()))
                .andExpect(status().isConflict());
    }

    @Test
    void appointmentNoShowRespectsTenantConfigurableGracePeriod() throws Exception {
        String patientId = registerPatient("Dorothy", "Vaughan");
        LocalDate future = LocalDate.now().plusDays(1);

        JsonNode notYetDue = postJson("/api/care-delivery/appointments", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s",
                 "scheduledStart":"%s","scheduledEnd":"%s","channel":"phone"}
                """.formatted(tenantId, laboratoryId, branchId, patientId, future, future));
        String notYetDueId = notYetDue.get("appointmentId").asText();
        mockMvc.perform(post("/api/care-delivery/appointments/{id}/confirm", notYetDueId)).andExpect(status().isOk());
        mockMvc.perform(post("/api/care-delivery/appointments/{id}/no-show", notYetDueId))
                .andExpect(status().isConflict());

        String elapsedPatientId = registerPatient("Hedy", "Lamarr");
        LocalDate past = LocalDate.now().minusDays(1);
        JsonNode elapsed = postJson("/api/care-delivery/appointments", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s",
                 "scheduledStart":"%s","scheduledEnd":"%s","channel":"phone"}
                """.formatted(tenantId, laboratoryId, branchId, elapsedPatientId, past, past));
        String elapsedId = elapsed.get("appointmentId").asText();
        mockMvc.perform(post("/api/care-delivery/appointments/{id}/confirm", elapsedId)).andExpect(status().isOk());
        mockMvc.perform(post("/api/care-delivery/appointments/{id}/no-show", elapsedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("no_show"));
    }

    @Test
    void appointmentSurfacesPublishedPreparationInstructionsForRequestedItems() throws Exception {
        String patientId = registerPatient("Vera", "Rubin");
        String testId = publishTest();
        publishPreparationForTarget(testId, "other", null);
        LocalDate start = LocalDate.now().plusDays(4);

        JsonNode appointment = postJson("/api/care-delivery/appointments", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s",
                 "scheduledStart":"%s","scheduledEnd":"%s","channel":"phone",
                 "requestedItems":[{"testDefinitionId":"%s","catalogItemKind":"test"}]}
                """.formatted(tenantId, laboratoryId, branchId, patientId, start, start, testId));

        mockMvc.perform(get("/api/care-delivery/appointments/{id}/preparation-instructions",
                        appointment.get("appointmentId").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("other"));
    }

    @Test
    void receptionQueueOrdersUrgentPriorityAheadOfLongerWaitingNormalVisit() throws Exception {
        String normalPatientId = registerPatient("Katherine", "Coleman");
        String urgentPatientId = registerPatient("Mary", "Jackson");

        JsonNode normalVisit = postJson("/api/care-delivery/reception-visits", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s","intakeChannel":"walk_in"}
                """.formatted(tenantId, laboratoryId, branchId, normalPatientId));
        JsonNode urgentVisit = postJson("/api/care-delivery/reception-visits", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s","intakeChannel":"walk_in"}
                """.formatted(tenantId, laboratoryId, branchId, urgentPatientId));
        mockMvc.perform(post("/api/care-delivery/reception-visits/{id}/priority", urgentVisit.get("visitId").asText())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"priority\":\"urgent\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/care-delivery/reception-visits").param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].visitId").value(urgentVisit.get("visitId").asText()))
                .andExpect(jsonPath("$[1].visitId").value(normalVisit.get("visitId").asText()));
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
    void admissionCommitHonorsTenantConfigurableAcknowledgementPolicy() throws Exception {
        policyStore.setRequiredAdmissionAcknowledgements(tenantId, java.util.Set.of(FrontDeskPolicyStore.ACK_CONSENT));
        String patientId = registerPatient("Annie", "Easley");
        String panelId = publishPanel();
        publishPriceListForPanel(panelId, "60.00");

        JsonNode visit = postJson("/api/care-delivery/reception-visits", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","patientId":"%s","intakeChannel":"walk_in"}
                """.formatted(tenantId, laboratoryId, branchId, patientId));
        String visitId = visit.get("visitId").asText();
        mockMvc.perform(post("/api/care-delivery/reception-visits/{id}/confirm-identity", visitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identityConfirmationMethod\":\"document_check\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/care-delivery/reception-visits/{id}/advance-to-admission", visitId))
                .andExpect(status().isOk());

        JsonNode admission = postJson("/api/care-delivery/admission-requests", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","visitId":"%s","patientId":"%s"}
                """.formatted(tenantId, laboratoryId, branchId, visitId, patientId));
        String admissionId = admission.get("admissionId").asText();
        mockMvc.perform(post("/api/care-delivery/admission-requests/{id}/mark-ready", admissionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"catalogSelection":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                                """.formatted(panelId)))
                .andExpect(status().isOk());

        // Sample-requirement acknowledgement is not tenant-required here, so its absence must not block commit.
        mockMvc.perform(post("/api/care-delivery/admission-requests/{id}/commit", admissionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"consentConfirmed\":true,\"sampleRequirementsAcknowledged\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.admissionStatus").value("order_created"));
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
    void quotationChannelDefaultsToEmployeePortalWhenOmitted() throws Exception {
        String panelId = publishPanel();
        publishPriceListForPanel(panelId, "80.00");

        JsonNode quotation = postJson("/api/care-delivery/quotations", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, panelId));

        assertThat(quotation.get("channel").asText()).isEqualTo("employee_portal");
    }

    @Test
    void quotationChannelAcceptsAnExplicitInternalValue() throws Exception {
        String panelId = publishPanel();
        publishPriceListForPanel(panelId, "80.00");

        JsonNode quotation = postJson("/api/care-delivery/quotations", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","channel":"phone",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, panelId));

        assertThat(quotation.get("channel").asText()).isEqualTo("phone");
    }

    @Test
    void quotationRejectsPublicWebsiteChannelFromInternalEndpoint() throws Exception {
        mockMvc.perform(post("/api/care-delivery/quotations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","channel":"public_website",
                                 "lines":[]}
                                """.formatted(tenantId, laboratoryId, branchId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void quotationIssuePricesEachLineFromItsOwnPriceList() throws Exception {
        String panelA = publishPanel();
        publishPriceListForPanel(panelA, "20.00");
        String panelB = publishPanel();
        publishPriceListForItem(panelB, "55.00", "AGREEMENT-" + UUID.randomUUID());

        JsonNode quotation = postJson("/api/care-delivery/quotations", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1},
                          {"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, panelA, panelB));

        JsonNode issued = postJson("/api/care-delivery/quotations/" + quotation.get("quotationId").asText() + "/issue", "{}");
        assertThat(issued.get("totalAmount").get("amount").asDouble()).isEqualTo(75.00);
    }

    @Test
    void quotationDiscountPolicyIsTenantConfigurable() throws Exception {
        policyStore.setStandardMaxDiscountPercentage(tenantId, java.math.BigDecimal.valueOf(5));
        String panelId = publishPanel();
        publishPriceListForPanel(panelId, "100.00");

        JsonNode quotation = postJson("/api/care-delivery/quotations", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s",
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"panel","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, panelId));

        mockMvc.perform(post("/api/care-delivery/quotations/{id}/issue", quotation.get("quotationId").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"discountKind\":\"percentage\",\"discountValue\":10}"))
                .andExpect(status().isConflict());
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
        return registerDoctor(givenName, familyName, true);
    }

    private String registerDoctor(String givenName, String familyName, boolean eligibleAsReferring) throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        JsonNode doctor = postJson("/api/people/doctors", """
                {"tenantId":"%s","laboratoryId":"%s","doctorCode":"D-%s",
                 "givenName":"%s","familyName":"%s","doctorType":"referring_external",
                 "primaryDocumentType":"professional_license","primaryDocumentNumber":"MD-%s"}
                """.formatted(tenantId, laboratoryId, suffix, givenName, familyName, suffix));
        String doctorId = doctor.get("doctorId").asText();
        if (eligibleAsReferring) {
            JsonNode credential = postJson("/api/people/doctors/" + doctorId + "/credentials", """
                    {"credentialType":"medical_license","credentialNumber":"LIC-%s",
                     "issuingAuthority":"Medical Board","issuedAt":"2020-01-01"}
                    """.formatted(suffix));
            String credentialId = credential.get("credentialId").asText();
            mockMvc.perform(post("/api/people/doctors/{doctorId}/credentials/{credentialId}/verify", doctorId, credentialId))
                    .andExpect(status().isOk());
        }
        return doctorId;
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

    private String publishTest() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        JsonNode test = postJson("/api/catalog/tests", """
                {"tenantId":"%s","laboratoryId":"%s","code":"TST-%s","nameEn":"Test %s","nameEs":"Prueba %s",
                 "resultType":"numeric","measurementUnit":"mg/dL",
                 "analyteRefIds":["analyte-1"],"sampleRequirementRefIds":["sample-1"]}
                """.formatted(tenantId, laboratoryId, suffix, suffix, suffix));
        String testId = test.get("testDefinitionId").asText();
        mockMvc.perform(post("/api/catalog/tests/{id}/publish", testId)).andExpect(status().isOk());
        return testId;
    }

    private void publishPreparationForTarget(String targetRefId, String category, Integer durationHours) throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        JsonNode preparation = postJson("/api/catalog/preparations", """
                {"tenantId":"%s","laboratoryId":"%s","code":"PREP-%s","titleEn":"Prep %s","titleEs":"Prep %s",
                 "instructionTextEn":"Fast for 8 hours","instructionTextEs":"Ayuno de 8 horas",
                 "category":"%s","durationHours":%s}
                """.formatted(tenantId, laboratoryId, suffix, suffix, suffix, category,
                        durationHours == null ? "null" : durationHours));
        String preparationId = preparation.get("preparationId").asText();
        mockMvc.perform(post("/api/catalog/preparations/{id}/publish", preparationId)).andExpect(status().isOk());
        mockMvc.perform(post("/api/catalog/preparations/{id}/assignments", preparationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetType\":\"test\",\"targetRefId\":\"%s\"}".formatted(targetRefId)))
                .andExpect(status().isNoContent());
    }

    private void publishPriceListForPanel(String panelId, String amount) throws Exception {
        publishPriceListForItem(panelId, amount, null);
    }

    /**
     * Two price lists in the same laboratory/currency with the same (or no) agreementRefId
     * conflict on publish if their effective windows overlap (RN-005 in bcm-svc-009). Tests that
     * need two simultaneously published price lists (multi-price-list resolution) must pass a
     * distinct agreementRefId for the second one; PriceListManagementService.getEffectivePriceSnapshot
     * still resolves either when called with a null agreementRefId (no scope filter).
     */
    private void publishPriceListForItem(String panelId, String amount, String agreementRefId) throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String agreementField = agreementRefId == null ? "" : ",\"agreementRefId\":\"%s\"".formatted(agreementRefId);
        JsonNode priceList = postJson("/api/catalog/price-lists", """
                {"tenantId":"%s","laboratoryId":"%s","code":"PRC-%s","nameEn":"Standard","nameEs":"Estandar",
                 "currency":"USD","effectiveFrom":"2026-01-01"%s}
                """.formatted(tenantId, laboratoryId, suffix, agreementField));
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
