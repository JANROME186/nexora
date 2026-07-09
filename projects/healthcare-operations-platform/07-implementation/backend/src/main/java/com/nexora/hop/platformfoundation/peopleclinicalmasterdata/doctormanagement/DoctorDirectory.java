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
}
