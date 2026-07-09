package com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.domain.SampleCatalogRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.domain.SampleRequirement;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.domain.SampleType;

@Repository
@Profile("!local")
class InMemorySampleCatalogRepository implements SampleCatalogRepository {

    private final Map<String, SampleType> sampleTypes = new ConcurrentHashMap<>();
    private final Map<String, SampleRequirement> sampleRequirements = new ConcurrentHashMap<>();

    @Override
    public SampleType saveSampleType(SampleType sampleType) {
        sampleTypes.put(sampleType.sampleTypeId(), sampleType);
        return sampleType;
    }

    @Override
    public Optional<SampleType> findSampleTypeById(String sampleTypeId) {
        return Optional.ofNullable(sampleTypes.get(sampleTypeId));
    }

    @Override
    public List<SampleType> findSampleTypesByLaboratoryId(String laboratoryId) {
        return sampleTypes.values().stream().filter(type -> type.laboratoryId().equals(laboratoryId)).toList();
    }

    @Override
    public boolean existsSampleTypeByCode(String laboratoryId, String code, String excludeSampleTypeId) {
        return sampleTypes.values().stream()
                .anyMatch(type -> type.laboratoryId().equals(laboratoryId)
                        && type.code().equals(code)
                        && !type.sampleTypeId().equals(excludeSampleTypeId));
    }

    @Override
    public SampleRequirement saveSampleRequirement(SampleRequirement requirement) {
        sampleRequirements.put(requirement.requirementId(), requirement);
        return requirement;
    }

    @Override
    public Optional<SampleRequirement> findSampleRequirementById(String requirementId) {
        return Optional.ofNullable(sampleRequirements.get(requirementId));
    }

    @Override
    public List<SampleRequirement> findSampleRequirementsByLaboratoryId(String laboratoryId) {
        return sampleRequirements.values().stream()
                .filter(requirement -> requirement.laboratoryId().equals(laboratoryId))
                .toList();
    }
}
