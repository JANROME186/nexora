package com.nexora.hop.platformfoundation.imagingoperations.receptionintake.adapter.in.web;

import com.nexora.hop.platformfoundation.imagingoperations.receptionintake.application.ImagingReceptionService;
import com.nexora.hop.platformfoundation.imagingoperations.receptionintake.domain.ImagingReceptionIntake;
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
public class ImagingReceptionController {

    private final ImagingReceptionService service;

    public ImagingReceptionController(ImagingReceptionService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/imaging/bcm-img-002")
    public ResponseEntity<Map<String, Object>> getBcmImg002Status() {
        return ResponseEntity.ok(Map.of(
                "capability", "BCM-IMG-002",
                "name", "Imaging Reception",
                "status", "active"
        ));
    }

    @PostMapping("/api/v1/imaging/receptions")
    public ResponseEntity<ImagingReceptionIntake> checkIn(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String actorId,
            @RequestBody CheckInRequest request) {
        ImagingReceptionIntake intake = service.checkIn(
                tenantId, request.appointmentSlotId(), request.patientId(),
                request.preparationVerified(), request.intakeNotes(), actorId
        );
        return ResponseEntity.ok(intake);
    }

    @GetMapping("/api/v1/imaging/receptions/{intakeId}")
    public ResponseEntity<ImagingReceptionIntake> getIntake(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable String intakeId) {
        return ResponseEntity.ok(service.getIntake(tenantId, intakeId));
    }

    @GetMapping("/api/v1/imaging/receptions")
    public ResponseEntity<ImagingReceptionIntake> getIntakeBySlot(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam String appointmentSlotId) {
        return ResponseEntity.ok(service.getIntakeBySlot(tenantId, appointmentSlotId));
    }

    public record CheckInRequest(
            String appointmentSlotId,
            String patientId,
            boolean preparationVerified,
            String intakeNotes
    ) {}
}
