package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import java.util.Optional;

public interface PatientResultHistoryRepository {
    Optional<PatientResultHistoryView> findByPatientId(PatientId patientId);
    void save(PatientResultHistoryView view);
}
