package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.application;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.domain.PatientResultHistoryRepository;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.domain.PatientResultHistoryView;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResultHistoryServiceTest {

    @Test
    void testGetHistoryForPatientWhenFound() {
        PatientResultHistoryRepository repository = mock(PatientResultHistoryRepository.class);
        ResultHistoryService service = new ResultHistoryService(repository);
        PatientId patientId = new PatientId("P-123");
        
        PatientResultHistoryView mockView = new PatientResultHistoryView(patientId, java.util.List.of());
        when(repository.findByPatientId(patientId)).thenReturn(Optional.of(mockView));

        PatientResultHistoryView result = service.getHistoryForPatient("P-123");
        assertEquals(patientId, result.patientId());
        verify(repository, times(1)).findByPatientId(patientId);
    }

    @Test
    void testGetHistoryForPatientWhenNotFound() {
        PatientResultHistoryRepository repository = mock(PatientResultHistoryRepository.class);
        ResultHistoryService service = new ResultHistoryService(repository);
        PatientId patientId = new PatientId("P-456");
        
        when(repository.findByPatientId(patientId)).thenReturn(Optional.empty());

        PatientResultHistoryView result = service.getHistoryForPatient("P-456");
        assertEquals(patientId, result.patientId());
        assertTrue(result.entries().isEmpty());
    }
}
