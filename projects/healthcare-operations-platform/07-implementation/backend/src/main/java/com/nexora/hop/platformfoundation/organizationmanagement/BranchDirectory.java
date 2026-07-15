package com.nexora.hop.platformfoundation.organizationmanagement;

import java.util.Optional;

import com.nexora.hop.platformfoundation.organizationmanagement.domain.BranchSnapshot;

/**
 * Cross-module read port exposed by the organization-management module so that other modules
 * (notably {@code frontdeskcaredelivery} order, appointment, reception and admission
 * orchestration) can read Branch snapshots and operational status without depending on internal
 * domain or application types.
 */
public interface BranchDirectory {

    Optional<BranchSnapshot> findSnapshot(String branchId);

    boolean branchExists(String branchId);

    /** BCM-ATT-001 RN-001: an appointment cannot be confirmed for a non-operational branch. */
    boolean isBranchOperational(String branchId);
}
