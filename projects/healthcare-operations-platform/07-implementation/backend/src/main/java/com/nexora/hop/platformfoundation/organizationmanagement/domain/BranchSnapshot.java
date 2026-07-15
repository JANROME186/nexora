package com.nexora.hop.platformfoundation.organizationmanagement.domain;

/**
 * Immutable minimal branch projection consumed by downstream contexts (orders-samples,
 * cash-sales). Modeled as RM-BRN-001, mirrors the {@code PatientSnapshot}/{@code DoctorSnapshot}
 * cross-module read pattern from peopleclinicalmasterdata.
 * <p>
 * {@code version} is fixed at {@value #UNVERSIONED} because {@link Branch} does not yet track an
 * incrementing optimistic-concurrency counter (unlike Patient/Doctor/TestDefinition); see
 * TD-BE-009. Downstream snapshot consumers should treat this field as a placeholder until branch
 * versioning is added.
 */
public record BranchSnapshot(
        String branchId,
        String tenantId,
        String laboratoryId,
        String name,
        String status,
        int version) {

    private static final int UNVERSIONED = 1;

    public static BranchSnapshot from(Branch branch) {
        return new BranchSnapshot(
                branch.branchId(),
                branch.tenantId(),
                branch.laboratoryId(),
                branch.name(),
                branch.status(),
                UNVERSIONED);
    }
}
