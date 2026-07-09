package com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain;

import java.util.List;
import java.util.Optional;

public interface ReferenceRangeRepository {

    ReferenceRange save(ReferenceRange range);

    Optional<ReferenceRange> findById(String rangeId);

    List<ReferenceRange> findByLaboratoryId(String laboratoryId);

    void replaceSegments(String rangeId, List<ReferenceRangeSegment> segments);

    List<ReferenceRangeSegment> findSegments(String rangeId);
}
