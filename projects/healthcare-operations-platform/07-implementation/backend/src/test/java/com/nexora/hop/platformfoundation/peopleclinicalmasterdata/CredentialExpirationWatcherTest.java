package com.nexora.hop.platformfoundation.peopleclinicalmasterdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.AttachCredentialCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.CredentialExpirationWatcher;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.DoctorManagementService;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.RegisterDoctorCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.Doctor;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.DoctorRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.ProfessionalCredential;

/**
 * BCM-PER-003 RN-005 {@code scheduler:credential_expiration_watcher} coverage. Closes technical
 * debt TD-BE-007: a verified credential must be proactively transitioned to
 * {@link ProfessionalCredential#STATUS_EXPIRED} once its {@code expiresAt} date has passed, without
 * requiring a reactive verification attempt.
 */
@AutoConfigureMockMvc
@SpringBootTest
class CredentialExpirationWatcherTest {

    private static final String LAB = "lab-1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorManagementService doctorManagementService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private CredentialExpirationWatcher watcher;

    private String tenantId;

    @BeforeEach
    void createTenant() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Credential Watcher Tenant\"}");
        tenantId = tenant.get("tenantId").asText();
    }

    @Test
    void watcherTransitionsAnExpiredVerifiedCredentialAndLeavesAFutureCredentialUntouched() {
        Doctor doctor = doctorManagementService.register(new RegisterDoctorCommand(
                tenantId, LAB, "D-WATCH-1", "Marie", null, "Curie", null, Doctor.TYPE_REFERRING_EXTERNAL,
                "professional_license", "MD-WATCH-1", null, null, null, null, null, null));

        // Simulates a credential that was verified while still valid and has since lapsed: written
        // directly as STATUS_VERIFIED with a past expiresAt, since the live verifyCredential path
        // correctly refuses to verify an already-expired credential.
        ProfessionalCredential expiring = new ProfessionalCredential(UUID.randomUUID().toString(), doctor.doctorId(),
                ProfessionalCredential.TYPE_MEDICAL_LICENSE, "LIC-WATCH-EXPIRING", "State Board", null,
                LocalDate.now().minusYears(2), LocalDate.now().minusDays(1), ProfessionalCredential.STATUS_VERIFIED,
                Instant.now());
        doctorRepository.saveCredential(expiring);

        ProfessionalCredential future = doctorManagementService.attachCredential(doctor.doctorId(),
                new AttachCredentialCommand(ProfessionalCredential.TYPE_MEDICAL_LICENSE, "LIC-WATCH-FUTURE",
                        "State Board", null, LocalDate.now().minusYears(2), LocalDate.now().plusYears(1)));
        doctorManagementService.verifyCredential(doctor.doctorId(), future.credentialId());

        int transitioned = watcher.runOnce();
        assertThat(transitioned).isEqualTo(1);

        List<ProfessionalCredential> credentials = doctorManagementService.listCredentials(doctor.doctorId());
        assertThat(credentials)
                .filteredOn(credential -> credential.credentialId().equals(expiring.credentialId()))
                .extracting(ProfessionalCredential::verificationStatus)
                .containsExactly(ProfessionalCredential.STATUS_EXPIRED);
        assertThat(credentials)
                .filteredOn(credential -> credential.credentialId().equals(future.credentialId()))
                .extracting(ProfessionalCredential::verificationStatus)
                .containsExactly(ProfessionalCredential.STATUS_VERIFIED);

        // Re-running is idempotent: the same credential is not transitioned twice.
        assertThat(watcher.runOnce()).isZero();
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
