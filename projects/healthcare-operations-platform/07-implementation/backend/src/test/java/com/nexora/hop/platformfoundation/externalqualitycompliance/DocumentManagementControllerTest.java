package com.nexora.hop.platformfoundation.externalqualitycompliance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.hop.platformfoundation.documentmanagement.adapter.in.web.DocumentManagementController;
import com.nexora.hop.platformfoundation.documentmanagement.application.DocumentManagementService;
import com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentStoragePort;
import com.nexora.hop.platformfoundation.documentmanagement.domain.StorageReference;
import com.nexora.hop.platformfoundation.sharedkernel.security.CurrentTenantContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DocumentManagementControllerTest {

    private MockMvc mockMvc;
    private DocumentManagementService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
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

        service = new DocumentManagementService(storagePort);
        mockMvc = MockMvcBuilders.standaloneSetup(new DocumentManagementController(service)).build();
    }

    @AfterEach
    void clearTenantContext() {
        CurrentTenantContext.clear();
    }

    @Test
    void uploadDocumentResolvesTenantFromTheAuthenticatedRequestContext_TD_IAM_004() throws Exception {
        CurrentTenantContext.set("tenant-from-authenticated-request");

        MockMultipartFile file = new MockMultipartFile(
                "file", "evidence.pdf", "application/pdf", "PDF Evidence Content".getBytes());

        String uploadResponse = mockMvc.perform(multipart("/api/documents")
                        .file(file)
                        .param("ownerCapability", "BCM-QLT-002")
                        .param("ownerReferenceId", UUID.randomUUID().toString())
                        .param("complianceCategory", "QUALITY_EVIDENCE"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String docId = objectMapper.readTree(uploadResponse).get("documentId").asText();

        org.assertj.core.api.Assertions.assertThat(
                        service.getDocumentRecord(UUID.fromString(docId)).orElseThrow()
                                .metadata().getTenantId().value())
                .isEqualTo("tenant-from-authenticated-request");
    }

    @Test
    void testDocumentUploadMetadataDownloadAndLegalHold() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "evidence.pdf",
                "application/pdf",
                "PDF Evidence Content".getBytes()
        );

        String uploadResponse = mockMvc.perform(multipart("/api/documents")
                        .file(file)
                        .param("ownerCapability", "BCM-QLT-002")
                        .param("ownerReferenceId", UUID.randomUUID().toString())
                        .param("complianceCategory", "QUALITY_EVIDENCE"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerCapability").value("BCM-QLT-002"))
                .andExpect(jsonPath("$.complianceCategory").value("QUALITY_EVIDENCE"))
                .andExpect(jsonPath("$.legalHold").value(false))
                .andReturn().getResponse().getContentAsString();

        String docId = objectMapper.readTree(uploadResponse).get("documentId").asText();

        mockMvc.perform(get("/api/documents/" + docId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentId").value(docId));

        mockMvc.perform(get("/api/documents/" + docId + "/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"document-" + docId + "\""));

        DocumentManagementController.UpdateLegalHoldRequest holdReq =
                new DocumentManagementController.UpdateLegalHoldRequest(true);

        mockMvc.perform(put("/api/documents/" + docId + "/legal-hold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(holdReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legalHold").value(true));

        DocumentManagementController.CreateEvidencePackageRequest pkgReq =
                new DocumentManagementController.CreateEvidencePackageRequest("Annual Quality Evidence Bundle", List.of(UUID.fromString(docId)));

        mockMvc.perform(post("/api/documents/evidence-package")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pkgReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Annual Quality Evidence Bundle"))
                .andExpect(jsonPath("$.documentCount").value(1));
    }
}
