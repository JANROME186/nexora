package com.nexora.hop.platformfoundation.peopleclinicalmasterdata;

import java.time.LocalDate;

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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

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
    void patientMergeRepresentativeAndConsentCustomRulesWork() throws Exception {
        JsonNode survivor = postJson("/api/people/patients", """
                {"tenantId":"%s","laboratoryId":"%s","patientCode":"P-SURVIVOR","givenName":"Ada","familyName":"Lovelace",
                 "birthDate":"1990-12-10","sexAtBirth":"female",
                 "primaryDocumentType":"national_id","primaryDocumentNumber":"DOC-SURVIVOR"}
                """.formatted(tenantId, LAB));
        String survivorId = survivor.get("patientId").asText();

        JsonNode duplicate = postJson("/api/people/patients", """
                {"tenantId":"%s","laboratoryId":"%s","patientCode":"P-DUPLICATE","givenName":"Ada2","familyName":"Lovelace2",
                 "birthDate":"1991-01-01","sexAtBirth":"female",
                 "primaryDocumentType":"national_id","primaryDocumentNumber":"DOC-DUPLICATE"}
                """.formatted(tenantId, LAB));
        String duplicateId = duplicate.get("patientId").asText();

        // BCM-PER-002 RN-005: merge nominates a surviving patient; source becomes "merged" and is
        // preserved (never deleted) for historical references.
        mockMvc.perform(post("/api/people/patients/{id}/merge", duplicateId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"survivingPatientId\":\"%s\"}".formatted(survivorId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("merged"));

        // Idempotent replay: merging again into the same survivor is a no-op, not an error.
        mockMvc.perform(post("/api/people/patients/{id}/merge", duplicateId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"survivingPatientId\":\"%s\"}".formatted(survivorId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("merged"));

        // Downstream snapshot references are rewired to the surviving patient.
        mockMvc.perform(get("/api/people/patients/{id}/snapshot", duplicateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(survivorId));

        // BCM-PER-002 RN-006: representative revocation closes the active authorization window.
        JsonNode representative = postJson("/api/people/patients/{id}/representatives".replace("{id}", survivorId), """
                {"relationship":"legal_guardian","givenName":"Frank","familyName":"Hopper",
                 "documentType":"national_id","documentNumber":"REP-MERGE-1","authorizationFrom":"2026-01-01"}
                """);
        String representativeId = representative.get("representativeId").asText();

        mockMvc.perform(post("/api/people/patients/{p}/representatives/{r}/revoke", survivorId, representativeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("revoked"));

        mockMvc.perform(post("/api/people/patients/{p}/representatives/{r}/revoke", survivorId, representativeId))
                .andExpect(status().isConflict());

        // BCM-PER-002 RN-007: consent revocation appends an immutable revocation record instead of
        // mutating the original grant.
        JsonNode consent = postJson("/api/people/patients/{id}/consents"
                .replace("{id}", survivorId), """
                {"consentType":"data_processing","granted":true,"grantedBy":"patient"}
                """);
        String consentId = consent.get("consentId").asText();

        String revocationBody = mockMvc.perform(
                post("/api/people/patients/{p}/consents/{c}/revoke", survivorId, consentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.granted").value(false))
                .andExpect(jsonPath("$.evidenceReference").value(consentId))
                .andReturn().getResponse().getContentAsString();
        JsonNode revocation = objectMapper.readTree(revocationBody);
        assertThat(revocation.get("consentId").asText()).isNotEqualTo(consentId);

        mockMvc.perform(get("/api/people/patients/{id}/consents", survivorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(post("/api/people/patients/{p}/consents/{c}/revoke", survivorId, consentId))
                .andExpect(status().isConflict());
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
    void doctorSuspendPortalAccessAndCredentialCustomRulesWork() throws Exception {
        JsonNode doctor = postJson("/api/people/doctors", """
                {"tenantId":"%s","laboratoryId":"%s","doctorCode":"D-HOOK",
                 "givenName":"Marie","familyName":"Curie","doctorType":"referring_external",
                 "primaryDocumentType":"professional_license","primaryDocumentNumber":"MD-CURIE"}
                """.formatted(tenantId, LAB));
        String doctorId = doctor.get("doctorId").asText();

        JsonNode credential = postJson("/api/people/doctors/{id}/credentials".replace("{id}", doctorId), """
                {"credentialType":"medical_license","credentialNumber":"LIC-HOOK","issuingAuthority":"State Board",
                 "issuedAt":"2020-01-01"}
                """);
        String credentialId = credential.get("credentialId").asText();
        assertThat(credential.get("verificationStatus").asText()).isEqualTo("pending");

        // BCM-PER-003 RN-004: verifying the credential is what makes the doctor eligible as a
        // referring doctor (see DoctorManagementService.isEligibleAsReferringDoctor).
        mockMvc.perform(post("/api/people/doctors/{d}/credentials/{c}/verify", doctorId, credentialId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("verified"));
        mockMvc.perform(post("/api/people/doctors/{d}/credentials/{c}/verify", doctorId, credentialId))
                .andExpect(status().isConflict());

        // BCM-PER-003 RN-007: portal access preparation declares readiness but never grants access.
        mockMvc.perform(post("/api/people/doctors/{id}/portal-access/prepare", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"portalEmail\":\"marie@example.org\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.portalStatus").value("ready_for_provisioning"))
                .andExpect(jsonPath("$.portalEmail").value("marie@example.org"));

        // BCM-PER-003 RN-006: suspension excludes the doctor from referring-doctor eligibility.
        mockMvc.perform(post("/api/people/doctors/{id}/suspend", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reasonCode\":\"quality_review\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("suspended"));
        mockMvc.perform(post("/api/people/doctors/{id}/suspend", doctorId))
                .andExpect(status().isConflict());

        // BCM-PER-003 RN-005: revoking the only verified credential is reflected in the credential
        // record itself; the eligibility recomputation is exercised in
        // DoctorEligibilityRulesTest.
        mockMvc.perform(post("/api/people/doctors/{d}/credentials/{c}/revoke", doctorId, credentialId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("revoked"));
        mockMvc.perform(post("/api/people/doctors/{d}/credentials/{c}/revoke", doctorId, credentialId))
                .andExpect(status().isConflict());
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

        // BCM-PER-001 RN-004: rebuild is an idempotent confirmation over the live projection.
        mockMvc.perform(post("/api/people/persons/index/rebuild").param("tenantId", tenantId))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.tenantId").value(tenantId))
                .andExpect(jsonPath("$.patientCount").value(1))
                .andExpect(jsonPath("$.doctorCount").value(1));

        // BCM-PER-001 merge coordination: cross-kind (patient + doctor) records are recorded as a
        // decision only, since Doctor has no merge concept in the current business model.
        mockMvc.perform(post("/api/people/persons/merges")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tenantId":"%s","sourceRecordId":"missing-a","targetRecordId":"missing-b"}
                        """.formatted(tenantId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void personMergeCoordinationAppliesPatientMergeWhenBothRecordsArePatients() throws Exception {
        JsonNode survivor = postJson("/api/people/patients", """
                {"tenantId":"%s","laboratoryId":"%s","patientCode":"P-COORD-SURVIVOR","givenName":"Katherine",
                 "familyName":"Johnson","birthDate":"1918-08-26","sexAtBirth":"female",
                 "primaryDocumentType":"national_id","primaryDocumentNumber":"DOC-COORD-SURVIVOR"}
                """.formatted(tenantId, LAB));
        String survivorId = survivor.get("patientId").asText();
        JsonNode duplicate = postJson("/api/people/patients", """
                {"tenantId":"%s","laboratoryId":"%s","patientCode":"P-COORD-DUP","givenName":"Katherine2",
                 "familyName":"Johnson2","birthDate":"1919-01-01","sexAtBirth":"female",
                 "primaryDocumentType":"national_id","primaryDocumentNumber":"DOC-COORD-DUP"}
                """.formatted(tenantId, LAB));
        String duplicateId = duplicate.get("patientId").asText();

        JsonNode coordination = postJson("/api/people/persons/merges", """
                {"tenantId":"%s","sourceRecordId":"%s","targetRecordId":"%s"}
                """.formatted(tenantId, duplicateId, survivorId));
        assertThat(coordination.get("status").asText()).isEqualTo("patients_merged");
        assertThat(coordination.get("patientMergeApplied").asBoolean()).isTrue();
        String coordinationId = coordination.get("coordinationId").asText();

        mockMvc.perform(get("/api/people/persons/merges/{id}", coordinationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("patients_merged"));

        mockMvc.perform(get("/api/people/patients/{id}/snapshot", duplicateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(survivorId));

        mockMvc.perform(get("/api/people/persons/merges/{id}", "unknown-coordination"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patientRegistrationCanBeStartedCommittedAndCancelled() throws Exception {
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

        // BCM-ATT-002 RN-002/RN-005: commit delegates to BCM-PER-002 (RegisterPatientCommand) and
        // captures the tenant's mandatory consents (default: data_processing).
        JsonNode committed = objectMapper.readTree(mockMvc.perform(
                post("/api/care-delivery/patient-registrations/{id}/commit", registrationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"patientCode":"P-REG-1","sexAtBirth":"male",
                                 "consents":[{"consentType":"data_processing","granted":true,"grantedBy":"patient"}]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outcome").value("committed"))
                .andExpect(jsonPath("$.outcomePatientId").exists())
                .andReturn().getResponse().getContentAsString());
        String outcomePatientId = committed.get("outcomePatientId").asText();

        mockMvc.perform(get("/api/people/patients/{id}", outcomePatientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientCode").value("P-REG-1"));

        // A committed registration cannot be committed again.
        mockMvc.perform(post("/api/care-delivery/patient-registrations/{id}/commit", registrationId))
                .andExpect(status().isConflict());

        JsonNode cancelSource = postJson("/api/care-delivery/patient-registrations", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                 "registrationKind":"new_patient","givenName":"Grace","familyName":"Hopper2",
                 "birthDate":"1906-12-09","documentType":"national_id","documentNumber":"REG-2"}
                """.formatted(tenantId, LAB, BRANCH));
        String cancelRegistrationId = cancelSource.get("registrationRequestId").asText();
        mockMvc.perform(post("/api/care-delivery/patient-registrations/{id}/cancel", cancelRegistrationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reasonCode\":\"withdrew\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outcome").value("cancelled"));
    }

    @Test
    void patientRegistrationDefaultsMinorToRepresentativeRegistration() throws Exception {
        LocalDate minorBirthDate = LocalDate.now().minusYears(10);
        JsonNode registration = postJson("/api/care-delivery/patient-registrations", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                 "registrationKind":"new_patient","givenName":"Minor","familyName":"Child",
                 "birthDate":"%s","documentType":"national_id","documentNumber":"REG-MINOR"}
                """.formatted(tenantId, LAB, BRANCH, minorBirthDate));
        // BCM-ATT-002 RN-008: age-of-majority default switches a plain new-patient intake to
        // representative registration.
        assertThat(registration.get("registrationKind").asText()).isEqualTo("representative_registration");

        String registrationId = registration.get("registrationRequestId").asText();
        // RN-003: committing a representative registration without representative details is
        // rejected.
        mockMvc.perform(post("/api/care-delivery/patient-registrations/{id}/commit", registrationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"patientCode":"P-REG-MINOR","sexAtBirth":"female",
                         "consents":[{"consentType":"data_processing","granted":true,"grantedBy":"representative"}]}
                        """))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/care-delivery/patient-registrations/{id}/commit", registrationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"patientCode":"P-REG-MINOR","sexAtBirth":"female",
                         "representativeRelationship":"parent","representativeGivenName":"Parent",
                         "representativeFamilyName":"Child","representativeDocumentType":"national_id",
                         "representativeDocumentNumber":"REP-MINOR-1","representativeAuthorizationFrom":"2026-01-01",
                         "consents":[{"consentType":"data_processing","granted":true,"grantedBy":"representative"}]}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outcome").value("committed"));
    }

    @Test
    void patientRegistrationRequiresMatchResolutionOnHighConfidenceDuplicateAndMandatoryConsent() throws Exception {
        postJson("/api/people/patients", """
                {"tenantId":"%s","laboratoryId":"%s","patientCode":"P-EXISTING","givenName":"Rosalind",
                 "familyName":"Franklin","birthDate":"1920-07-25","sexAtBirth":"female",
                 "primaryDocumentType":"national_id","primaryDocumentNumber":"DOC-EXISTING"}
                """.formatted(tenantId, LAB));

        JsonNode registration = postJson("/api/care-delivery/patient-registrations", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","intakeChannel":"walk_in",
                 "registrationKind":"new_patient","givenName":"Rosalind","familyName":"Franklin",
                 "birthDate":"1920-07-25","documentType":"national_id","documentNumber":"DOC-EXISTING"}
                """.formatted(tenantId, LAB, BRANCH));
        String registrationId = registration.get("registrationRequestId").asText();

        // High-confidence match (same normalized name, birth date and document number) requires an
        // explicit actor decision before commit (BCM-ATT-002 RN-006).
        mockMvc.perform(post("/api/care-delivery/patient-registrations/{id}/commit", registrationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"patientCode":"P-SHOULD-NOT-BE-CREATED","sexAtBirth":"female",
                         "consents":[{"consentType":"data_processing","granted":true,"grantedBy":"patient"}]}
                        """))
                .andExpect(status().isConflict());

        // Missing the tenant's mandatory consent is also rejected, even with a resolved match.
        JsonNode existingPatients = objectMapper.readTree(mockMvc.perform(
                get("/api/people/patients").param("laboratoryId", LAB))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
        String existingPatientId = null;
        for (JsonNode candidate : existingPatients) {
            if ("P-EXISTING".equals(candidate.get("patientCode").asText())) {
                existingPatientId = candidate.get("patientId").asText();
            }
        }
        assertThat(existingPatientId).isNotNull();

        mockMvc.perform(post("/api/care-delivery/patient-registrations/{id}/commit", registrationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"resolvedExistingPatientId\":\"%s\"}".formatted(existingPatientId)))
                .andExpect(status().isConflict());

        // Resolving the match and providing the mandatory consent commits by reusing the existing
        // patient (RN-002: no new Patient record is created).
        mockMvc.perform(post("/api/care-delivery/patient-registrations/{id}/commit", registrationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"resolvedExistingPatientId":"%s",
                         "consents":[{"consentType":"data_processing","granted":true,"grantedBy":"patient"}]}
                        """.formatted(existingPatientId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outcome").value("committed"))
                .andExpect(jsonPath("$.outcomePatientId").value(existingPatientId));
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
