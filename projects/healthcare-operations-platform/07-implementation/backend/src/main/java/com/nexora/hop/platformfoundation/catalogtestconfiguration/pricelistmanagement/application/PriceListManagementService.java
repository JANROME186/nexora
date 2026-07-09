package com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.application;

import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.optionalText;
import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.requiredText;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceEntry;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceList;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceListRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogCustomRuleNotImplementedException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogEntityNotFoundException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.InvalidCatalogCommandException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;

/**
 * Compiles generatable outputs from bcm-svc-009-price-list-management/generation-plan.yaml.
 * Custom points CUS-SVC-009-01..04 (catalog item publication validation, versioning,
 * effective-date overlap detection, effective price resolution) are hooks deferred to
 * MVP-MOD-002-BE-002. updatePriceList is entirely a hook because openapi-source.yaml marks it
 * non-generatable (effective-dated versioning and snapshot freeze).
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

    public PriceList update(String priceListId, UpdatePriceListCommand command) {
        require(priceListId);
        throw new CatalogCustomRuleNotImplementedException(
                "RN-004",
                "Updating a price list requires effective-dated versioning and snapshot freeze "
                        + "reserved for MVP-MOD-002-BE-002.");
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

    public PriceList publish(String priceListId) {
        require(priceListId);
        throw new CatalogCustomRuleNotImplementedException(
                "RN-003/RN-005",
                "Publishing a price list requires catalog item publication validation and effective-date "
                        + "overlap detection reserved for MVP-MOD-002-BE-002.");
    }

    public PriceList getEffectivePriceSnapshot(String itemType, String itemRefId, String currency, String agreementRefId, String saleDate) {
        requiredText(itemRefId, "Item reference id is required.");
        throw new CatalogCustomRuleNotImplementedException(
                "RN-006",
                "Effective-dated price resolution for quotation, cash and billing is reserved for "
                        + "MVP-MOD-002-BE-002.");
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
