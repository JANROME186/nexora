package com.nexora.hop.platformfoundation.organizationmanagement.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.OrganizationRepository;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.Tenant;

class OrganizationManagementServiceTest {

    private OrganizationRepository repository;
    private AuditRecorder auditRecorder;
    private OrganizationManagementService service;

    @BeforeEach
    void setUp() {
        repository = mock(OrganizationRepository.class);
        auditRecorder = mock(AuditRecorder.class);
        service = new OrganizationManagementService(repository, auditRecorder);
        when(repository.saveTenant(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void provisionTenantDefaultsTierToStarterAndRecordsAuditEvent() {
        when(repository.findTenantByCode("nexora")).thenReturn(Optional.empty());

        Tenant tenant = service.provisionTenant(new ProvisionTenantCommand("nexora", "Nexora Labs", null, null, null));

        assertThat(tenant.tier()).isEqualTo("STARTER");
        assertThat(tenant.status()).isEqualTo("PENDING_PROVISIONING");
        assertThat(tenant.tradeName()).isEqualTo("Nexora Labs");
        verify(auditRecorder).recordSystemEvent(
                eq(tenant.tenantId()), eq("TenantCreated"), eq("Tenant"), eq(tenant.tenantId()), anyString());
    }

    @Test
    void provisionTenantRejectsDuplicateCode() {
        when(repository.findTenantByCode("dup")).thenReturn(Optional.of(sampleTenant("dup")));

        assertThatThrownBy(() -> service.provisionTenant(new ProvisionTenantCommand("dup", "Dup Co", null, null, null)))
                .isInstanceOf(TenantCodeConflictException.class);
    }

    @Test
    void provisionTenantRejectsUnknownTier() {
        when(repository.findTenantByCode(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.provisionTenant(
                new ProvisionTenantCommand("code", "Legal Name", null, null, "NOT_A_TIER")))
                .isInstanceOf(InvalidOrganizationCommandException.class);
    }

    @Test
    void listTenantsReturnsRepositoryContents() {
        when(repository.findAllTenants()).thenReturn(List.of(sampleTenant("a"), sampleTenant("b")));

        assertThat(service.listTenants()).hasSize(2);
    }

    @Test
    void updateTenantStatusRecordsAuditEventWithPreviousAndNewStatus() {
        Tenant current = sampleTenant("triage");
        when(repository.findTenantById(current.tenantId())).thenReturn(Optional.of(current));
        Tenant suspended = new Tenant(
                current.tenantId(), current.code(), current.legalName(), current.tradeName(), current.taxId(),
                "SUSPENDED", current.tier(), current.isolationStrategy(), current.createdAt(), Instant.now());
        when(repository.updateTenantStatus(eq(current.tenantId()), eq("SUSPENDED"), any())).thenReturn(suspended);

        Tenant result = service.updateTenantStatus(
                new UpdateTenantStatusCommand(current.tenantId(), "suspended", "impact triage"));

        assertThat(result.status()).isEqualTo("SUSPENDED");
        verify(auditRecorder).recordSystemEvent(
                eq(current.tenantId()), eq("TenantStatusChanged"), eq("Tenant"), eq(current.tenantId()), anyString());
    }

    @Test
    void updateTenantStatusRejectsUnknownStatus() {
        Tenant current = sampleTenant("triage-invalid");
        when(repository.findTenantById(current.tenantId())).thenReturn(Optional.of(current));

        assertThatThrownBy(() -> service.updateTenantStatus(
                new UpdateTenantStatusCommand(current.tenantId(), "NOT_A_STATUS", null)))
                .isInstanceOf(InvalidOrganizationCommandException.class);
    }

    @Test
    void updateTenantStatusRejectsUnknownTenant() {
        when(repository.findTenantById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTenantStatus(new UpdateTenantStatusCommand("missing", "ACTIVE", null)))
                .isInstanceOf(OrganizationEntityNotFoundException.class);
    }

    private static Tenant sampleTenant(String code) {
        Instant now = Instant.now();
        return new Tenant(
                "tenant-" + code, code, "Legal " + code, "Trade " + code, "",
                "PENDING_PROVISIONING", "STARTER", "DISCRIMINATOR_WITH_RLS", now, now);
    }
}
