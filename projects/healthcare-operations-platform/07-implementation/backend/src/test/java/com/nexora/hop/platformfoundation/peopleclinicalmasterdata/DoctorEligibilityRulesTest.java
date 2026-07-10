package com.nexora.hop.platformfoundation.peopleclinicalmasterdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.AttachCredentialCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.DoctorManagementService;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.RegisterDoctorCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.Doctor;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.ProfessionalCredential;

/**
 * Direct service-level coverage for BCM-PER-003 RN-004/RN-006
 * ({@link DoctorManagementService#isEligibleAsReferringDoctor(String)}), which is a cross-context
 * policy consulted through {@code DoctorDirectory} rather than a REST operation of its own.
 */
@AutoConfigureMockMvc
@SpringBootTest
class DoctorEligibilityRulesTest {

    private static final String LAB = "lab-1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorManagementService doctorManagementService;

    private String tenantId;

    @BeforeEach
    void createTenant() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Eligibility Tenant\"}");
        tenantId = tenant.get("tenantId").asText();
    }

    @Test
    void doctorIsNotEligibleUntilAVerifiedMedicalLicenseExists() {
        Doctor doctor = doctorManagementService.register(new RegisterDoctorCommand(
                tenantId, LAB, "D-ELIG-1", "Marie", null, "Curie", null, Doctor.TYPE_REFERRING_EXTERNAL,
                "professional_license", "MD-ELIG-1", null, null, null, null, null, null));

        assertThat(doctorManagementService.isEligibleAsReferringDoctor(doctor.doctorId())).isFalse();

        ProfessionalCredential credential = doctorManagementService.attachCredential(doctor.doctorId(),
                new AttachCredentialCommand(ProfessionalCredential.TYPE_MEDICAL_LICENSE, "LIC-ELIG-1",
                        "State Board", null, java.time.LocalDate.of(2020, 1, 1), null));
        assertThat(doctorManagementService.isEligibleAsReferringDoctor(doctor.doctorId())).isFalse();

        doctorManagementService.verifyCredential(doctor.doctorId(), credential.credentialId());
        assertThat(doctorManagementService.isEligibleAsReferringDoctor(doctor.doctorId())).isTrue();

        doctorManagementService.suspend(doctor.doctorId(), "quality_review");
        assertThat(doctorManagementService.isEligibleAsReferringDoctor(doctor.doctorId()))
                .as("a suspended doctor is never eligible, even with a verified credential")
                .isFalse();
    }

    @Test
    void revokingTheOnlyVerifiedCredentialRemovesEligibility() {
        Doctor doctor = doctorManagementService.register(new RegisterDoctorCommand(
                tenantId, LAB, "D-ELIG-2", "Rosalind", null, "Franklin", null, Doctor.TYPE_REFERRING_EXTERNAL,
                "professional_license", "MD-ELIG-2", null, null, null, null, null, null));
        ProfessionalCredential credential = doctorManagementService.attachCredential(doctor.doctorId(),
                new AttachCredentialCommand(ProfessionalCredential.TYPE_MEDICAL_LICENSE, "LIC-ELIG-2",
                        "State Board", null, java.time.LocalDate.of(2020, 1, 1), null));
        doctorManagementService.verifyCredential(doctor.doctorId(), credential.credentialId());
        assertThat(doctorManagementService.isEligibleAsReferringDoctor(doctor.doctorId())).isTrue();

        doctorManagementService.revokeCredential(doctor.doctorId(), credential.credentialId());
        assertThat(doctorManagementService.isEligibleAsReferringDoctor(doctor.doctorId())).isFalse();
    }

    @Test
    void expiredCredentialCannotBeVerifiedAndDoesNotGrantEligibility() {
        Doctor doctor = doctorManagementService.register(new RegisterDoctorCommand(
                tenantId, LAB, "D-ELIG-3", "Alan", null, "Turing", null, Doctor.TYPE_REFERRING_EXTERNAL,
                "professional_license", "MD-ELIG-3", null, null, null, null, null, null));
        ProfessionalCredential credential = doctorManagementService.attachCredential(doctor.doctorId(),
                new AttachCredentialCommand(ProfessionalCredential.TYPE_MEDICAL_LICENSE, "LIC-ELIG-3",
                        "State Board", null, java.time.LocalDate.of(2010, 1, 1),
                        java.time.LocalDate.of(2011, 1, 1)));

        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> doctorManagementService.verifyCredential(doctor.doctorId(), credential.credentialId()))
                .isInstanceOf(com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleConflictException.class);

        assertThat(doctorManagementService.isEligibleAsReferringDoctor(doctor.doctorId())).isFalse();
    }

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
}
