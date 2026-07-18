package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.reportgeneration.application;

import com.nexora.hop.platformfoundation.documentmanagement.application.DocumentManagementService;
import com.nexora.hop.platformfoundation.documentmanagement.domain.StorageReference;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResult;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResultsRepository;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ResultStatus;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.reportgeneration.adapter.out.memory.InMemoryGeneratedResultReportRepository;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.reportgeneration.domain.GeneratedResultReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResultReportServiceTest {

    private InMemoryGeneratedResultReportRepository repository;
    private LaboratoryResultsRepository laboratoryResultsRepository;
    private DocumentManagementService documentManagementService;
    private ResultReportService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryGeneratedResultReportRepository();
        laboratoryResultsRepository = mock(LaboratoryResultsRepository.class);

        com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentStoragePort storagePort =
                mock(com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentStoragePort.class);
        when(storagePort.putDocument(any(), any())).thenReturn(
                new StorageReference(StorageReference.StorageProvider.LOCAL_FILESYSTEM, "key", LocalDateTime.now()));
        documentManagementService = new DocumentManagementService(storagePort);

        service = new ResultReportService(repository, laboratoryResultsRepository, documentManagementService);
    }

    private LaboratoryResult releasedResult() {
        LaboratoryResult result = mock(LaboratoryResult.class);
        when(result.resultId()).thenReturn("r1");
        when(result.tenantId()).thenReturn("t1");
        when(result.laboratoryId()).thenReturn("lab-1");
        when(result.status()).thenReturn(ResultStatus.released);
        return result;
    }

    @Test
    void shouldRegenerateReportForReleasedResult() {
        LaboratoryResult result = releasedResult();
        when(laboratoryResultsRepository.findById("r1", "t1")).thenReturn(Optional.of(result));

        GeneratedResultReport report = service.regenerateReport("r1", "t1", "actor-1");

        assertNotNull(report);
        assertEquals(GeneratedResultReport.Status.GENERATED, report.getStatus());
        assertEquals(1, report.getVersion());
        assertNotNull(report.getStoredDocumentId());
        assertNotNull(report.getIntegrityChecksum());
        assertEquals("actor-1", report.getAudit().createdBy());

        List<GeneratedResultReport> listed = service.listReports("r1", "t1");
        assertEquals(1, listed.size());
    }

    @Test
    void shouldSupersedePreviousReportOnRegeneration() {
        LaboratoryResult result = releasedResult();
        when(laboratoryResultsRepository.findById("r1", "t1")).thenReturn(Optional.of(result));

        GeneratedResultReport first = service.regenerateReport("r1", "t1", "actor-1");
        GeneratedResultReport second = service.regenerateReport("r1", "t1", "actor-1");

        assertEquals(2, second.getVersion());
        assertEquals(GeneratedResultReport.Status.GENERATED, second.getStatus());

        GeneratedResultReport reloadedFirst = repository.findById(first.getReportId()).orElseThrow();
        assertEquals(GeneratedResultReport.Status.SUPERSEDED, reloadedFirst.getStatus());
    }

    @Test
    void shouldRejectRegenerationWhenResultNotReleased() {
        LaboratoryResult result = mock(LaboratoryResult.class);
        when(result.status()).thenReturn(ResultStatus.captured);
        when(laboratoryResultsRepository.findById("r1", "t1")).thenReturn(Optional.of(result));

        assertThrows(IllegalStateException.class, () -> service.regenerateReport("r1", "t1", "actor-1"));
        assertTrue(service.listReports("r1", "t1").isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoReportsExist() {
        assertTrue(service.listReports("unknown", "t1").isEmpty());
    }
}
