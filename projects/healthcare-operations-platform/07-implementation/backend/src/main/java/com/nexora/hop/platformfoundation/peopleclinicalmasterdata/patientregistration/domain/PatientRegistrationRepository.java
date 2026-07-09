package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.domain;

import java.util.List;
import java.util.Optional;

public interface PatientRegistrationRepository {

    PatientRegistrationRequest save(PatientRegistrationRequest registration);

    Optional<PatientRegistrationRequest> findById(String registrationRequestId);

    List<PatientRegistrationRequest> findByTenantId(String tenantId);
}
