package com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.domain;

import java.util.List;
import java.util.Optional;

public interface SampleCatalogRepository {

    SampleType saveSampleType(SampleType sampleType);

    Optional<SampleType> findSampleTypeById(String sampleTypeId);

    List<SampleType> findSampleTypesByLaboratoryId(String laboratoryId);

    boolean existsSampleTypeByCode(String laboratoryId, String code, String excludeSampleTypeId);

    SampleRequirement saveSampleRequirement(SampleRequirement requirement);

    Optional<SampleRequirement> findSampleRequirementById(String requirementId);

    List<SampleRequirement> findSampleRequirementsByLaboratoryId(String laboratoryId);
}
