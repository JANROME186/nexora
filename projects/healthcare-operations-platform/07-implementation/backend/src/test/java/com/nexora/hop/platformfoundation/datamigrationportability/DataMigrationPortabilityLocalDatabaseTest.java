package com.nexora.hop.platformfoundation.datamigrationportability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class DataMigrationPortabilityLocalDatabaseTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void dataMigrationPortabilitySchemaIsInitializedInPostgres() {
        Integer tableCount = jdbcTemplate.queryForObject("""
                select count(*)
                  from information_schema.tables
                 where table_schema = 'data_migration_portability'
                   and table_name in (
                       'migration_jobs', 'import_batches', 'mapping_templates',
                       'import_validation_reports', 'reconciliation_reports', 'import_executions')
                """, Integer.class);

        assertThat(tableCount).isEqualTo(6);
    }

    @Test
    void migrationLifecycleRoundTripsAgainstRealPostgres() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"JDBC Migration Tenant " + token + "\"}");
        String tenantId = tenant.get("tenantId").asText();

        JsonNode job = postJson("/api/platform/migration/jobs", """
                {"tenantId":"%s","laboratoryId":"lab-jdbc","sourceSystemName":"LegacyLIS","actorId":"lead"}
                """.formatted(tenantId));
        String migrationJobId = job.get("migrationJobId").asText();

        String csvContent = "id,name\n1,Alice\n";
        String checksum = HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(csvContent.getBytes(StandardCharsets.UTF_8)));
        String manifest = """
                source_system_name: LegacyLIS
                exporting_organization: Legacy Labs
                export_datetime: "2026-01-01T00:00:00Z"
                export_timezone: UTC
                exported_by: exporter@legacy.com
                contact_email: exporter@legacy.com
                files: [patients.csv]
                entity_counts: {patients.csv: 1}
                checksum_algorithm: sha256
                checksums: {patients.csv: "%s"}
                declared_formats: [csv]
                declared_encoding: UTF-8
                """.formatted(checksum);

        MockMultipartFile manifestPart = new MockMultipartFile(
                "manifest", "manifest.yaml", MediaType.TEXT_PLAIN_VALUE, manifest.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile packagePart = new MockMultipartFile(
                "package", "patients.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        MvcResult receiveResult = mockMvc.perform(
                        multipart("/api/platform/migration/jobs/{id}/import-batches", migrationJobId)
                                .file(manifestPart).file(packagePart).param("zipBundle", "false")
                                .param("actorId", "lead"))
                .andExpect(status().isCreated())
                .andReturn();
        String importBatchId = objectMapper.readTree(receiveResult.getResponse().getContentAsString())
                .get("importBatchId").asText();

        mockMvc.perform(post("/api/platform/migration/import-batches/{id}/dry-run", importBatchId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"lead\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passed").value(true));

        mockMvc.perform(post("/api/platform/migration/import-batches/{id}/approve", importBatchId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"lead\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/platform/migration/import-batches/{id}/commit", importBatchId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"lead\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("in_progress"));

        mockMvc.perform(get("/api/platform/migration/jobs/{id}/reconciliation", migrationJobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phase").value("pre_import"));

        mockMvc.perform(post("/api/platform/migration/jobs/{id}/retry", migrationJobId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"lead\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attemptNumber").value(2));
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
