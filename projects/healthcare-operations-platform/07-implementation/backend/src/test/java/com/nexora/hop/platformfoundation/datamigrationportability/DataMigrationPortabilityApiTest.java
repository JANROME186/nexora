package com.nexora.hop.platformfoundation.datamigrationportability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
class DataMigrationPortabilityApiTest {

    private static final String CSV_CONTENT = "id,name\n1,Alice\n2,Bob\n";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tenantId;

    @BeforeEach
    void createTenant() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Migration Tenant " + token + "\"}");
        tenantId = tenant.get("tenantId").asText();
    }

    @Test
    void fullMigrationLifecycleFromPackageReceiptThroughCommitAndRetry() throws Exception {
        String migrationJobId = createJob();
        JsonNode batch = receivePackage(migrationJobId);
        String importBatchId = batch.get("importBatchId").asText();
        assertThat(batch.get("entityCounts").get("patients.csv").asInt()).isEqualTo(2);

        JsonNode dryRun = postJson("/api/platform/migration/import-batches/" + importBatchId + "/dry-run",
                "{\"actorId\":\"migration-lead\"}");
        assertThat(dryRun.get("passed").asBoolean()).isTrue();

        mockMvc.perform(get("/api/platform/migration/import-batches/{id}/dry-run", importBatchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passed").value(true));

        mockMvc.perform(post("/api/platform/migration/import-batches/{id}/approve", importBatchId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"migration-lead\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("approved"));

        JsonNode execution = postJson("/api/platform/migration/import-batches/" + importBatchId + "/commit",
                "{\"actorId\":\"migration-lead\"}");
        assertThat(execution.get("status").asText()).isEqualTo("in_progress");
        assertThat(execution.get("domainCommandsInvoked")).isEmpty();

        mockMvc.perform(get("/api/platform/migration/jobs/{id}/reconciliation", migrationJobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phase").value("pre_import"))
                .andExpect(jsonPath("$[0].importedCounts['patients.csv']").value(2));

        JsonNode retried = postJson("/api/platform/migration/jobs/" + migrationJobId + "/retry",
                "{\"actorId\":\"migration-lead\"}");
        assertThat(retried.get("attemptNumber").asInt()).isEqualTo(2);
        assertThat(retried.get("status").asText()).isEqualTo("in_progress");
    }

    @Test
    void approvalIsRejectedWithoutAPassedDryRunReport() throws Exception {
        String migrationJobId = createJob();
        String importBatchId = receivePackage(migrationJobId).get("importBatchId").asText();

        mockMvc.perform(post("/api/platform/migration/import-batches/{id}/approve", importBatchId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"migration-lead\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("MIGRATION_DRY_RUN_NOT_PASSED"));
    }

    @Test
    void receiveImportPackageRejectsChecksumMismatch() throws Exception {
        String migrationJobId = createJob();
        String manifest = manifestYaml("wrong-checksum-000000000000000000000000000000000000000000000000000000000000");

        MockMultipartFile manifestPart = new MockMultipartFile(
                "manifest", "manifest.yaml", MediaType.TEXT_PLAIN_VALUE, manifest.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile packagePart = new MockMultipartFile(
                "package", "patients.csv", "text/csv", CSV_CONTENT.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/platform/migration/jobs/{id}/import-batches", migrationJobId)
                        .file(manifestPart).file(packagePart).param("zipBundle", "false")
                        .param("actorId", "migration-lead"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MIGRATION_MANIFEST_INVALID_OR_MISSING"));
    }

    @Test
    void migrationJobsReturnStructuredErrorForUnknownJob() throws Exception {
        mockMvc.perform(get("/api/platform/migration/jobs/{id}", "missing-job"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MIGRATION_JOB_NOT_FOUND"));
    }

    private String createJob() throws Exception {
        JsonNode job = postJson("/api/platform/migration/jobs", """
                {"tenantId":"%s","laboratoryId":"lab-1","sourceSystemName":"LegacyLIS","actorId":"migration-lead"}
                """.formatted(tenantId));
        return job.get("migrationJobId").asText();
    }

    private JsonNode receivePackage(String migrationJobId) throws Exception {
        String checksum = sha256Hex(CSV_CONTENT);
        MockMultipartFile manifestPart = new MockMultipartFile(
                "manifest", "manifest.yaml", MediaType.TEXT_PLAIN_VALUE,
                manifestYaml(checksum).getBytes(StandardCharsets.UTF_8));
        MockMultipartFile packagePart = new MockMultipartFile(
                "package", "patients.csv", "text/csv", CSV_CONTENT.getBytes(StandardCharsets.UTF_8));

        MvcResult result = mockMvc.perform(multipart("/api/platform/migration/jobs/{id}/import-batches", migrationJobId)
                        .file(manifestPart).file(packagePart).param("zipBundle", "false")
                        .param("actorId", "migration-lead"))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private static String manifestYaml(String checksum) {
        return """
                source_system_name: LegacyLIS
                exporting_organization: Legacy Labs
                export_datetime: "%s"
                export_timezone: UTC
                exported_by: exporter@legacy.com
                contact_email: exporter@legacy.com
                files: [patients.csv]
                entity_counts: {patients.csv: 2}
                checksum_algorithm: sha256
                checksums: {patients.csv: "%s"}
                declared_formats: [csv]
                declared_encoding: UTF-8
                """.formatted(Instant.parse("2026-01-01T00:00:00Z"), checksum);
    }

    private static String sha256Hex(String content) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest(content.getBytes(StandardCharsets.UTF_8)));
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
