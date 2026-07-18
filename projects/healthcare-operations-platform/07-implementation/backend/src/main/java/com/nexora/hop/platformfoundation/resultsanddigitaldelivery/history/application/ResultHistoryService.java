package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.application;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.domain.PatientResultHistoryRepository;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.domain.PatientResultHistoryView;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResultHistoryService {

    private final PatientResultHistoryRepository repository;

    public ResultHistoryService(PatientResultHistoryRepository repository) {
        this.repository = repository;
    }

    public PatientResultHistoryView getHistoryForPatient(String patientIdStr) {
        PatientId patientId = new PatientId(patientIdStr);
        Optional<PatientResultHistoryView> view = repository.findByPatientId(patientId);
        return view.orElse(new PatientResultHistoryView(patientId, java.util.List.of()));
    }
}
