package com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain;

import java.util.List;
import java.util.Optional;

public interface ReferenceRangeRepository {

    ReferenceRange save(ReferenceRange range);

    Optional<ReferenceRange> findById(String rangeId);

    List<ReferenceRange> findByLaboratoryId(String laboratoryId);

    /**
     * Returns every reference range recorded for the given analyte, across statuses. Used by the
     * effective-dated resolution (RN-006) and publication overlap detection (RN-005) custom rules.
     */
    List<ReferenceRange> findByAnalyteRefId(String analyteRefId);

    void replaceSegments(String rangeId, List<ReferenceRangeSegment> segments);

    List<ReferenceRangeSegment> findSegments(String rangeId);
}
