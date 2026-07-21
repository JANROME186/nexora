package com.nexora.hop.platformfoundation.frontdeskcaredelivery.publicintake;

import java.time.LocalDate;
import java.util.List;

/**
 * Anonymous public-website intake boundary for BCM-ATT-001 RN-008 (appointment) and BCM-ATT-006
 * RN-009 (quotation). Every method captures a ProspectiveContact and produces a requested-state
 * or draft-state record; issuing, confirming, converting or pricing an intake record is never
 * exposed here.
 */
public interface PublicIntakePort {

    PublicAppointmentIntakeResult submitAppointmentRequest(PublicAppointmentIntakeCommand command);

    PublicQuotationIntakeResult submitQuotationRequest(PublicQuotationIntakeCommand command);

    /** Anonymous public appointment request (RN-008). */
    record PublicAppointmentIntakeCommand(
            String tenantId,
            String laboratoryId,
            String branchId,
            String prospectiveFullName,
            String prospectivePhone,
            String prospectiveEmail,
            LocalDate scheduledStart,
            LocalDate scheduledEnd,
            List<PublicRequestedItem> requestedItems) {
    }

    record PublicRequestedItem(String testDefinitionId, String catalogItemKind) {
    }

    record PublicAppointmentIntakeResult(
            String appointmentId,
            String laboratoryId,
            String branchId,
            LocalDate scheduledStart,
            LocalDate scheduledEnd,
            String status,
            String channel) {
    }

    /** Anonymous public quotation request (RN-009). */
    record PublicQuotationIntakeCommand(
            String tenantId,
            String laboratoryId,
            String branchId,
            String prospectiveFullName,
            String prospectivePhone,
            String prospectiveEmail,
            List<PublicQuotationLine> lines) {
    }

    record PublicQuotationLine(String testDefinitionId, String catalogItemKind, Integer quantity) {
    }

    record PublicQuotationIntakeResult(
            String quotationId, String laboratoryId, String branchId, String status) {
    }

    /** Structured exception raised for validation or conflict failures during public intake. */
    class PublicIntakeException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private final Kind kind;

        public PublicIntakeException(Kind kind, String message) {
            super(message);
            this.kind = kind;
        }

        public Kind kind() {
            return kind;
        }

        public enum Kind { INVALID, CONFLICT, NOT_FOUND }
    }
}
