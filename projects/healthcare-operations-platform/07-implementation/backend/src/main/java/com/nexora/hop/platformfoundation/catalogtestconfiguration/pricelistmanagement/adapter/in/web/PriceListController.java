package com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.adapter.in.web;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.application.AddPriceEntryCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.application.CreatePriceListCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.application.PriceListManagementService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.application.UpdatePriceListCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceEntry;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceList;

/** Rendered controller for bcm-svc-009-price-list-management/openapi-source.md (base path /api/catalog/price-lists). */
@RestController
@RequestMapping("/api/catalog/price-lists")
class PriceListController {

    private final PriceListManagementService service;

    PriceListController(PriceListManagementService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<PriceListResponse>> listPriceLists(@RequestParam String laboratoryId) {
        return ResponseEntity.ok(service.list(laboratoryId).stream().map(this::toResponse).toList());
    }

    @GetMapping("/{priceListId}")
    ResponseEntity<PriceListResponse> getPriceList(@PathVariable String priceListId) {
        return ResponseEntity.ok(toResponse(service.get(priceListId)));
    }

    @PostMapping
    ResponseEntity<PriceListResponse> createPriceList(@Valid @RequestBody CreatePriceListRequest request) {
        PriceList created = service.create(new CreatePriceListCommand(
                request.tenantId(), request.laboratoryId(), request.code(), request.nameEn(), request.nameEs(),
                request.currency(), request.agreementRefId(), request.effectiveFrom(), request.effectiveTo()));
        return ResponseEntity.created(URI.create("/api/catalog/price-lists/" + created.priceListId()))
                .body(toResponse(created));
    }

    @PostMapping("/{priceListId}/entries")
    ResponseEntity<PriceEntryResponse> addPriceEntry(
            @PathVariable String priceListId, @Valid @RequestBody AddPriceEntryRequest request) {
        PriceEntry entry = service.addPriceEntry(
                priceListId, new AddPriceEntryCommand(request.itemType(), request.itemRefId(), request.amount()));
        return ResponseEntity.created(URI.create("/api/catalog/price-lists/" + priceListId + "/entries/" + entry.entryId()))
                .body(PriceEntryResponse.from(entry));
    }

    @PutMapping("/{priceListId}")
    ResponseEntity<PriceListResponse> updatePriceList(
            @PathVariable String priceListId, @Valid @RequestBody UpdatePriceListRequest request) {
        PriceList updated = service.update(priceListId, new UpdatePriceListCommand(
                request.nameEn(), request.nameEs(), request.agreementRefId(), request.effectiveFrom(),
                request.effectiveTo()));
        return ResponseEntity.ok(toResponse(updated));
    }

    @PostMapping("/{priceListId}/publish")
    ResponseEntity<PriceListResponse> publishPriceList(@PathVariable String priceListId) {
        return ResponseEntity.ok(toResponse(service.publish(priceListId)));
    }

    @PostMapping("/{priceListId}/deprecate")
    ResponseEntity<PriceListResponse> deprecatePriceList(@PathVariable String priceListId) {
        return ResponseEntity.ok(toResponse(service.deprecate(priceListId)));
    }

    @GetMapping("/effective")
    ResponseEntity<PriceListResponse> getEffectivePriceSnapshot(
            @RequestParam String itemType,
            @RequestParam String itemRefId,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String agreementRefId,
            @RequestParam(required = false) String saleDate) {
        return ResponseEntity.ok(
                toResponse(service.getEffectivePriceSnapshot(itemType, itemRefId, currency, agreementRefId, saleDate)));
    }

    private PriceListResponse toResponse(PriceList entity) {
        return PriceListResponse.from(entity, service.getEntries(entity.priceListId()));
    }

    record CreatePriceListRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String code,
            @NotBlank String nameEn,
            @NotBlank String nameEs,
            @NotBlank String currency,
            String agreementRefId,
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveFrom,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveTo) {
    }

    record AddPriceEntryRequest(@NotBlank String itemType, @NotBlank String itemRefId, @NotNull BigDecimal amount) {
    }

    record UpdatePriceListRequest(
            String nameEn,
            String nameEs,
            String agreementRefId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveFrom,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveTo) {
    }

    record PriceEntryResponse(String entryId, String itemType, String itemRefId, String currency, BigDecimal amount) {
        static PriceEntryResponse from(PriceEntry entry) {
            return new PriceEntryResponse(
                    entry.entryId(), entry.itemType(), entry.itemRefId(), entry.price().currency(),
                    entry.price().amount());
        }
    }

    record PriceListResponse(
            String priceListId,
            String tenantId,
            String laboratoryId,
            String code,
            String nameEn,
            String nameEs,
            String currency,
            String agreementRefId,
            LocalDate effectiveFrom,
            LocalDate effectiveTo,
            List<PriceEntryResponse> entries,
            String status,
            int version,
            Instant createdAt,
            Instant updatedAt) {
        static PriceListResponse from(PriceList entity, List<PriceEntry> entries) {
            return new PriceListResponse(
                    entity.priceListId(),
                    entity.tenantId(),
                    entity.laboratoryId(),
                    entity.code(),
                    entity.name().en(),
                    entity.name().es(),
                    entity.currency(),
                    entity.agreementRefId(),
                    entity.effectiveFrom(),
                    entity.effectiveTo(),
                    entries.stream().map(PriceEntryResponse::from).toList(),
                    entity.status(),
                    entity.version(),
                    entity.createdAt(),
                    entity.updatedAt());
        }
    }
}
