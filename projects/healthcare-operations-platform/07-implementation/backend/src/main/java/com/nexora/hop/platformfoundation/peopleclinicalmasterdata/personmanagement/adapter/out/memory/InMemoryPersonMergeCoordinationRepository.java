package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.adapter.out.memory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonMergeCoordination;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonMergeCoordinationRepository;

@Repository
@Profile("!local")
class InMemoryPersonMergeCoordinationRepository implements PersonMergeCoordinationRepository {

    private final Map<String, PersonMergeCoordination> coordinations = new ConcurrentHashMap<>();

    @Override
    public PersonMergeCoordination save(PersonMergeCoordination coordination) {
        coordinations.put(coordination.coordinationId(), coordination);
        return coordination;
    }

    @Override
    public Optional<PersonMergeCoordination> findById(String coordinationId) {
        return Optional.ofNullable(coordinations.get(coordinationId));
    }
}
