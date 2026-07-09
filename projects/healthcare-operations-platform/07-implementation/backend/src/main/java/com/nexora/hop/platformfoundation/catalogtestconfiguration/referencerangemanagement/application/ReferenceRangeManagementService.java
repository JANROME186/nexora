package com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.application;

import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.requiredText;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain.ReferenceRange;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain.ReferenceRangeRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain.ReferenceRangeSegment;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogConflictException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogEntityNotFoundException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.EffectiveDating;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.InvalidCatalogCommandException;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;

/**
 * Compiles the generatable outputs from bcm-svc-006-reference-range-management/generation-plan.yaml
 * and implements the custom rules delivered by MVP-MOD-002-BE-002:
 *
 * <ul>
 *   <li>RN-001 normal low &le; normal high (segment bounds).</li>
 *   <li>RN-002 critical threshold consistency (critical low &le; normal low, critical high &ge;
 *       normal high, critical low &le; critical high).</li>
 *   <li>RN-003 demographic overlap detection between segments of the same range (sex, age and
 *       condition context).</li>
 *   <li>RN-004 published reference ranges are immutable (direct update rejected).</li>
 *   <li>RN-005 effective-dated versioning: two published ranges for the same analyte may not have
 *       overlapping validity windows.</li>
 *   <li>RN-006 effective-dated resolution of the range applicable to a patient context.</li>
 * </ul>
 *
 * <p>Species-based segmentation (referenced by RN-003 "when applicable") is not modeled by
 * ENT-REF-002, so overlap detection is limited to sex, age and condition; see the
 * MVP-MOD-002-BE-002 validation evidence for the documented boundary.</p>
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
        validateEffectiveWindow(command.effectiveFrom(), command.effectiveTo());
        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new CatalogEntityNotFoundException("Tenant was not found.");
        }

        List<ReferenceRangeSegment> segments = toValidatedSegments(newId(), command.segments());
        Instant now = Instant.now(clock);
        ReferenceRange range = new ReferenceRange(
                newId(), tenantId, laboratoryId, analyteRefId, 1, ReferenceRange.STATUS_DRAFT,
                command.effectiveFrom(), command.effectiveTo(), now, now);
        ReferenceRange saved = repository.save(range);
        repository.replaceSegments(saved.rangeId(), reparent(saved.rangeId(), segments));

        auditRecorder.recordSystemEvent(tenantId, "ReferenceRangeCreated", "ReferenceRange", saved.rangeId(),
                "{\"analyteRefId\":\"%s\"}".formatted(jsonText(analyteRefId)));
        return saved;
    }

    /**
     * RN-002/RN-003/RN-004 update rule: only a draft range can be edited directly. Segment bounds,
     * critical thresholds and demographic overlaps are re-validated on every update.
     */
    public ReferenceRange update(String rangeId, UpdateReferenceRangeCommand command) {
        ReferenceRange current = require(rangeId);
        if (!ReferenceRange.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogConflictException(
                    "A published reference range is immutable. Create a new effective-dated version instead of "
                            + "editing it directly (RN-004).");
        }
        LocalDate effectiveFrom = command.effectiveFrom() == null ? current.effectiveFrom() : command.effectiveFrom();
        if (effectiveFrom == null) {
            throw new InvalidCatalogCommandException("Effective from date is required.");
        }
        validateEffectiveWindow(effectiveFrom, command.effectiveTo());
        List<ReferenceRangeSegment> segments = toValidatedSegments(current.rangeId(), command.segments());

        ReferenceRange updated = new ReferenceRange(
                current.rangeId(), current.tenantId(), current.laboratoryId(), current.analyteRefId(),
                current.version(), current.status(), effectiveFrom, command.effectiveTo(), current.createdAt(),
                Instant.now(clock));
        ReferenceRange saved = repository.save(updated);
        repository.replaceSegments(saved.rangeId(), segments);
        return saved;
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

    /**
     * RN-004/RN-005 publication rule: a reference range can only be published from draft, must
     * declare at least one consistent, non-overlapping segment, and its effective window must not
     * overlap any other published range for the same analyte. Publishing freezes the record.
     */
    public ReferenceRange publish(String rangeId) {
        ReferenceRange current = require(rangeId);
        if (!ReferenceRange.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogConflictException(
                    "Only a draft reference range can be published (current status: " + current.status() + ").");
        }
        List<ReferenceRangeSegment> segments = repository.findSegments(current.rangeId());
        if (segments.isEmpty()) {
            throw new InvalidCatalogCommandException(
                    "A reference range must declare at least one segment before publication.");
        }
        validateSegmentConsistency(segments);
        validateNoDemographicOverlap(segments);

        boolean overlaps = repository.findByAnalyteRefId(current.analyteRefId()).stream()
                .filter(other -> !other.rangeId().equals(current.rangeId()))
                .filter(other -> ReferenceRange.STATUS_PUBLISHED.equals(other.status()))
                .anyMatch(other -> EffectiveDating.windowsOverlap(
                        current.effectiveFrom(), current.effectiveTo(), other.effectiveFrom(), other.effectiveTo()));
        if (overlaps) {
            throw new CatalogConflictException(
                    "Another published reference range for this analyte already covers an overlapping "
                            + "effective period (RN-005).");
        }

        ReferenceRange published = new ReferenceRange(
                current.rangeId(), current.tenantId(), current.laboratoryId(), current.analyteRefId(),
                current.version(), ReferenceRange.STATUS_PUBLISHED, current.effectiveFrom(), current.effectiveTo(),
                current.createdAt(), Instant.now(clock));
        ReferenceRange saved = repository.save(published);
        auditRecorder.recordSystemEvent(saved.tenantId(), "ReferenceRangePublished", "ReferenceRange",
                saved.rangeId(), "{\"analyteRefId\":\"%s\"}".formatted(jsonText(saved.analyteRefId())));
        return saved;
    }

    /**
     * RN-006 effective-dated resolution: returns the published reference range that applies to the
     * given analyte on the observation date and that has a segment matching the patient sex and age
     * context. When several published ranges are effective on the same day the most recently
     * effective one wins.
     */
    public ReferenceRange getEffectiveRangeSnapshot(String analyteId, String sex, Integer ageDays, String observationDate) {
        String analyteRefId = requiredText(analyteId, "Analyte id is required.");
        LocalDate onDate = parseObservationDate(observationDate);
        String requestedSex = sex == null
                ? ReferenceRangeSegment.SEX_ANY
                : requiredOneOf(sex, "Sex is invalid.", SEXES.toArray(String[]::new));

        List<ReferenceRange> candidates = repository.findByAnalyteRefId(analyteRefId).stream()
                .filter(range -> ReferenceRange.STATUS_PUBLISHED.equals(range.status()))
                .filter(range -> EffectiveDating.isEffectiveOn(range.effectiveFrom(), range.effectiveTo(), onDate))
                .sorted(Comparator.comparing(ReferenceRange::effectiveFrom).reversed())
                .toList();

        for (ReferenceRange candidate : candidates) {
            boolean hasMatchingSegment = repository.findSegments(candidate.rangeId()).stream()
                    .anyMatch(segment -> segmentMatches(segment, requestedSex, ageDays));
            if (hasMatchingSegment) {
                return candidate;
            }
        }
        throw new CatalogEntityNotFoundException(
                "No published reference range resolves for the requested analyte and patient context.");
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

    private static void validateEffectiveWindow(LocalDate effectiveFrom, LocalDate effectiveTo) {
        if (effectiveTo != null && effectiveTo.isBefore(effectiveFrom)) {
            throw new InvalidCatalogCommandException("Effective to date must not be before effective from date.");
        }
    }

    private static List<ReferenceRangeSegment> toValidatedSegments(String rangeId, List<ReferenceRangeSegmentInput> inputs) {
        List<ReferenceRangeSegment> segments = toSegments(rangeId, inputs);
        validateSegmentConsistency(segments);
        validateNoDemographicOverlap(segments);
        return segments;
    }

    private static List<ReferenceRangeSegment> toSegments(String rangeId, List<ReferenceRangeSegmentInput> inputs) {
        if (inputs == null) {
            return List.of();
        }
        return inputs.stream().map(input -> toSegment(rangeId, input)).toList();
    }

    private static List<ReferenceRangeSegment> reparent(String rangeId, List<ReferenceRangeSegment> segments) {
        List<ReferenceRangeSegment> reparented = new ArrayList<>(segments.size());
        for (ReferenceRangeSegment segment : segments) {
            reparented.add(new ReferenceRangeSegment(
                    segment.segmentId(), rangeId, segment.sex(), segment.ageMinDays(), segment.ageMaxDays(),
                    segment.condition(), segment.normalLow(), segment.normalHigh(), segment.criticalLow(),
                    segment.criticalHigh(), segment.unit()));
        }
        return reparented;
    }

    private static ReferenceRangeSegment toSegment(String rangeId, ReferenceRangeSegmentInput input) {
        String sex = requiredOneOf(input.sex(), "Segment sex is invalid.", SEXES.toArray(String[]::new));
        if (input.ageMinDays() != null && input.ageMinDays() < 0) {
            throw new InvalidCatalogCommandException("Segment minimum age in days must not be negative.");
        }
        if (input.ageMinDays() != null && input.ageMaxDays() != null && input.ageMinDays() > input.ageMaxDays()) {
            throw new InvalidCatalogCommandException("Segment minimum age must not exceed maximum age.");
        }
        return new ReferenceRangeSegment(
                newId(), rangeId, sex, input.ageMinDays(), input.ageMaxDays(), input.condition(),
                input.normalLow(), input.normalHigh(), input.criticalLow(), input.criticalHigh(), input.unit());
    }

    /** RN-001 and RN-002: normal bounds ordering plus critical threshold consistency. */
    private static void validateSegmentConsistency(List<ReferenceRangeSegment> segments) {
        for (ReferenceRangeSegment segment : segments) {
            BigDecimal normalLow = segment.normalLow();
            BigDecimal normalHigh = segment.normalHigh();
            BigDecimal criticalLow = segment.criticalLow();
            BigDecimal criticalHigh = segment.criticalHigh();

            if (normalLow != null && normalHigh != null && normalLow.compareTo(normalHigh) > 0) {
                throw new InvalidCatalogCommandException(
                        "Segment normal low must be less than or equal to normal high.");
            }
            if (criticalLow != null && criticalHigh != null && criticalLow.compareTo(criticalHigh) > 0) {
                throw new InvalidCatalogCommandException(
                        "Segment critical low must be less than or equal to critical high.");
            }
            if (criticalLow != null && normalLow != null && criticalLow.compareTo(normalLow) > 0) {
                throw new InvalidCatalogCommandException(
                        "Segment critical low must not be greater than normal low.");
            }
            if (criticalHigh != null && normalHigh != null && criticalHigh.compareTo(normalHigh) < 0) {
                throw new InvalidCatalogCommandException(
                        "Segment critical high must not be less than normal high.");
            }
        }
    }

    /** RN-003: no two segments of the same range may cover an overlapping demographic context. */
    private static void validateNoDemographicOverlap(List<ReferenceRangeSegment> segments) {
        for (int i = 0; i < segments.size(); i++) {
            for (int j = i + 1; j < segments.size(); j++) {
                if (segmentsConflict(segments.get(i), segments.get(j))) {
                    throw new InvalidCatalogCommandException(
                            "Reference range segments overlap for the same demographic context (sex, age, "
                                    + "condition).");
                }
            }
        }
    }

    private static boolean segmentsConflict(ReferenceRangeSegment a, ReferenceRangeSegment b) {
        return sexesOverlap(a.sex(), b.sex())
                && conditionsMatch(a.condition(), b.condition())
                && ageRangesOverlap(a.ageMinDays(), a.ageMaxDays(), b.ageMinDays(), b.ageMaxDays());
    }

    private static boolean sexesOverlap(String a, String b) {
        return a.equals(b) || ReferenceRangeSegment.SEX_ANY.equals(a) || ReferenceRangeSegment.SEX_ANY.equals(b);
    }

    private static boolean conditionsMatch(String a, String b) {
        if (a == null && b == null) {
            return true;
        }
        return a != null && a.equals(b);
    }

    private static boolean ageRangesOverlap(Integer aMin, Integer aMax, Integer bMin, Integer bMax) {
        int aLow = aMin == null ? Integer.MIN_VALUE : aMin;
        int aHigh = aMax == null ? Integer.MAX_VALUE : aMax;
        int bLow = bMin == null ? Integer.MIN_VALUE : bMin;
        int bHigh = bMax == null ? Integer.MAX_VALUE : bMax;
        return aLow <= bHigh && bLow <= aHigh;
    }

    private static boolean segmentMatches(ReferenceRangeSegment segment, String requestedSex, Integer ageDays) {
        boolean sexMatches = ReferenceRangeSegment.SEX_ANY.equals(segment.sex())
                || ReferenceRangeSegment.SEX_ANY.equals(requestedSex)
                || segment.sex().equals(requestedSex);
        if (!sexMatches) {
            return false;
        }
        if (ageDays == null) {
            return true;
        }
        int low = segment.ageMinDays() == null ? Integer.MIN_VALUE : segment.ageMinDays();
        int high = segment.ageMaxDays() == null ? Integer.MAX_VALUE : segment.ageMaxDays();
        return ageDays >= low && ageDays <= high;
    }

    private LocalDate parseObservationDate(String observationDate) {
        if (observationDate == null || observationDate.isBlank()) {
            return LocalDate.now(clock);
        }
        try {
            return LocalDate.parse(observationDate);
        } catch (RuntimeException exception) {
            throw new InvalidCatalogCommandException("Observation date must be an ISO-8601 date (yyyy-MM-dd).");
        }
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    private static String jsonText(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
