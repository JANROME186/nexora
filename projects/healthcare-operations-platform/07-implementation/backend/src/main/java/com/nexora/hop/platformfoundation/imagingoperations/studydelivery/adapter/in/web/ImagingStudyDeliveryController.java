package com.nexora.hop.platformfoundation.imagingoperations.studydelivery.adapter.in.web;

import com.nexora.hop.platformfoundation.imagingoperations.studydelivery.application.ImagingStudyDeliveryService;
import com.nexora.hop.platformfoundation.imagingoperations.studydelivery.domain.ImagingDeliveryPackage;
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
public class ImagingStudyDeliveryController {

    private final ImagingStudyDeliveryService service;

    public ImagingStudyDeliveryController(ImagingStudyDeliveryService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/imaging/bcm-img-008")
    public ResponseEntity<Map<String, Object>> getBcmImg008Status() {
        return ResponseEntity.ok(Map.of(
                "capability", "BCM-IMG-008",
                "name", "Imaging Study Delivery",
                "status", "active"
        ));
    }

    @PostMapping("/api/v1/imaging/delivery-packages")
    public ResponseEntity<ImagingDeliveryPackage> createDeliveryPackage(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String actorId,
            @RequestBody CreateDeliveryPackageRequest request) {
        ImagingDeliveryPackage deliveryPackage = service.createDeliveryPackage(
                tenantId, request.studyId(), request.patientId(), request.deliveryFormat(), actorId
        );
        return ResponseEntity.ok(deliveryPackage);
    }

    @GetMapping("/api/v1/imaging/delivery-packages/{packageId}")
    public ResponseEntity<ImagingDeliveryPackage> getDeliveryPackage(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable String packageId) {
        return ResponseEntity.ok(service.getDeliveryPackage(tenantId, packageId));
    }

    @GetMapping("/api/v1/imaging/delivery-packages")
    public ResponseEntity<List<ImagingDeliveryPackage>> listDeliveryPackagesForPatient(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam String patientId) {
        return ResponseEntity.ok(service.listDeliveryPackagesForPatient(tenantId, patientId));
    }

    @PutMapping("/api/v1/imaging/delivery-packages/{packageId}/deliver")
    public ResponseEntity<ImagingDeliveryPackage> markDelivered(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String actorId,
            @PathVariable String packageId) {
        return ResponseEntity.ok(service.markDelivered(tenantId, packageId, actorId));
    }

    public record CreateDeliveryPackageRequest(
            String studyId,
            String patientId,
            String deliveryFormat
    ) {}
}
