package com.nexora.hop.platformfoundation.publicweb.intake;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.publicintake.PublicIntakePort;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.publicintake.PublicIntakePort.PublicAppointmentIntakeCommand;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.publicintake.PublicIntakePort.PublicAppointmentIntakeResult;
import com.nexora.hop.platformfoundation.publicweb.PublicWebConstants;
import com.nexora.hop.platformfoundation.publicweb.PublicWebErrorCodes;
import com.nexora.hop.platformfoundation.publicweb.PublicWebException;

/**
 * Anonymous public-website appointment intake (BCM-ATT-001 RN-008). Creates a
 * {@code requested}-state AppointmentSlot from a ProspectiveContact only, subject to
 * BCM-PLT-005's public rate-limit classification enforced by
 * {@code PublicApiRateLimitInterceptor}. Never confirms an appointment, never links to a
 * registered patient and never issues a diagnostic order.
 */
@RestController
@RequestMapping(PublicWebConstants.CARE_DELIVERY_BASE_PATH + "/appointment-requests")
class PublicAppointmentIntakeController {

    private final PublicIntakePort intakePort;

    PublicAppointmentIntakeController(PublicIntakePort intakePort) {
        this.intakePort = intakePort;
    }

    @PostMapping
    ResponseEntity<PublicAppointmentIntakeResult> submit(
            @Valid @RequestBody PublicAppointmentRequestBody body) {
        try {
            PublicAppointmentIntakeResult created = intakePort.submitAppointmentRequest(
                    new PublicAppointmentIntakeCommand(
                            body.tenantId(), body.laboratoryId(), body.branchId(),
                            body.prospectiveFullName(), body.prospectivePhone(), body.prospectiveEmail(),
                            body.scheduledStart(), body.scheduledEnd(),
                            body.requestedItems() == null ? List.of()
                                    : body.requestedItems().stream().map(PublicRequestedItemBody::toInput).toList()));
            return ResponseEntity.created(URI.create(PublicWebConstants.CARE_DELIVERY_BASE_PATH
                    + "/appointment-requests/" + created.appointmentId()))
                    .body(created);
        } catch (PublicIntakePort.PublicIntakeException ex) {
            throw mapException(ex);
        }
    }

    private static PublicWebException mapException(PublicIntakePort.PublicIntakeException ex) {
        return switch (ex.kind()) {
            case INVALID -> new PublicWebException(HttpStatus.BAD_REQUEST,
                    PublicWebErrorCodes.PUBLIC_APPOINTMENT_REQUEST_INVALID, ex.getMessage());
            case CONFLICT -> new PublicWebException(HttpStatus.CONFLICT,
                    PublicWebErrorCodes.PUBLIC_APPOINTMENT_REQUEST_INVALID, ex.getMessage());
            case NOT_FOUND -> new PublicWebException(HttpStatus.NOT_FOUND,
                    PublicWebErrorCodes.PUBLIC_CATALOG_NOT_PUBLISHED, ex.getMessage());
        };
    }

    record PublicRequestedItemBody(@NotBlank String testDefinitionId, @NotBlank String catalogItemKind) {
        PublicIntakePort.PublicRequestedItem toInput() {
            return new PublicIntakePort.PublicRequestedItem(testDefinitionId, catalogItemKind);
        }
    }

    record PublicAppointmentRequestBody(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String branchId,
            String prospectiveFullName,
            String prospectivePhone,
            String prospectiveEmail,
            LocalDate scheduledStart,
            LocalDate scheduledEnd,
            List<PublicRequestedItemBody> requestedItems) {
    }
}
