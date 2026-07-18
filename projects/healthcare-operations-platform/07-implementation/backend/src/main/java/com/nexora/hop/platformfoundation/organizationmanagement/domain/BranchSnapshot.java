package com.nexora.hop.platformfoundation.organizationmanagement.domain;

/**
 * Immutable minimal branch projection consumed by downstream contexts (orders-samples,
 * cash-sales). Modeled as RM-BRN-001, mirrors the {@code PatientSnapshot}/{@code DoctorSnapshot}
 * cross-module read pattern from peopleclinicalmasterdata.
 * <p>
 * {@code version} is now sourced directly from {@link Branch#version()} (TD-BE-009 closed).
 * {@link Branch} currently only supports a create command — there is no update/deactivate
 * mutation path yet — so every branch has {@code version} 1 by construction, not because the
 * field is a placeholder; once a branch mutation command is added, its version will increment
 * there and flow through this snapshot automatically.
 */
public record BranchSnapshot(
        String branchId,
        String tenantId,
        String laboratoryId,
        String name,
        String status,
        int version) {

    public static BranchSnapshot from(Branch branch) {
        return new BranchSnapshot(
                branch.branchId(),
                branch.tenantId(),
                branch.laboratoryId(),
                branch.name(),
                branch.status(),
                branch.version());
    }
}
