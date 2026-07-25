package com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.adapter.in.web;

import com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.application.ImagingAppointmentSchedulingService;
import com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.domain.ImagingAppointmentSlot;
import java.time.Instant;
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
public class ImagingAppointmentSchedulingController {

    private final ImagingAppointmentSchedulingService service;

    public ImagingAppointmentSchedulingController(ImagingAppointmentSchedulingService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/imaging/bcm-img-001")
    public ResponseEntity<Map<String, Object>> getBcmImg001Status() {
        return ResponseEntity.ok(Map.of(
                "capability", "BCM-IMG-001",
                "name", "Imaging Appointment Scheduling",
                "status", "active"
        ));
    }

    @PostMapping("/api/v1/imaging/appointments")
    public ResponseEntity<ImagingAppointmentSlot> scheduleSlot(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String actorId,
            @RequestBody ScheduleSlotRequest request) {
        ImagingAppointmentSlot slot = service.scheduleSlot(
                tenantId, request.patientId(), request.branchId(), request.modality(),
                request.procedureCode(), request.procedureRoomId(), request.startTime(),
                request.durationMinutes(), request.notes(), actorId
        );
        return ResponseEntity.ok(slot);
    }

    @GetMapping("/api/v1/imaging/appointments/{slotId}")
    public ResponseEntity<ImagingAppointmentSlot> getSlot(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable String slotId) {
        return ResponseEntity.ok(service.getSlot(tenantId, slotId));
    }

    @GetMapping("/api/v1/imaging/appointments")
    public ResponseEntity<List<ImagingAppointmentSlot>> listSlotsForPatient(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam String patientId) {
        return ResponseEntity.ok(service.listSlotsForPatient(tenantId, patientId));
    }

    @PutMapping("/api/v1/imaging/appointments/{slotId}/status")
    public ResponseEntity<ImagingAppointmentSlot> updateStatus(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String actorId,
            @PathVariable String slotId,
            @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "SCHEDULED");
        return ResponseEntity.ok(service.updateSlotStatus(tenantId, slotId, status, actorId));
    }

    public record ScheduleSlotRequest(
            String patientId,
            String branchId,
            String modality,
            String procedureCode,
            String procedureRoomId,
            Instant startTime,
            int durationMinutes,
            String notes
    ) {}
}
