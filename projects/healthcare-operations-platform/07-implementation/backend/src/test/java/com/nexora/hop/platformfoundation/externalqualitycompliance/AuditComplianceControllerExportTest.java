package com.nexora.hop.platformfoundation.externalqualitycompliance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.hop.platformfoundation.auditcompliance.adapter.in.web.AuditComplianceController;
import com.nexora.hop.platformfoundation.auditcompliance.application.AuditComplianceService;
import com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEventRepository;
import com.nexora.hop.platformfoundation.documentmanagement.application.DocumentManagementService;
import com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentStoragePort;
import com.nexora.hop.platformfoundation.documentmanagement.domain.StorageReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuditComplianceControllerExportTest {

    private MockMvc mockMvc;
    private AuditComplianceService auditService;
    private DocumentManagementService documentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        AuditEventRepository eventRepo = new InMemoryAuditEventRepository();
        DocumentStoragePort storagePort = new DocumentStoragePort() {
            @Override
            public StorageReference putDocument(byte[] bytes, String contentType) {
                return new StorageReference(StorageReference.StorageProvider.LOCAL_FILESYSTEM, "key-" + UUID.randomUUID(), LocalDateTime.now());
            }

            @Override
            public byte[] getDocument(StorageReference reference) {
                return new byte[0];
            }

            @Override
            public void deleteDocument(StorageReference reference) {
            }
        };

        documentService = new DocumentManagementService(storagePort);
        auditService = new AuditComplianceService(eventRepo, documentService);

        mockMvc = MockMvcBuilders.standaloneSetup(new AuditComplianceController(auditService)).build();
    }

    @Test
    void testAuditSearchAndExportFulfillingTdBe016() throws Exception {
        auditService.recordSystemEvent("tenant-1", "QUALITY_CAPA_CREATED", "CAPA", "CAPA-101", "{\"correlationId\":\"CORR-99\"}");
        auditService.recordSystemEvent("tenant-1", "QUALITY_AUDIT_CLOSED", "AUDIT", "AUD-202", "{\"correlationId\":\"CORR-99\"}");

        mockMvc.perform(get("/api/audit/events")
                        .param("complianceCorrelationId", "CORR-99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        AuditComplianceController.ExportAuditEventsRequest exportReq =
                new AuditComplianceController.ExportAuditEventsRequest(null, "CORR-99", null, null, "csv");

        mockMvc.perform(post("/api/audit/events/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exportReq)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.exportId").exists())
                .andExpect(jsonPath("$.recordCount").value(2))
                .andExpect(jsonPath("$.storedDocumentId").exists());
    }

    private static class InMemoryAuditEventRepository implements AuditEventRepository {
        private final java.util.List<com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEvent> list = new java.util.concurrent.CopyOnWriteArrayList<>();

        @Override
        public com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEvent append(com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEvent event) {
            list.add(event);
            return event;
        }

        @Override
        public java.util.List<com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEvent> search(String tenantId, String subjectId) {
            return list.stream()
                    .filter(e -> tenantId == null || tenantId.equals(e.tenantId()))
                    .filter(e -> subjectId == null || subjectId.equals(e.subjectId()))
                    .toList();
        }
    }
}
