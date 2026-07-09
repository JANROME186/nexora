package com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.application;

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
import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain.ReferenceRange;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain.ReferenceRangeRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain.ReferenceRangeSegment;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogCustomRuleNotImplementedException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogEntityNotFoundException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.InvalidCatalogCommandException;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;

/**
 * Compiles the generatable outputs from bcm-svc-006-reference-range-management/generation-plan.yaml.
 * Only RN-001 (normal low &lt;= normal high) is enforced here; RN-002..RN-006 (critical threshold
 * consistency, demographic overlap detection, analyte publication validation, effective-dated
 * versioning and effective-range resolution) are hooks deferred to MVP-MOD-002-BE-002, matching
 * openapi-source.yaml which marks updateReferenceRange, publishReferenceRange and
 * getEffectiveRangeSnapshot as non-generatable operations.
 *
 * <p>Known model gap: business-rules.yaml lists RN-002 and RN-003 enforcement points as applying to
 * both create and update, but openapi-source.yaml marks createReferenceRange as generatable. This
 * implementation only enforces RN-001 at create time and defers RN-002/RN-003 entirely to
 * MVP-MOD-002-BE-002, consistent with the contract-level generatable flag.</p>
 */
@Service
public class ReferenceRangeManagementService {

    private static final List<String> SEXES = List.of(
            ReferenceRangeSegment.SEX_ANY, ReferenceRangeSegment.SEX_MALE,
            ReferenceRangeSegment.SEX_FEMALE, ReferenceRangeSegment.SEX_OTHER);

    private final ReferenceRangeRepository repository;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public ReferenceRangeManagementService(
            ReferenceRangeRepository repository, TenantDirectory tenantDirectory, AuditRecorder auditRecorder) {
        this(repository, tenantDirectory, auditRecorder, Clock.systemUTC());
    }

    ReferenceRangeManagementService(
            ReferenceRangeRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public ReferenceRange create(CreateReferenceRangeCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String analyteRefId = requiredText(command.analyteRefId(), "Analyte reference id is required.");
        if (command.effectiveFrom() == null) {
            throw new InvalidCatalogCommandException("Effective from date is required.");
        }
        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new CatalogEntityNotFoundException("Tenant was not found.");
        }

        Instant now = Instant.now(clock);
        ReferenceRange range = new ReferenceRange(
                newId(), tenantId, laboratoryId, analyteRefId, 1, ReferenceRange.STATUS_DRAFT,
                command.effectiveFrom(), command.effectiveTo(), now, now);
        ReferenceRange saved = repository.save(range);
        repository.replaceSegments(saved.rangeId(), toSegments(saved.rangeId(), command.segments()));

        auditRecorder.recordSystemEvent(tenantId, "ReferenceRangeCreated", "ReferenceRange", saved.rangeId(),
                "{\"analyteRefId\":\"%s\"}".formatted(jsonText(analyteRefId)));
        return saved;
    }

    public ReferenceRange update(String rangeId, UpdateReferenceRangeCommand command) {
        require(rangeId);
        throw new CatalogCustomRuleNotImplementedException(
                "RN-002/RN-003/RN-005",
                "Updating a reference range requires critical threshold consistency, demographic "
                        + "overlap detection and effective-dated versioning reserved for MVP-MOD-002-BE-002.");
    }

    public ReferenceRange deprecate(String rangeId) {
        ReferenceRange current = require(rangeId);
        if (ReferenceRange.STATUS_RETIRED.equals(current.status())) {
            throw new InvalidCatalogCommandException("A retired reference range cannot be deprecated.");
        }
        ReferenceRange deprecated = new ReferenceRange(
                current.rangeId(), current.tenantId(), current.laboratoryId(), current.analyteRefId(),
                current.version(), ReferenceRange.STATUS_DEPRECATED, current.effectiveFrom(), current.effectiveTo(),
                current.createdAt(), Instant.now(clock));
        return repository.save(deprecated);
    }

    public ReferenceRange publish(String rangeId) {
        require(rangeId);
        throw new CatalogCustomRuleNotImplementedException(
                "RN-004",
                "Publishing a reference range requires analyte publication validation and snapshot "
                        + "freeze reserved for MVP-MOD-002-BE-002.");
    }

    public ReferenceRange getEffectiveRangeSnapshot(String analyteId, String sex, Integer ageDays, String observationDate) {
        requiredText(analyteId, "Analyte id is required.");
        throw new CatalogCustomRuleNotImplementedException(
                "RN-006",
                "Effective-dated range resolution for result validation is reserved for MVP-MOD-002-BE-002.");
    }

    public ReferenceRange get(String rangeId) {
        return require(rangeId);
    }

    public List<ReferenceRangeSegment> getSegments(String rangeId) {
        require(rangeId);
        return repository.findSegments(rangeId);
    }

    public List<ReferenceRange> list(String laboratoryId) {
        return repository.findByLaboratoryId(requiredText(laboratoryId, "Laboratory id is required."));
    }

    private ReferenceRange require(String rangeId) {
        return repository.findById(requiredText(rangeId, "Reference range id is required."))
                .orElseThrow(() -> new CatalogEntityNotFoundException("Reference range was not found."));
    }

    private static List<ReferenceRangeSegment> toSegments(String rangeId, List<ReferenceRangeSegmentInput> inputs) {
        if (inputs == null) {
            return List.of();
        }
        return inputs.stream().map(input -> toSegment(rangeId, input)).toList();
    }

    private static ReferenceRangeSegment toSegment(String rangeId, ReferenceRangeSegmentInput input) {
        String sex = requiredOneOf(input.sex(), "Segment sex is invalid.", SEXES.toArray(String[]::new));
        BigDecimal normalLow = input.normalLow();
        BigDecimal normalHigh = input.normalHigh();
        if (normalLow != null && normalHigh != null && normalLow.compareTo(normalHigh) > 0) {
            throw new InvalidCatalogCommandException("Segment normal low must be less than or equal to normal high.");
        }
        return new ReferenceRangeSegment(
                newId(), rangeId, sex, input.ageMinDays(), input.ageMaxDays(), input.condition(),
                normalLow, normalHigh, input.criticalLow(), input.criticalHigh(), input.unit());
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    private static String jsonText(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
