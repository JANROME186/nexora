package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain;

import java.util.Optional;

public interface PersonMergeCoordinationRepository {

    PersonMergeCoordination save(PersonMergeCoordination coordination);

    Optional<PersonMergeCoordination> findById(String coordinationId);
}
