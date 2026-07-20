package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.application;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.domain.PatientResultHistoryView;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ResultHistoryControllerTest {

    @Test
    void testGetPatientHistory() {
        ResultHistoryService service = mock(ResultHistoryService.class);
        ResultHistoryController controller = new ResultHistoryController(service);

        PatientId patientId = new PatientId("P-999");
        PatientResultHistoryView mockView = new PatientResultHistoryView(patientId, List.of());
        when(service.getHistoryForPatient("P-999", null, null, null)).thenReturn(mockView);

        ResponseEntity<PatientResultHistoryView> response =
                controller.getPatientHistory("P-999", null, null, null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockView, response.getBody());
    }

    @Test
    void testGetPatientHistoryForReferringDoctor() {
        ResultHistoryService service = mock(ResultHistoryService.class);
        ResultHistoryController controller = new ResultHistoryController(service);

        PatientId patientId = new PatientId("P-321");
        PatientResultHistoryView mockView = new PatientResultHistoryView(patientId, List.of());
        when(service.getHistoryForPatient("P-321", "tenant-a", "REFERRING_DOCTOR", "Doctor-01"))
                .thenReturn(mockView);

        ResponseEntity<PatientResultHistoryView> response =
                controller.getPatientHistory("P-321", "tenant-a", "REFERRING_DOCTOR", "Doctor-01");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockView, response.getBody());
        verify(service).getHistoryForPatient("P-321", "tenant-a", "REFERRING_DOCTOR", "Doctor-01");
    }
}
