package com.nexora.hop.platformfoundation.inventoryquality.productcatalog.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.application.InventoryItemService.RegisterInventoryItemCommand;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItemRepository;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InvalidInventoryCommandException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryConflictException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryErrorCodes;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the RN-001/RN-002/RN-004 rules of BCM-INV-001 Product Catalog. */
class InventoryItemServiceTest {

    private InventoryItemRepository repository;
    private TenantDirectory tenantDirectory;
    private InventoryItemService service;

    @BeforeEach
    void setUp() {
        repository = mock(InventoryItemRepository.class);
        tenantDirectory = mock(TenantDirectory.class);
        AuditRecorder auditRecorder = mock(AuditRecorder.class);
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        when(tenantDirectory.tenantExists(any())).thenReturn(true);
        when(repository.findByScopeAndCode(any(), any(), any(), any())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        service = new InventoryItemService(repository, tenantDirectory, auditRecorder, clock);
    }

    @Test
    void classificationCapitalEquipmentRequiresItemTypeEquipment() {
        assertThatThrownBy(
                () ->
                    service.registerItem(new RegisterInventoryItemCommand(
                        "t", "l", "b", "code1", "name", "reagent", "capital_equipment", "unit", "admin")))
                .isInstanceOf(InvalidInventoryCommandException.class)
                .satisfies(
                        exception ->
                                assertThat(((InvalidInventoryCommandException) exception).code())
                                        .isEqualTo(InventoryErrorCodes.INVENTORY_ITEM_TYPE_CLASSIFICATION_MISMATCH));
    }

    @Test
    void classificationDiagnosticReagentAcceptsItemTypeReagent() {
        InventoryItem item = service.registerItem(new RegisterInventoryItemCommand(
                "t", "l", "b", "code1", "name", "reagent", "diagnostic_reagent", "unit", "admin"));
        assertThat(item.status()).isEqualTo(InventoryItem.STATUS_ACTIVE);
    }

    @Test
    void duplicateCodeInSameScopeIsRejected() {
        InventoryItem existing = new InventoryItem(
                "id", "t", "l", "b", "code1", "name", "reagent", "diagnostic_reagent", "unit", "active",
                com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.StockSummary.empty(),
                null, null,
                new com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata(
                        "a", java.time.LocalDateTime.now(), "a", java.time.LocalDateTime.now()));
        when(repository.findByScopeAndCode("t", "l", "b", "code1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(
                () ->
                    service.registerItem(new RegisterInventoryItemCommand(
                        "t", "l", "b", "code1", "name", "reagent", "diagnostic_reagent", "unit", "admin")))
                .isInstanceOf(InventoryConflictException.class)
                .satisfies(
                        exception ->
                                assertThat(((InventoryConflictException) exception).code())
                                        .isEqualTo(InventoryErrorCodes.INVENTORY_ITEM_CODE_NOT_UNIQUE));
    }

    @Test
    void requireActiveItemRejectsDiscontinuedInstance() {
        String id = "abc";
        InventoryItem discontinued = new InventoryItem(
                id, "t", "l", "b", "code1", "name", "reagent", "diagnostic_reagent", "unit", "discontinued",
                com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.StockSummary.empty(),
                null, null,
                new com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata(
                        "a", java.time.LocalDateTime.now(), "a", java.time.LocalDateTime.now()));
        when(repository.findById(id)).thenReturn(Optional.of(discontinued));

        assertThatThrownBy(() -> service.requireActiveItem(id, InventoryErrorCodes.INVENTORY_ITEM_DISCONTINUED))
                .isInstanceOf(InventoryConflictException.class);
    }
}
