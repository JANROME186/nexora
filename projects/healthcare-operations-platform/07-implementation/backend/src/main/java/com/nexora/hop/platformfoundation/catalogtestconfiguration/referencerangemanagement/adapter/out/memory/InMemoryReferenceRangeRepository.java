package com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.adapter.out.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain.ReferenceRange;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain.ReferenceRangeRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain.ReferenceRangeSegment;

@Repository
@Profile("!local")
class InMemoryReferenceRangeRepository implements ReferenceRangeRepository {

    private final Map<String, ReferenceRange> ranges = new ConcurrentHashMap<>();
    private final Map<String, List<ReferenceRangeSegment>> segments = new ConcurrentHashMap<>();

    @Override
    public ReferenceRange save(ReferenceRange range) {
        ranges.put(range.rangeId(), range);
        return range;
    }

    @Override
    public Optional<ReferenceRange> findById(String rangeId) {
        return Optional.ofNullable(ranges.get(rangeId));
    }

    @Override
    public List<ReferenceRange> findByLaboratoryId(String laboratoryId) {
        return ranges.values().stream().filter(range -> range.laboratoryId().equals(laboratoryId)).toList();
    }

    @Override
    public void replaceSegments(String rangeId, List<ReferenceRangeSegment> rangeSegments) {
        segments.put(rangeId, new ArrayList<>(rangeSegments));
    }

    @Override
    public List<ReferenceRangeSegment> findSegments(String rangeId) {
        return List.copyOf(segments.getOrDefault(rangeId, List.of()));
    }
}
