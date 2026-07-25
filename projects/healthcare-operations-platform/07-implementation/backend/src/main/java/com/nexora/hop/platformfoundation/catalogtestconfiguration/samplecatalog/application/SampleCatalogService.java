package com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.application;

import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.optionalText;
import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.requiredText;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.domain.SampleCatalogRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.domain.SampleRequirement;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.domain.SampleType;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogConflictException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogEntityNotFoundException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.InvalidCatalogCommandException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;

/**
 * Compiles generatable outputs from bcm-svc-007-sample-catalog/generation-plan.md and implements
 * the custom rules CUS-SVC-007-01..04 (sample type publication, sample requirement completeness and
 * sample-type-publication-gated publication, immutable versioning and the published snapshot)
 * delivered by MVP-MOD-002-BE-002.
 */
@Service
public class SampleCatalogService {

    private static final List<String> MATRICES = List.of(
            SampleType.MATRIX_BLOOD, SampleType.MATRIX_SERUM, SampleType.MATRIX_PLASMA, SampleType.MATRIX_URINE,
            SampleType.MATRIX_STOOL, SampleType.MATRIX_SWAB, SampleType.MATRIX_TISSUE, SampleType.MATRIX_OTHER);

    private static final List<String> STORAGE_TEMPERATURES = List.of(
            SampleRequirement.STORAGE_AMBIENT, SampleRequirement.STORAGE_REFRIGERATED,
            SampleRequirement.STORAGE_FROZEN, SampleRequirement.STORAGE_DEEP_FROZEN);

    private final SampleCatalogRepository repository;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public SampleCatalogService(
            SampleCatalogRepository repository, TenantDirectory tenantDirectory, AuditRecorder auditRecorder) {
        this(repository, tenantDirectory, auditRecorder, Clock.systemUTC());
    }

    SampleCatalogService(
            SampleCatalogRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public SampleType createSampleType(CreateSampleTypeCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String code = requiredText(command.code(), "Sample type code is required.");
        String nameEn = requiredText(command.nameEn(), "English name is required.");
        String nameEs = requiredText(command.nameEs(), "Spanish name is required.");
        String matrix = requiredOneOf(command.matrix(), "Sample matrix is invalid.", MATRICES.toArray(String[]::new));

        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new CatalogEntityNotFoundException("Tenant was not found.");
        }
        if (repository.existsSampleTypeByCode(laboratoryId, code, null)) {
            throw new InvalidCatalogCommandException("Sample type code already exists in this laboratory.");
        }

        Instant now = Instant.now(clock);
        SampleType sampleType = new SampleType(
                newId(), tenantId, laboratoryId, code, new LocalizedText(nameEn, nameEs), matrix,
                SampleType.STATUS_DRAFT, 1, now, now);
        SampleType saved = repository.saveSampleType(sampleType);

        auditRecorder.recordSystemEvent(tenantId, "SampleTypeCreated", "SampleType", saved.sampleTypeId(),
                "{\"code\":\"%s\"}".formatted(jsonText(saved.code())));
        return saved;
    }

    public SampleType updateSampleType(String sampleTypeId, UpdateSampleTypeCommand command) {
        SampleType current = requireSampleType(sampleTypeId);
        String code = requiredText(command.code(), "Sample type code is required.");
        String nameEn = requiredText(command.nameEn(), "English name is required.");
        String nameEs = requiredText(command.nameEs(), "Spanish name is required.");
        String matrix = requiredOneOf(command.matrix(), "Sample matrix is invalid.", MATRICES.toArray(String[]::new));

        if (repository.existsSampleTypeByCode(current.laboratoryId(), code, current.sampleTypeId())) {
            throw new InvalidCatalogCommandException("Sample type code already exists in this laboratory.");
        }

        SampleType updated = new SampleType(
                current.sampleTypeId(), current.tenantId(), current.laboratoryId(), code,
                new LocalizedText(nameEn, nameEs), matrix, current.status(), current.version(), current.createdAt(),
                Instant.now(clock));
        return repository.saveSampleType(updated);
    }

    public SampleType getSampleType(String sampleTypeId) {
        return requireSampleType(sampleTypeId);
    }

    public List<SampleType> listSampleTypes(String laboratoryId) {
        return repository.findSampleTypesByLaboratoryId(requiredText(laboratoryId, "Laboratory id is required."));
    }

    public SampleRequirement createSampleRequirement(CreateSampleRequirementCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String sampleTypeRefId = requiredText(command.sampleTypeRefId(), "Sample type reference id is required.");
        BigDecimal minVolumeMl = command.minVolumeMl();
        if (minVolumeMl != null && minVolumeMl.signum() <= 0) {
            throw new InvalidCatalogCommandException("Minimum volume must be greater than zero when specified.");
        }
        String storageTemperature = command.storageTemperature() == null
                ? null
                : requiredOneOf(command.storageTemperature(), "Storage temperature is invalid.",
                        STORAGE_TEMPERATURES.toArray(String[]::new));

        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new CatalogEntityNotFoundException("Tenant was not found.");
        }

        Instant now = Instant.now(clock);
        LocalizedText handlingInstructions = toHandlingInstructions(
                command.handlingInstructionsEn(), command.handlingInstructionsEs());
        SampleRequirement requirement = new SampleRequirement(
                newId(), tenantId, laboratoryId, sampleTypeRefId, minVolumeMl, optionalText(command.containerRefId()),
                handlingInstructions, storageTemperature, SampleRequirement.STATUS_DRAFT, 1, now, now);
        return repository.saveSampleRequirement(requirement);
    }

    public SampleRequirement updateSampleRequirement(String requirementId, UpdateSampleRequirementCommand command) {
        SampleRequirement current = requireSampleRequirement(requirementId);
        if (!SampleRequirement.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogConflictException(
                    "A published sample requirement is immutable. Its published snapshot is the source of truth; "
                            + "create a new draft version instead of editing it directly (RN-004).");
        }

        String sampleTypeRefId = requiredText(command.sampleTypeRefId(), "Sample type reference id is required.");
        BigDecimal minVolumeMl = command.minVolumeMl();
        if (minVolumeMl != null && minVolumeMl.signum() <= 0) {
            throw new InvalidCatalogCommandException("Minimum volume must be greater than zero when specified.");
        }
        String storageTemperature = command.storageTemperature() == null
                ? null
                : requiredOneOf(command.storageTemperature(), "Storage temperature is invalid.",
                        STORAGE_TEMPERATURES.toArray(String[]::new));

        LocalizedText handlingInstructions = toHandlingInstructions(
                command.handlingInstructionsEn(), command.handlingInstructionsEs());
        SampleRequirement updated = new SampleRequirement(
                current.requirementId(), current.tenantId(), current.laboratoryId(), sampleTypeRefId, minVolumeMl,
                optionalText(command.containerRefId()), handlingInstructions, storageTemperature, current.status(),
                current.version(), current.createdAt(), Instant.now(clock));
        return repository.saveSampleRequirement(updated);
    }

    /**
     * RN-003/RN-005 publication rule: a sample requirement can only be published from draft, it must
     * declare a minimum volume (collection completeness), and the sample type it references must
     * itself already be published. Publishing freezes the record; the published record is the
     * immutable snapshot returned by {@link #getPublishedSampleRequirementSnapshot}.
     */
    public SampleRequirement publishSampleRequirement(String requirementId) {
        SampleRequirement current = requireSampleRequirement(requirementId);
        if (!SampleRequirement.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogConflictException(
                    "Only a draft sample requirement can be published (current status: " + current.status() + ").");
        }
        if (current.minVolumeMl() == null) {
            throw new InvalidCatalogCommandException(
                    "A sample requirement must declare a minimum volume before publication.");
        }
        SampleType sampleType = repository.findSampleTypeById(current.sampleTypeRefId())
                .orElseThrow(() -> new InvalidCatalogCommandException(
                        "The referenced sample type could not be resolved for publication."));
        if (!SampleType.STATUS_PUBLISHED.equals(sampleType.status())) {
            throw new InvalidCatalogCommandException(
                    "A sample requirement can only be published when its referenced sample type is published.");
        }

        SampleRequirement published = new SampleRequirement(
                current.requirementId(), current.tenantId(), current.laboratoryId(), current.sampleTypeRefId(),
                current.minVolumeMl(), current.containerRefId(), current.handlingInstructions(),
                current.storageTemperature(), SampleRequirement.STATUS_PUBLISHED, current.version(),
                current.createdAt(), Instant.now(clock));
        SampleRequirement saved = repository.saveSampleRequirement(published);
        auditRecorder.recordSystemEvent(saved.tenantId(), "SampleRequirementPublished", "SampleRequirement",
                saved.requirementId(), "{\"version\":%d}".formatted(saved.version()));
        return saved;
    }

    /** CUS-SVC-007-04: the immutable published snapshot projection of a sample requirement. */
    public SampleRequirement getPublishedSampleRequirementSnapshot(String requirementId) {
        SampleRequirement current = requireSampleRequirement(requirementId);
        if (SampleRequirement.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogEntityNotFoundException(
                    "This sample requirement has no published snapshot; it has never been published.");
        }
        return current;
    }

    /**
     * Publishes a sample type from draft. Sample requirements can only be published once their
     * referenced sample type is published, so exposing sample type publication keeps the sample
     * catalog usable end-to-end.
     */
    public SampleType publishSampleType(String sampleTypeId) {
        SampleType current = requireSampleType(sampleTypeId);
        if (!SampleType.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogConflictException(
                    "Only a draft sample type can be published (current status: " + current.status() + ").");
        }
        SampleType published = new SampleType(
                current.sampleTypeId(), current.tenantId(), current.laboratoryId(), current.code(), current.name(),
                current.matrix(), SampleType.STATUS_PUBLISHED, current.version(), current.createdAt(),
                Instant.now(clock));
        SampleType saved = repository.saveSampleType(published);
        auditRecorder.recordSystemEvent(saved.tenantId(), "SampleTypePublished", "SampleType",
                saved.sampleTypeId(), "{\"version\":%d}".formatted(saved.version()));
        return saved;
    }

    public SampleRequirement getSampleRequirement(String requirementId) {
        return requireSampleRequirement(requirementId);
    }

    public List<SampleRequirement> listSampleRequirements(String laboratoryId) {
        return repository.findSampleRequirementsByLaboratoryId(requiredText(laboratoryId, "Laboratory id is required."));
    }

    private SampleType requireSampleType(String sampleTypeId) {
        return repository.findSampleTypeById(requiredText(sampleTypeId, "Sample type id is required."))
                .orElseThrow(() -> new CatalogEntityNotFoundException("Sample type was not found."));
    }

    private SampleRequirement requireSampleRequirement(String requirementId) {
        return repository.findSampleRequirementById(requiredText(requirementId, "Sample requirement id is required."))
                .orElseThrow(() -> new CatalogEntityNotFoundException("Sample requirement was not found."));
    }

    private static LocalizedText toHandlingInstructions(String en, String es) {
        if (en == null && es == null) {
            return null;
        }
        return new LocalizedText(optionalText(en), optionalText(es));
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    private static String jsonText(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
