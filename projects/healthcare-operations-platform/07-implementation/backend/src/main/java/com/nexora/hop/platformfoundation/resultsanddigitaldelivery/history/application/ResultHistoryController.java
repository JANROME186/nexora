package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.application;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.domain.PatientResultHistoryView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/results/history")
public class ResultHistoryController {

    private final ResultHistoryService resultHistoryService;

    public ResultHistoryController(ResultHistoryService resultHistoryService) {
        this.resultHistoryService = resultHistoryService;
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<PatientResultHistoryView> getPatientHistory(@PathVariable String patientId) {
        PatientResultHistoryView view = resultHistoryService.getHistoryForPatient(patientId);
        return ResponseEntity.ok(view);
    }
}
