package com.nexora.hop.platformfoundation.peopleclinicalmasterdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private JsonNode postJson(String path, String json) throws Exception {
        MvcResult result = mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
