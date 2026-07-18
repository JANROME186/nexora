package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain;

import java.util.List;
import java.util.Optional;

public interface ApiSurfaceRegistrationRepository {

    ApiSurfaceRegistration save(ApiSurfaceRegistration registration);

    Optional<ApiSurfaceRegistration> findByOperationId(String operationId);

    List<ApiSurfaceRegistration> findAll();
}
