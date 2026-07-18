package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.infrastructure;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.domain.PatientResultHistoryRepository;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.domain.PatientResultHistoryView;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPatientResultHistoryRepository implements PatientResultHistoryRepository {
    private final Map<PatientId, PatientResultHistoryView> store = new ConcurrentHashMap<>();

    public InMemoryPatientResultHistoryRepository() {
        // Initialize with fixture data for Patient-01 (mock patient)
        PatientId mockPatient = new PatientId("Patient-01");
        PatientResultHistoryView mockView = new PatientResultHistoryView(
                mockPatient,
                List.of(
                        new PatientResultHistoryView.ResultHistoryEntry(
                                new ResultId("RES-001"),
                                "Glucose",
                                "95.5",
                                "70-100 mg/dL",
                                false,
                                LocalDateTime.now().minusDays(1)
                        ),
                        new PatientResultHistoryView.ResultHistoryEntry(
                                new ResultId("RES-002"),
                                "Hemoglobin",
                                "14.2",
                                "13.5-17.5 g/dL",
                                false,
                                LocalDateTime.now().minusDays(30)
                        ),
                        new PatientResultHistoryView.ResultHistoryEntry(
                                new ResultId("RES-003"),
                                "Cholesterol",
                                "220",
                                "<200 mg/dL",
                                true,
                                LocalDateTime.now().minusDays(90)
                        )
                )
        );
        store.put(mockPatient, mockView);
    }

    @Override
    public Optional<PatientResultHistoryView> findByPatientId(PatientId patientId) {
        return Optional.ofNullable(store.get(patientId));
    }

    @Override
    public void save(PatientResultHistoryView view) {
        store.put(view.patientId(), view);
    }
}
