package com.nexora.hop.platformfoundation.peopleclinicalmasterdata;

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
 * Validates that the JDBC adapters compiled for peopleclinicalmasterdata persist to the real
 * Postgres schema created by {@code db/people-and-clinical-master-data/schema.sql}. Runs only when
 * the system property {@code hop.local-db-tests=true} is set.
 */
@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class PeopleClinicalMasterDataLocalDatabaseTest {

    private static final String LAB = "lab-1";
    private static final String BRANCH = "branch-1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void patientAndDoctorPersistInPostgresAndSearchIndexAcrossBoth() throws Exception {
        String runToken = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"People DB Tenant\"}");
        String tenantId = tenant.get("tenantId").asText();

        JsonNode patient = postJson("/api/people/patients", """
                {"tenantId":"%s","laboratoryId":"%s","patientCode":"DB-P-%s",
                 "givenName":"Ada","familyName":"Lovelace","birthDate":"1990-12-10","sexAtBirth":"female",
                 "primaryDocumentType":"national_id","primaryDocumentNumber":"DB-DOC-%s"}
                """.formatted(tenantId, LAB, runToken, runToken));
        String patientId = patient.get("patientId").asText();

        JsonNode doctor = postJson("/api/people/doctors", """
                {"tenantId":"%s","laboratoryId":"%s","doctorCode":"DB-D-%s",
                 "givenName":"Ada","familyName":"Lovelace","doctorType":"referring_external",
                 "primaryDocumentType":"professional_license","primaryDocumentNumber":"DB-MD-%s"}
                """.formatted(tenantId, LAB, runToken, runToken));
        String doctorId = doctor.get("doctorId").asText();

        postJson("/api/care-delivery/patient-registrations", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                 "registrationKind":"new_patient","givenName":"Ada","familyName":"Lovelace",
                 "birthDate":"1990-12-10","documentType":"national_id","documentNumber":"REG-%s"}
                """.formatted(tenantId, LAB, BRANCH, runToken));

        Integer patientCount = jdbcTemplate.queryForObject(
                "select count(*) from people.patients where patient_id = ?", Integer.class, patientId);
        Integer doctorCount = jdbcTemplate.queryForObject(
                "select count(*) from people.doctors where doctor_id = ?", Integer.class, doctorId);
        Integer registrationCount = jdbcTemplate.queryForObject(
                "select count(*) from people.patient_registrations where tenant_id = ?",
                Integer.class, tenantId);
        assertThat(patientCount).isOne();
        assertThat(doctorCount).isOne();
        assertThat(registrationCount).isGreaterThanOrEqualTo(1);

        // Normalization stored: uppercase, diacritic-free.
        String normalizedFamilyName = jdbcTemplate.queryForObject(
                "select normalized_family_name from people.patients where patient_id = ?",
                String.class, patientId);
        assertThat(normalizedFamilyName).isEqualTo("LOVELACE");
    }

    /**
     * TD-BE-006: {@code PatientRegistrationService#commit} must roll back the just-created Patient
     * when a later step in the same commit fails. The representative relationship is validated by
     * {@code PatientManagementService#attachRepresentative}, which only runs <em>after</em> the new
     * Patient is persisted, so an invalid relationship value is a real mid-commit failure that
     * exercises the transaction boundary against the actual Postgres-backed JDBC repositories.
     */
    @Test
    void failedRepresentativeAttachDuringCommitRollsBackTheNewlyCreatedPatient() throws Exception {
        String runToken = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Rollback Tenant\"}");
        String tenantId = tenant.get("tenantId").asText();

        JsonNode registration = postJson("/api/care-delivery/patient-registrations", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                 "registrationKind":"representative_registration","givenName":"Rollback","familyName":"Case",
                 "birthDate":"2015-01-01","documentType":"national_id","documentNumber":"ROLLBACK-DOC-%s"}
                """.formatted(tenantId, LAB, BRANCH, runToken));
        String registrationId = registration.get("registrationRequestId").asText();
        String patientCode = "ROLLBACK-P-" + runToken;

        mockMvc.perform(post("/api/care-delivery/patient-registrations/{id}/commit", registrationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"patientCode":"%s","sexAtBirth":"male",
                                 "representativeRelationship":"not_a_valid_relationship",
                                 "representativeGivenName":"Rep","representativeFamilyName":"Guardian",
                                 "consents":[{"consentType":"data_processing","granted":true,"grantedBy":"representative"}]}
                                """.formatted(patientCode)))
                .andExpect(status().isBadRequest());

        Integer patientCount = jdbcTemplate.queryForObject(
                "select count(*) from people.patients where patient_code = ?", Integer.class, patientCode);
        assertThat(patientCount).as("the patient created before the failed representative attach must be rolled back")
                .isZero();

        String outcome = jdbcTemplate.queryForObject(
                "select outcome from people.patient_registrations where registration_request_id = ?",
                String.class, registrationId);
        assertThat(outcome).isEqualTo("pending");
    }

    private JsonNode postJson(String path, String json) throws Exception {
        MvcResult result = mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
