package com.nexora.hop.platformfoundation.imagingoperations.studymanagement.adapter.in.web;

import com.nexora.hop.platformfoundation.imagingoperations.studymanagement.application.ImagingStudyManagementService;
import com.nexora.hop.platformfoundation.imagingoperations.studymanagement.domain.ImagingStudy;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ImagingStudyManagementController {

    private final ImagingStudyManagementService service;

    public ImagingStudyManagementController(ImagingStudyManagementService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/imaging/bcm-img-003")
    public ResponseEntity<Map<String, Object>> getBcmImg003Status() {
        return ResponseEntity.ok(Map.of(
                "capability", "BCM-IMG-003",
                "name", "Imaging Study Management",
                "status", "active"
        ));
    }

    @PostMapping("/api/v1/imaging/studies")
    public ResponseEntity<ImagingStudy> createStudy(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String actorId,
            @RequestBody CreateStudyRequest request) {
        ImagingStudy study = service.createStudy(
                tenantId, request.accessionNumber(), request.patientId(), request.modality(),
                request.studyDescription(), actorId
        );
        return ResponseEntity.ok(study);
    }

    @GetMapping("/api/v1/imaging/studies/{studyId}")
    public ResponseEntity<ImagingStudy> getStudy(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable String studyId) {
        return ResponseEntity.ok(service.getStudy(tenantId, studyId));
    }

    @GetMapping("/api/v1/imaging/studies")
    public ResponseEntity<List<ImagingStudy>> listStudies(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam String patientId) {
        return ResponseEntity.ok(service.listStudiesForPatient(tenantId, patientId));
    }

    @PutMapping("/api/v1/imaging/studies/{studyId}/status")
    public ResponseEntity<ImagingStudy> updateStudyStatus(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String actorId,
            @PathVariable String studyId,
            @RequestBody UpdateStudyStatusRequest request) {
        ImagingStudy study = service.updateStudyCountsAndStatus(
                tenantId, studyId, request.seriesCount(), request.instanceCount(), request.status(), actorId
        );
        return ResponseEntity.ok(study);
    }

    public record CreateStudyRequest(
            String accessionNumber,
            String patientId,
            String modality,
            String studyDescription
    ) {}

    public record UpdateStudyStatusRequest(
            int seriesCount,
            int instanceCount,
            String status
    ) {}
}
