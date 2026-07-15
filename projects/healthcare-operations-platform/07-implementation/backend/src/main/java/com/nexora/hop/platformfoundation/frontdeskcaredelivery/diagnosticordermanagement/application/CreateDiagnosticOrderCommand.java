package com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.application;

import java.util.List;

public record CreateDiagnosticOrderCommand(
        String tenantId,
        String laboratoryId,
        String branchId,
        String intakeChannel,
        String sourceReferenceId,
        String patientId,
        String doctorId,
        String actorId,
        List<OrderLineInput> lines) {
}
