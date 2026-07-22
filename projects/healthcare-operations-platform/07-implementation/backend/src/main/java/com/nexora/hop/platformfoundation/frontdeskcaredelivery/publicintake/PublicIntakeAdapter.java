package com.nexora.hop.platformfoundation.frontdeskcaredelivery.publicintake;

import java.util.List;

import org.springframework.stereotype.Component;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.application.AppointmentSchedulingService;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.application.PublicRequestAppointmentCommand;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain.AppointmentSlot;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.application.QuotationManagementService;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.application.StartQuotationCommand;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationRequest;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskConflictException;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskEntityNotFoundException;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.InvalidFrontDeskCommandException;

/**
 * Default in-module implementation of {@link PublicIntakePort}. Delegates to the existing
 * AppointmentScheduling and QuotationManagement application services and translates
 * front-desk exceptions into {@link PublicIntakeException} kinds so the public web layer never
 * imports internal exception types.
 */
@Component
class PublicIntakeAdapter implements PublicIntakePort {

    private final AppointmentSchedulingService appointmentSchedulingService;
    private final QuotationManagementService quotationManagementService;

    PublicIntakeAdapter(
            AppointmentSchedulingService appointmentSchedulingService,
            QuotationManagementService quotationManagementService) {
        this.appointmentSchedulingService = appointmentSchedulingService;
        this.quotationManagementService = quotationManagementService;
    }

    @Override
    public PublicAppointmentIntakeResult submitAppointmentRequest(PublicAppointmentIntakeCommand command) {
        try {
            AppointmentSlot created = appointmentSchedulingService.requestFromProspectiveContact(
                    new PublicRequestAppointmentCommand(
                            command.tenantId(), command.laboratoryId(), command.branchId(),
                            command.prospectiveFullName(), command.prospectivePhone(), command.prospectiveEmail(),
                            command.scheduledStart(), command.scheduledEnd(),
                            command.requestedItems() == null ? List.of()
                                    : command.requestedItems().stream()
                                            .map(item -> new PublicRequestAppointmentCommand.RequestedItemInput(
                                                    item.testDefinitionId(), item.catalogItemKind()))
                                            .toList()));
            return new PublicAppointmentIntakeResult(created.appointmentId(), created.laboratoryId(),
                    created.branchId(), created.scheduledStart(), created.scheduledEnd(),
                    created.status(), created.channel());
        } catch (InvalidFrontDeskCommandException ex) {
            throw new PublicIntakeException(PublicIntakeException.Kind.INVALID, ex.getMessage());
        } catch (FrontDeskConflictException ex) {
            throw new PublicIntakeException(PublicIntakeException.Kind.CONFLICT, ex.getMessage());
        } catch (FrontDeskEntityNotFoundException ex) {
            throw new PublicIntakeException(PublicIntakeException.Kind.NOT_FOUND, ex.getMessage());
        }
    }

    @Override
    public PublicQuotationIntakeResult submitQuotationRequest(PublicQuotationIntakeCommand command) {
        try {
            QuotationRequest saved = quotationManagementService.startPublic(new StartQuotationCommand(
                    command.tenantId(), command.laboratoryId(), command.branchId(), null,
                    command.prospectiveFullName(), command.prospectivePhone(), command.prospectiveEmail(), null, null,
                    command.lines() == null ? List.of()
                            : command.lines().stream()
                                    .map(line -> new StartQuotationCommand.QuotationLineInput(
                                            line.testDefinitionId(), line.catalogItemKind(), line.quantity()))
                                    .toList()));
            return new PublicQuotationIntakeResult(saved.quotationId(), saved.laboratoryId(),
                    saved.branchId(), saved.status(), saved.channel());
        } catch (InvalidFrontDeskCommandException ex) {
            throw new PublicIntakeException(PublicIntakeException.Kind.INVALID, ex.getMessage());
        } catch (FrontDeskConflictException ex) {
            throw new PublicIntakeException(PublicIntakeException.Kind.CONFLICT, ex.getMessage());
        } catch (FrontDeskEntityNotFoundException ex) {
            throw new PublicIntakeException(PublicIntakeException.Kind.NOT_FOUND, ex.getMessage());
        }
    }
}
