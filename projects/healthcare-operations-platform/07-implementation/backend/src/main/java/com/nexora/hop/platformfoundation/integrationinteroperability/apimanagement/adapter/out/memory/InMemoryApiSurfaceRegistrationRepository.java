package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.ApiSurfaceRegistration;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.ApiSurfaceRegistrationRepository;

@Repository
@Profile("!local")
class InMemoryApiSurfaceRegistrationRepository implements ApiSurfaceRegistrationRepository {

    private final Map<String, ApiSurfaceRegistration> registrations = new ConcurrentHashMap<>();

    @Override
    public ApiSurfaceRegistration save(ApiSurfaceRegistration registration) {
        registrations.put(registration.registrationId(), registration);
        return registration;
    }

    @Override
    public Optional<ApiSurfaceRegistration> findByOperationId(String operationId) {
        return registrations.values().stream()
                .filter(registration -> registration.operationId().equals(operationId)).findFirst();
    }

    @Override
    public List<ApiSurfaceRegistration> findAll() {
        return List.copyOf(registrations.values());
    }
}
