package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.domain.PatientRegistrationRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.domain.PatientRegistrationRequest;

@Repository
@Profile("!local")
class InMemoryPatientRegistrationRepository implements PatientRegistrationRepository {

    private final Map<String, PatientRegistrationRequest> registrations = new ConcurrentHashMap<>();

    @Override
    public PatientRegistrationRequest save(PatientRegistrationRequest registration) {
        registrations.put(registration.registrationRequestId(), registration);
        return registration;
    }

    @Override
    public Optional<PatientRegistrationRequest> findById(String registrationRequestId) {
        return Optional.ofNullable(registrations.get(registrationRequestId));
    }

    @Override
    public List<PatientRegistrationRequest> findByTenantId(String tenantId) {
        return registrations.values().stream()
                .filter(registration -> registration.tenantId().equals(tenantId))
                .toList();
    }
}
