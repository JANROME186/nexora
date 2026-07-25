package com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.adapter.in.web;

import com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.application.MedicalDictationService;
import com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.domain.RadiologyDictation;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class MedicalDictationController {

    private final MedicalDictationService service;

    public MedicalDictationController(MedicalDictationService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/imaging/bcm-img-006")
    public ResponseEntity<Map<String, Object>> getBcmImg006Status() {
        return ResponseEntity.ok(Map.of(
                "capability", "BCM-IMG-006",
                "name", "Medical Dictation",
                "status", "active"
        ));
    }

    @PostMapping("/api/v1/imaging/dictations")
    public ResponseEntity<RadiologyDictation> createDictation(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String actorId,
            @RequestBody CreateDictationRequest request) {
        RadiologyDictation dictation = service.createDictation(
                tenantId, request.studyId(), request.dictationText(), request.audioReferenceUrl(), actorId
        );
        return ResponseEntity.ok(dictation);
    }

    @GetMapping("/api/v1/imaging/dictations/{dictationId}")
    public ResponseEntity<RadiologyDictation> getDictation(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable String dictationId) {
        return ResponseEntity.ok(service.getDictation(tenantId, dictationId));
    }

    @GetMapping("/api/v1/imaging/dictations")
    public ResponseEntity<List<RadiologyDictation>> listDictationsForStudy(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam String studyId) {
        return ResponseEntity.ok(service.listDictationsForStudy(tenantId, studyId));
    }

    public record CreateDictationRequest(
            String studyId,
            String dictationText,
            String audioReferenceUrl
    ) {}
}
