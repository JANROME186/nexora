package com.nexora.hop.platformfoundation.organizationmanagement.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

/**
 * Verifies TD-BE-009 closure: {@link BranchSnapshot#from(Branch)} must reflect the real
 * {@link Branch#version()} field rather than a hard-coded placeholder constant.
 */
class BranchSnapshotTest {

    @Test
    void fromReflectsTheBranchesActualVersionField() {
        Instant now = Instant.now();
        Branch branch = new Branch(
                "branch-1", "tenant-1", "laboratory-1", "North Branch", "active", 7, now, now);

        BranchSnapshot snapshot = BranchSnapshot.from(branch);

        assertThat(snapshot.version()).isEqualTo(7);
        assertThat(snapshot.branchId()).isEqualTo(branch.branchId());
        assertThat(snapshot.tenantId()).isEqualTo(branch.tenantId());
        assertThat(snapshot.laboratoryId()).isEqualTo(branch.laboratoryId());
        assertThat(snapshot.name()).isEqualTo(branch.name());
        assertThat(snapshot.status()).isEqualTo(branch.status());
    }

    @Test
    void fromDoesNotHardcodeVersionToOneWhenBranchVersionDiffers() {
        Instant now = Instant.now();
        Branch branch = new Branch(
                "branch-2", "tenant-1", "laboratory-1", "South Branch", "active", 3, now, now);

        BranchSnapshot snapshot = BranchSnapshot.from(branch);

        assertThat(snapshot.version()).isNotEqualTo(1);
        assertThat(snapshot.version()).isEqualTo(branch.version());
    }
}
