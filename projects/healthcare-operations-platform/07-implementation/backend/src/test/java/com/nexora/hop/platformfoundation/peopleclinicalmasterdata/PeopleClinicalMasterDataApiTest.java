package com.nexora.hop.platformfoundation.peopleclinicalmasterdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Functional coverage for the generatable flows of the peopleclinicalmasterdata bounded context
 * compiled by MVP-MOD-003-BE-001. Every operation classified as {@code generatable: false} in the
 * openapi-source models raises a HTTP 501 with backlogItem MVP-MOD-003-BE-002; those hooks are
 * asserted alongside the happy paths.
 */
@AutoConfigureMockMvc
@SpringBootTest
class PeopleClinicalMasterDataApiTest {

    private static final String LAB = "lab-1";
    private static final String BRANCH = "branch-1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tenantId;

    @BeforeEach
    void createTenant() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"People Tenant\"}");
        tenantId = tenant.get("tenantId").asText();
    }

    @Test
    void patientCanBeRegisteredListedUpdatedSnapshottedAndDeactivated() throws Exception {
        JsonNode patient = postJson("/api/people/patients", """
                {"tenantId":"%s","laboratoryId":"%s","patientCode":"P-0001",
                 "givenName":"Ada","familyName":"Lovelace","birthDate":"1990-12-10",
                 "sexAtBirth":"female","primaryDocumentType":"national_id","primaryDocumentNumber":"DOC-1234"}
                """.formatted(tenantId, LAB));
        String patientId = patient.get("patientId").asText();
        assertThat(patient.get("status").asText()).isEqualTo("active");
        assertThat(patient.get("primaryDocumentNumberMasked").asText()).endsWith("1234");

        mockMvc.perform(get("/api/people/patients").param("laboratoryId", LAB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").exists());

        mockMvc.perform(put("/api/people/patients/{id}", patientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"givenName":"Ada","familyName":"Byron","birthDate":"1990-12-10",
                         "sexAtBirth":"female","primaryDocumentType":"national_id","primaryDocumentNumber":"DOC-5678"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.familyName").value("Byron"))
                .andExpect(jsonPath("$.version").value(2));

        mockMvc.perform(get("/api/people/patients/{id}/snapshot", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Ada Byron"));

        mockMvc.perform(post("/api/people/patients/{id}/deactivate", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("inactive"));
    }

    @Test
    void patientRegistrationRejectsDuplicateCodeAndMissingTenant() throws Exception {
        postJson("/api/people/patients", """
                {"tenantId":"%s","laboratoryId":"%s","patientCode":"P-DUP","givenName":"John","familyName":"Doe",
                 "birthDate":"1985-01-01","sexAtBirth":"male",
                 "primaryDocumentType":"passport","primaryDocumentNumber":"P-9999"}
                """.formatted(tenantId, LAB));

        mockMvc.perform(post("/api/people/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tenantId":"%s","laboratoryId":"%s","patientCode":"P-DUP","givenName":"Jane","familyName":"Roe",
                         "birthDate":"1990-01-01","sexAtBirth":"female",
                         "primaryDocumentType":"passport","primaryDocumentNumber":"P-1000"}
                        """.formatted(tenantId, LAB)))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/people/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tenantId":"missing-tenant","laboratoryId":"%s","patientCode":"P-XXX","givenName":"X","familyName":"Y",
                         "birthDate":"1990-01-01","sexAtBirth":"male",
                         "primaryDocumentType":"passport","primaryDocumentNumber":"P-2000"}
                        """.formatted(LAB)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patientCustomRuleEndpointsRespondWith501AndDeferrableRuleId() throws Exception {
        JsonNode patient = postJson("/api/people/patients", """
                {"tenantId":"%s","laboratoryId":"%s","patientCode":"P-HOOK","givenName":"Ada","familyName":"Lovelace",
                 "birthDate":"1990-12-10","sexAtBirth":"female",
                 "primaryDocumentType":"national_id","primaryDocumentNumber":"DOC-HOOK"}
                """.formatted(tenantId, LAB));
        String patientId = patient.get("patientId").asText();

        mockMvc.perform(post("/api/people/patients/{id}/merge", patientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"survivingPatientId\":\"other\"}"))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.backlogItem").value("MVP-MOD-003-BE-002"))
                .andExpect(jsonPath("$.ruleId").value("BCM-PER-002-RN-005"));

        JsonNode consent = postJson("/api/people/patients/{id}/consents"
                .replace("{id}", patientId), """
                {"consentType":"data_processing","granted":true,"grantedBy":"patient"}
                """);
        String consentId = consent.get("consentId").asText();

        mockMvc.perform(post("/api/people/patients/{p}/consents/{c}/revoke", patientId, consentId))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.ruleId").value("BCM-PER-002-RN-007"));
    }

    @Test
    void patientRepresentativeConsentAndDocumentGeneratableFlowsWork() throws Exception {
        JsonNode patient = postJson("/api/people/patients", """
                {"tenantId":"%s","laboratoryId":"%s","patientCode":"P-EXT","givenName":"Grace","familyName":"Hopper",
                 "birthDate":"1920-12-09","sexAtBirth":"female",
                 "primaryDocumentType":"passport","primaryDocumentNumber":"P-EXT-1"}
                """.formatted(tenantId, LAB));
        String patientId = patient.get("patientId").asText();

        JsonNode representative = postJson("/api/people/patients/{id}/representatives".replace("{id}", patientId), """
                {"relationship":"legal_guardian","givenName":"Frank","familyName":"Hopper",
                 "documentType":"national_id","documentNumber":"REP-1","authorizationFrom":"2026-01-01"}
                """);
        assertThat(representative.get("relationship").asText()).isEqualTo("legal_guardian");

        JsonNode consent = postJson("/api/people/patients/{id}/consents".replace("{id}", patientId), """
                {"consentType":"portal_access","granted":true,"grantedBy":"representative"}
                """);
        assertThat(consent.get("granted").asBoolean()).isTrue();

        JsonNode document = postJson("/api/people/patients/{id}/documents".replace("{id}", patientId), """
                {"category":"insurance","fileReference":"blob://insurance.pdf"}
                """);
        String documentId = document.get("documentId").asText();

        mockMvc.perform(get("/api/people/patients/{id}/documents", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].documentId").value(documentId));

        mockMvc.perform(delete("/api/people/patients/{p}/documents/{d}", patientId, documentId))
                .andExpect(status().isNoContent());
    }

    @Test
    void doctorCanBeRegisteredListedUpdatedRetiredWithSpecialtiesAndCredentials() throws Exception {
        JsonNode doctor = postJson("/api/people/doctors", """
                {"tenantId":"%s","laboratoryId":"%s","doctorCode":"D-0001",
                 "givenName":"Isaac","familyName":"Newton","doctorType":"referring_external",
                 "primaryDocumentType":"professional_license","primaryDocumentNumber":"MD-42"}
                """.formatted(tenantId, LAB));
        String doctorId = doctor.get("doctorId").asText();
        assertThat(doctor.get("status").asText()).isEqualTo("active");

        mockMvc.perform(get("/api/people/doctors").param("laboratoryId", LAB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].doctorId").exists());

        mockMvc.perform(put("/api/people/doctors/{id}", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"givenName":"Isaac","familyName":"Newton","doctorType":"both",
                         "primaryDocumentType":"professional_license","primaryDocumentNumber":"MD-43"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorType").value("both"));

        JsonNode credential = postJson("/api/people/doctors/{id}/credentials".replace("{id}", doctorId), """
                {"credentialType":"medical_license","credentialNumber":"LIC-1","issuingAuthority":"State Board",
                 "issuedAt":"2020-01-01"}
                """);
        assertThat(credential.get("verificationStatus").asText()).isEqualTo("pending");

        JsonNode specialty = postJson("/api/people/doctors/{id}/specialties".replace("{id}", doctorId),
                "{\"specialtyCode\":\"internal_medicine\",\"primary\":true}");
        String assignmentId = specialty.get("assignmentId").asText();

        mockMvc.perform(get("/api/people/doctors/{id}/snapshot", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("active"));

        mockMvc.perform(delete("/api/people/doctors/{d}/specialties/{a}", doctorId, assignmentId))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/people/doctors/{id}/retire", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("retired"));
    }

    @Test
    void doctorCustomRuleEndpointsRespondWith501() throws Exception {
        JsonNode doctor = postJson("/api/people/doctors", """
                {"tenantId":"%s","laboratoryId":"%s","doctorCode":"D-HOOK",
                 "givenName":"Marie","familyName":"Curie","doctorType":"referring_external",
                 "primaryDocumentType":"professional_license","primaryDocumentNumber":"MD-CURIE"}
                """.formatted(tenantId, LAB));
        String doctorId = doctor.get("doctorId").asText();

        mockMvc.perform(post("/api/people/doctors/{id}/suspend", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reasonCode\":\"quality_review\"}"))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.ruleId").value("BCM-PER-003-RN-006"));

        mockMvc.perform(post("/api/people/doctors/{id}/portal-access/prepare", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"portalEmail\":\"marie@example.org\"}"))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.ruleId").value("BCM-PER-003-RN-007"));

        JsonNode credential = postJson("/api/people/doctors/{id}/credentials".replace("{id}", doctorId), """
                {"credentialType":"medical_license","credentialNumber":"LIC-HOOK","issuingAuthority":"State Board",
                 "issuedAt":"2020-01-01"}
                """);
        String credentialId = credential.get("credentialId").asText();

        mockMvc.perform(post("/api/people/doctors/{d}/credentials/{c}/verify", doctorId, credentialId))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.ruleId").value("BCM-PER-003-RN-004"));
        mockMvc.perform(post("/api/people/doctors/{d}/credentials/{c}/revoke", doctorId, credentialId))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.ruleId").value("BCM-PER-003-RN-005"));
    }

    @Test
    void personSearchReturnsBothPatientsAndDoctorsAndDuplicateDetectionAudits() throws Exception {
        postJson("/api/people/patients", """
                {"tenantId":"%s","laboratoryId":"%s","patientCode":"P-SEARCH","givenName":"Emmy","familyName":"Noether",
                 "birthDate":"1882-03-23","sexAtBirth":"female",
                 "primaryDocumentType":"national_id","primaryDocumentNumber":"DOC-N"}
                """.formatted(tenantId, LAB));
        postJson("/api/people/doctors", """
                {"tenantId":"%s","laboratoryId":"%s","doctorCode":"D-SEARCH",
                 "givenName":"Emmy","familyName":"Noether","doctorType":"referring_external",
                 "primaryDocumentType":"professional_license","primaryDocumentNumber":"MD-N"}
                """.formatted(tenantId, LAB));

        mockMvc.perform(get("/api/people/persons/search")
                        .param("tenantId", tenantId)
                        .param("familyName", "Noether"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        JsonNode candidates = postJson("/api/people/persons/duplicates/detect", """
                {"tenantId":"%s","familyName":"Noether","givenName":"Emmy","birthDate":"1882-03-23"}
                """.formatted(tenantId), status().isOk());
        assertThat(candidates.isArray()).isTrue();
        assertThat(candidates.size()).isGreaterThanOrEqualTo(2);

        mockMvc.perform(post("/api/people/persons/index/rebuild").param("tenantId", tenantId))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.backlogItem").value("MVP-MOD-003-BE-002"));

        mockMvc.perform(post("/api/people/persons/merges")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tenantId":"%s","sourceRecordId":"a","targetRecordId":"b"}
                        """.formatted(tenantId)))
                .andExpect(status().isNotImplemented());
    }

    @Test
    void patientRegistrationCanBeStartedAndCancelledAndCommitIsDeferred() throws Exception {
        JsonNode registration = postJson("/api/care-delivery/patient-registrations", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                 "registrationKind":"new_patient","givenName":"Alan","familyName":"Turing",
                 "birthDate":"1912-06-23","documentType":"national_id","documentNumber":"REG-1"}
                """.formatted(tenantId, LAB, BRANCH));
        String registrationId = registration.get("registrationRequestId").asText();
        assertThat(registration.get("outcome").asText()).isEqualTo("pending");
        assertThat(registration.get("normalizedFamilyName").asText()).isEqualTo("TURING");

        mockMvc.perform(get("/api/care-delivery/patient-registrations").param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].registrationRequestId").value(registrationId));

        mockMvc.perform(post("/api/care-delivery/patient-registrations/{id}/commit", registrationId))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.backlogItem").value("MVP-MOD-003-BE-002"));

        mockMvc.perform(post("/api/care-delivery/patient-registrations/{id}/cancel", registrationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reasonCode\":\"withdrew\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outcome").value("cancelled"));
    }

    // -- Test helpers ----------------------------------------------------------------------

    private JsonNode postJson(String path, String json) throws Exception {
        String body = mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(body);
    }

    private JsonNode postJson(String path, String json,
            org.springframework.test.web.servlet.ResultMatcher matcher) throws Exception {
        String body = mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(matcher)
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(body);
    }
}
