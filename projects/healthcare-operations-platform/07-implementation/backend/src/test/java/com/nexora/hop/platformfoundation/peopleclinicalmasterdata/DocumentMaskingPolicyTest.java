package com.nexora.hop.platformfoundation.peopleclinicalmasterdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.DoctorManagementService;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.RegisterDoctorCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.Doctor;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.DoctorSnapshot;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.PatientManagementService;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.RegisterPatientCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.Patient;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientSnapshot;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application.TenantPeoplePolicyStore;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument.DocumentNumberMaskingPolicy;

/**
 * BCM-PER-002 RN-008 / BCM-PER-003 RN-008 coverage. Closes technical debt TD-BE-008: document
 * number masking is a tenant-configurable policy (visible-character count and mask character)
 * rather than a single fixed algorithm, and the platform default reproduces the original fixed
 * behavior for tenants that never override it.
 */
@AutoConfigureMockMvc
@SpringBootTest
class DocumentMaskingPolicyTest {

    private static final String LAB = "lab-1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorManagementService doctorManagementService;

    @Autowired
    private PatientManagementService patientManagementService;

    @Autowired
    private TenantPeoplePolicyStore policyStore;

    private String tenantId;

    @BeforeEach
    void createTenant() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Masking Policy Tenant\"}");
        tenantId = tenant.get("tenantId").asText();
    }

    @Test
    void defaultPolicyMasksAllButTheLastFourCharacters() {
        PersonDocument document = new PersonDocument(
                PersonDocument.TYPE_NATIONAL_ID, "ABCDEF1234", null, null, null);
        assertThat(document.maskedNumber()).isEqualTo("******1234");
        assertThat(document.maskedNumber(DocumentNumberMaskingPolicy.DEFAULT)).isEqualTo("******1234");
    }

    @Test
    void shortDocumentNumberIsMaskedInFullRatherThanExposedByAPermissivePolicy() {
        PersonDocument document = new PersonDocument(PersonDocument.TYPE_NATIONAL_ID, "AB", null, null, null);
        assertThat(document.maskedNumber(new DocumentNumberMaskingPolicy(4, '*'))).isEqualTo("**");
    }

    @Test
    void negativeVisibleCharacterCountIsRejected() {
        assertThatThrownBy(() -> new DocumentNumberMaskingPolicy(-1, '*'))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tenantOverridePolicyChangesVisibleCharactersAndMaskCharacterOnDoctorAndPatientSnapshots() {
        policyStore.setDocumentMaskingPolicy(tenantId, new DocumentNumberMaskingPolicy(2, '#'));

        Doctor doctor = doctorManagementService.register(new RegisterDoctorCommand(
                tenantId, LAB, "D-MASK-1", "Ada", null, "Lovelace", null, Doctor.TYPE_REFERRING_EXTERNAL,
                "professional_license", "MD-MASK-1234", null, null, null, null, null, null));
        DoctorSnapshot doctorSnapshot = doctorManagementService.snapshot(doctor.doctorId());
        assertThat(doctorSnapshot.primaryDocumentNumberMasked()).isEqualTo("##########34");

        Patient patient = patientManagementService.register(new RegisterPatientCommand(
                tenantId, LAB, "P-MASK-1", "Ada", null, "Lovelace", null, null,
                java.time.LocalDate.of(1990, 1, 1), Patient.SEX_FEMALE, PersonDocument.TYPE_NATIONAL_ID,
                "DOC-MASK-1234", null, null, null, null, null, null, null, null, null));
        PatientSnapshot patientSnapshot = patientManagementService.snapshot(patient.patientId());
        assertThat(patientSnapshot.primaryDocumentNumberMasked()).isEqualTo("###########34");
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
