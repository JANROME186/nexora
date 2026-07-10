package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement;

import java.util.Optional;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.DoctorSnapshot;

/**
 * Cross-module read port exposed by the medical-staff context. Downstream contexts use this port
 * to read Doctor snapshots without depending on internal aggregate types (BCM-PER-003 RN-003).
 */
public interface DoctorDirectory {

    Optional<DoctorSnapshot> findSnapshot(String doctorId);

    boolean doctorExists(String doctorId);

    /**
     * BCM-PER-003 RN-004 (active doctors must have at least one verified, non-expired medical
     * license credential) and RN-006 (a suspended doctor cannot be selected as referring doctor).
     * Downstream contexts such as order intake or the doctor portal should consult this policy
     * instead of re-deriving eligibility from raw doctor/credential state.
     */
    boolean isEligibleAsReferringDoctor(String doctorId);
}
