package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement;

import java.util.Optional;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientSnapshot;

/**
 * Cross-module read port exposed by the patient-management context. The Patient Registration
 * orchestration ({@code BCM-ATT-002}) and downstream consumers use this port to read Patient
 * snapshots without depending on internal aggregate types (BCM-PER-002 RN-003, POL-PAT-002-02).
 */
public interface PatientDirectory {

    Optional<PatientSnapshot> findSnapshot(String patientId);

    boolean patientExists(String patientId);
}
