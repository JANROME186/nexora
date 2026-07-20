package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.application;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.ReferringDoctorAuthorizationPort;
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
        ReferringDoctorAuthorizationPort authorizationPort = mock(ReferringDoctorAuthorizationPort.class);
        ResultHistoryService service = new ResultHistoryService(repository, authorizationPort);
        PatientId patientId = new PatientId("P-123");

        PatientResultHistoryView mockView = new PatientResultHistoryView(patientId, java.util.List.of());
        when(repository.findByPatientId(patientId)).thenReturn(Optional.of(mockView));

        PatientResultHistoryView result = service.getHistoryForPatient("P-123");
        assertEquals(patientId, result.patientId());
        verify(repository, times(1)).findByPatientId(patientId);
        verifyNoInteractions(authorizationPort);
    }

    @Test
    void testGetHistoryForPatientWhenNotFound() {
        PatientResultHistoryRepository repository = mock(PatientResultHistoryRepository.class);
        ReferringDoctorAuthorizationPort authorizationPort = mock(ReferringDoctorAuthorizationPort.class);
        ResultHistoryService service = new ResultHistoryService(repository, authorizationPort);
        PatientId patientId = new PatientId("P-456");

        when(repository.findByPatientId(patientId)).thenReturn(Optional.empty());

        PatientResultHistoryView result = service.getHistoryForPatient("P-456");
        assertEquals(patientId, result.patientId());
        assertTrue(result.entries().isEmpty());
    }

    @Test
    void referringDoctorWithConfirmedReferralReceivesHistory() {
        PatientResultHistoryRepository repository = mock(PatientResultHistoryRepository.class);
        ReferringDoctorAuthorizationPort authorizationPort = mock(ReferringDoctorAuthorizationPort.class);
        ResultHistoryService service = new ResultHistoryService(repository, authorizationPort);
        PatientId patientId = new PatientId("P-789");
        PatientResultHistoryView mockView = new PatientResultHistoryView(patientId, java.util.List.of());

        when(authorizationPort.isPatientReferredByDoctor("tenant-a", "Doctor-01", "P-789")).thenReturn(true);
        when(repository.findByPatientId(patientId)).thenReturn(Optional.of(mockView));

        PatientResultHistoryView result =
                service.getHistoryForPatient("P-789", "tenant-a", "REFERRING_DOCTOR", "Doctor-01");

        assertEquals(patientId, result.patientId());
    }

    @Test
    void referringDoctorWithoutReferralIsDenied() {
        PatientResultHistoryRepository repository = mock(PatientResultHistoryRepository.class);
        ReferringDoctorAuthorizationPort authorizationPort = mock(ReferringDoctorAuthorizationPort.class);
        ResultHistoryService service = new ResultHistoryService(repository, authorizationPort);

        when(authorizationPort.isPatientReferredByDoctor("tenant-a", "Doctor-01", "P-999")).thenReturn(false);

        assertThrows(ResultHistoryAccessDeniedException.class, () ->
                service.getHistoryForPatient("P-999", "tenant-a", "REFERRING_DOCTOR", "Doctor-01"));
        verifyNoInteractions(repository);
    }

    @Test
    void nonDoctorCallerRoleIsNotSubjectToReferralCheck() {
        PatientResultHistoryRepository repository = mock(PatientResultHistoryRepository.class);
        ReferringDoctorAuthorizationPort authorizationPort = mock(ReferringDoctorAuthorizationPort.class);
        ResultHistoryService service = new ResultHistoryService(repository, authorizationPort);
        PatientId patientId = new PatientId("P-321");
        when(repository.findByPatientId(patientId)).thenReturn(Optional.empty());

        PatientResultHistoryView result =
                service.getHistoryForPatient("P-321", "tenant-a", "PATIENT", "P-321");

        assertEquals(patientId, result.patientId());
        verifyNoInteractions(authorizationPort);
    }
}
