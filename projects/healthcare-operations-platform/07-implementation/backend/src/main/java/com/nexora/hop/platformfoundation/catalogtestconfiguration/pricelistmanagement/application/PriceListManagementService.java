package com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.application;

import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.optionalText;
import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.requiredText;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceEntry;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceList;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceListRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogConflictException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogEntityNotFoundException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.EffectiveDating;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.InvalidCatalogCommandException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;

/**
 * Compiles generatable outputs from bcm-svc-009-price-list-management/generation-plan.yaml and
 * implements the custom rules CUS-SVC-009-01..04 (draft-only editing, entry-gated publication with
 * effective-date overlap detection, and effective-dated price resolution) delivered by
 * MVP-MOD-002-BE-002.
 */
@Service
public class PriceListManagementService {

    private static final List<String> ITEM_TYPES = List.of(
            PriceEntry.ITEM_SERVICE, PriceEntry.ITEM_TEST, PriceEntry.ITEM_PANEL);

    private final PriceListRepository repository;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public PriceListManagementService(
            PriceListRepository repository, TenantDirectory tenantDirectory, AuditRecorder auditRecorder) {
        this(repository, tenantDirectory, auditRecorder, Clock.systemUTC());
    }

    PriceListManagementService(
            PriceListRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public PriceList create(CreatePriceListCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String code = requiredText(command.code(), "Price list code is required.");
        String nameEn = requiredText(command.nameEn(), "English name is required.");
        String nameEs = requiredText(command.nameEs(), "Spanish name is required.");
        String currency = requiredText(command.currency(), "Currency is required.");
        if (command.effectiveFrom() == null) {
            throw new InvalidCatalogCommandException("Effective from date is required.");
        }
        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new CatalogEntityNotFoundException("Tenant was not found.");
        }
        if (repository.existsByCode(laboratoryId, code, null)) {
            throw new InvalidCatalogCommandException("Price list code already exists in this laboratory.");
        }

        Instant now = Instant.now(clock);
        PriceList priceList = new PriceList(
                newId(), tenantId, laboratoryId, code, new LocalizedText(nameEn, nameEs), currency,
                optionalText(command.agreementRefId()), command.effectiveFrom(), command.effectiveTo(),
                PriceList.STATUS_DRAFT, 1, now, now);
        PriceList saved = repository.save(priceList);

        auditRecorder.recordSystemEvent(tenantId, "PriceListCreated", "PriceList", saved.priceListId(),
                "{\"code\":\"%s\"}".formatted(jsonText(saved.code())));
        return saved;
    }

    public PriceEntry addPriceEntry(String priceListId, AddPriceEntryCommand command) {
        PriceList priceList = require(priceListId);
        String itemType = requiredOneOf(command.itemType(), "Item type is invalid.", ITEM_TYPES.toArray(String[]::new));
        String itemRefId = requiredText(command.itemRefId(), "Item reference id is required.");
        if (command.amount() == null || command.amount().signum() < 0) {
            throw new InvalidCatalogCommandException("A price entry amount must be non-negative.");
        }

        PriceEntry entry = new PriceEntry(
                newId(), priceList.priceListId(), itemType, itemRefId, new Money(priceList.currency(), command.amount()));
        return repository.saveEntry(entry);
    }

    /**
     * RN-004 update rule: only a draft price list can be edited directly. A published price list is
     * an immutable effective-dated version; changes require publishing a new version instead.
     */
    public PriceList update(String priceListId, UpdatePriceListCommand command) {
        PriceList current = require(priceListId);
        if (!PriceList.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogConflictException(
                    "A published price list is immutable. Publish a new effective-dated version instead of "
                            + "editing it directly (RN-004).");
        }
        String nameEn = requiredText(command.nameEn(), "English name is required.");
        String nameEs = requiredText(command.nameEs(), "Spanish name is required.");
        LocalDate effectiveFrom = command.effectiveFrom() == null ? current.effectiveFrom() : command.effectiveFrom();
        validateEffectiveWindow(effectiveFrom, command.effectiveTo());

        PriceList updated = new PriceList(
                current.priceListId(), current.tenantId(), current.laboratoryId(), current.code(),
                new LocalizedText(nameEn, nameEs), current.currency(), optionalText(command.agreementRefId()),
                effectiveFrom, command.effectiveTo(), current.status(), current.version(), current.createdAt(),
                Instant.now(clock));
        return repository.save(updated);
    }

    public PriceList deprecate(String priceListId) {
        PriceList current = require(priceListId);
        if (PriceList.STATUS_RETIRED.equals(current.status())) {
            throw new InvalidCatalogCommandException("A retired price list cannot be deprecated.");
        }
        PriceList deprecated = new PriceList(
                current.priceListId(), current.tenantId(), current.laboratoryId(), current.code(), current.name(),
                current.currency(), current.agreementRefId(), current.effectiveFrom(), current.effectiveTo(),
                PriceList.STATUS_DEPRECATED, current.version(), current.createdAt(), Instant.now(clock));
        return repository.save(deprecated);
    }

    /**
     * RN-003/RN-005 publication rule: a price list can only be published from draft, must carry at
     * least one price entry, and its effective window must not overlap any other published price
     * list that shares the same laboratory, currency and commercial agreement scope. Publishing
     * freezes the price list.
     */
    public PriceList publish(String priceListId) {
        PriceList current = require(priceListId);
        if (!PriceList.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogConflictException(
                    "Only a draft price list can be published (current status: " + current.status() + ").");
        }
        if (repository.findEntries(current.priceListId()).isEmpty()) {
            throw new InvalidCatalogCommandException(
                    "A price list must contain at least one price entry before publication.");
        }

        boolean overlaps = repository.findByLaboratoryId(current.laboratoryId()).stream()
                .filter(other -> !other.priceListId().equals(current.priceListId()))
                .filter(other -> PriceList.STATUS_PUBLISHED.equals(other.status()))
                .filter(other -> other.currency().equals(current.currency()))
                .filter(other -> Objects.equals(other.agreementRefId(), current.agreementRefId()))
                .anyMatch(other -> EffectiveDating.windowsOverlap(
                        current.effectiveFrom(), current.effectiveTo(), other.effectiveFrom(), other.effectiveTo()));
        if (overlaps) {
            throw new CatalogConflictException(
                    "Another published price list for the same currency and agreement already covers an "
                            + "overlapping effective period (RN-005).");
        }

        PriceList published = new PriceList(
                current.priceListId(), current.tenantId(), current.laboratoryId(), current.code(), current.name(),
                current.currency(), current.agreementRefId(), current.effectiveFrom(), current.effectiveTo(),
                PriceList.STATUS_PUBLISHED, current.version(), current.createdAt(), Instant.now(clock));
        PriceList saved = repository.save(published);
        auditRecorder.recordSystemEvent(saved.tenantId(), "PriceListPublished", "PriceList", saved.priceListId(),
                "{\"code\":\"%s\"}".formatted(jsonText(saved.code())));
        return saved;
    }

    /**
     * RN-006 effective-dated price resolution: returns the published price list that prices the
     * requested catalog item on the sale date, honouring an optional currency and commercial
     * agreement filter. When several published price lists apply, the most recently effective one
     * wins.
     *
     * <p>The contract does not carry a laboratory in this query, so resolution searches published
     * price lists by their entries; see the MVP-MOD-002-BE-002 validation evidence for the
     * documented scoping boundary.</p>
     */
    public PriceList getEffectivePriceSnapshot(
            String itemType, String itemRefId, String currency, String agreementRefId, String saleDate) {
        String type = requiredOneOf(itemType, "Item type is invalid.", ITEM_TYPES.toArray(String[]::new));
        String refId = requiredText(itemRefId, "Item reference id is required.");
        LocalDate onDate = parseSaleDate(saleDate);

        return repository.findByStatus(PriceList.STATUS_PUBLISHED).stream()
                .filter(list -> currency == null || currency.isBlank() || list.currency().equals(currency))
                .filter(list -> agreementRefId == null || agreementRefId.isBlank()
                        || agreementRefId.equals(list.agreementRefId()))
                .filter(list -> EffectiveDating.isEffectiveOn(list.effectiveFrom(), list.effectiveTo(), onDate))
                .filter(list -> pricesItem(list.priceListId(), type, refId))
                .max(Comparator.comparing(PriceList::effectiveFrom))
                .orElseThrow(() -> new CatalogEntityNotFoundException(
                        "No published price list resolves for the requested item and sale context."));
    }

    private boolean pricesItem(String priceListId, String itemType, String itemRefId) {
        return repository.findEntries(priceListId).stream()
                .anyMatch(entry -> entry.itemType().equals(itemType) && entry.itemRefId().equals(itemRefId));
    }

    private LocalDate parseSaleDate(String saleDate) {
        if (saleDate == null || saleDate.isBlank()) {
            return LocalDate.now(clock);
        }
        try {
            return LocalDate.parse(saleDate);
        } catch (RuntimeException exception) {
            throw new InvalidCatalogCommandException("Sale date must be an ISO-8601 date (yyyy-MM-dd).");
        }
    }

    private static void validateEffectiveWindow(LocalDate effectiveFrom, LocalDate effectiveTo) {
        if (effectiveFrom == null) {
            throw new InvalidCatalogCommandException("Effective from date is required.");
        }
        if (effectiveTo != null && effectiveTo.isBefore(effectiveFrom)) {
            throw new InvalidCatalogCommandException("Effective to date must not be before effective from date.");
        }
    }

    public PriceList get(String priceListId) {
        return require(priceListId);
    }

    public List<PriceEntry> getEntries(String priceListId) {
        require(priceListId);
        return repository.findEntries(priceListId);
    }

    public List<PriceList> list(String laboratoryId) {
        return repository.findByLaboratoryId(requiredText(laboratoryId, "Laboratory id is required."));
    }

    private PriceList require(String priceListId) {
        return repository.findById(requiredText(priceListId, "Price list id is required."))
                .orElseThrow(() -> new CatalogEntityNotFoundException("Price list was not found."));
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    private static String jsonText(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
