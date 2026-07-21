package com.nexora.hop.platformfoundation.catalogtestconfiguration.publicreads;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.application.DiagnosticServiceCatalogService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.domain.DiagnosticService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.application.PanelCatalogService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain.PanelDefinition;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.application.PatientPreparationManagementService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain.PreparationInstruction;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogEntityNotFoundException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.application.TestCatalogService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestDefinition;

/**
 * Default in-module implementation of {@link CatalogPublicReadPort}. Delegates to the existing
 * MVP-MOD-002 catalog services and enforces the published-only invariant per capability, so
 * downstream (publicweb) consumers never see draft, deprecated or retired records.
 */
@Component
class CatalogPublicReadAdapter implements CatalogPublicReadPort {

    private final DiagnosticServiceCatalogService diagnosticServiceCatalogService;
    private final TestCatalogService testCatalogService;
    private final PanelCatalogService panelCatalogService;
    private final PatientPreparationManagementService preparationManagementService;

    CatalogPublicReadAdapter(
            DiagnosticServiceCatalogService diagnosticServiceCatalogService,
            TestCatalogService testCatalogService,
            PanelCatalogService panelCatalogService,
            PatientPreparationManagementService preparationManagementService) {
        this.diagnosticServiceCatalogService = diagnosticServiceCatalogService;
        this.testCatalogService = testCatalogService;
        this.panelCatalogService = panelCatalogService;
        this.preparationManagementService = preparationManagementService;
    }

    @Override
    public List<PublicDiagnosticServiceSnapshot> listPublishedDiagnosticServices(String laboratoryId) {
        return diagnosticServiceCatalogService.listPublished(laboratoryId).stream()
                .map(CatalogPublicReadAdapter::toSnapshot)
                .toList();
    }

    @Override
    public Optional<PublicDiagnosticServiceSnapshot> findPublishedDiagnosticServiceSnapshot(String serviceId) {
        try {
            DiagnosticService snapshot = diagnosticServiceCatalogService.getPublishedSnapshot(serviceId);
            if (!DiagnosticService.STATUS_PUBLISHED.equals(snapshot.status())) {
                return Optional.empty();
            }
            return Optional.of(toSnapshot(snapshot));
        } catch (CatalogEntityNotFoundException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<PublicTestSnapshot> listPublishedTests(String laboratoryId) {
        return testCatalogService.listPublished(laboratoryId).stream()
                .map(CatalogPublicReadAdapter::toSnapshot)
                .toList();
    }

    @Override
    public Optional<PublicTestSnapshot> findPublishedTestSnapshot(String testDefinitionId) {
        try {
            TestDefinition snapshot = testCatalogService.getPublishedSnapshot(testDefinitionId);
            if (!TestDefinition.STATUS_PUBLISHED.equals(snapshot.status())) {
                return Optional.empty();
            }
            return Optional.of(toSnapshot(snapshot));
        } catch (CatalogEntityNotFoundException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<PublicPanelSnapshot> listPublishedPanels(String laboratoryId) {
        return panelCatalogService.listPublished(laboratoryId).stream()
                .map(CatalogPublicReadAdapter::toSnapshot)
                .toList();
    }

    @Override
    public Optional<PublicPanelSnapshot> findPublishedPanelSnapshot(String panelId) {
        try {
            PanelDefinition snapshot = panelCatalogService.getPublishedSnapshot(panelId);
            if (!PanelDefinition.STATUS_PUBLISHED.equals(snapshot.status())) {
                return Optional.empty();
            }
            return Optional.of(toSnapshot(snapshot));
        } catch (CatalogEntityNotFoundException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<PublicPreparationSnapshot> listPublishedPreparations(String laboratoryId) {
        return preparationManagementService.listPublished(laboratoryId).stream()
                .map(CatalogPublicReadAdapter::toSnapshot)
                .toList();
    }

    @Override
    public Optional<PublicPreparationSnapshot> findPublishedPreparationSnapshot(String preparationId) {
        try {
            PreparationInstruction snapshot = preparationManagementService.getPublishedSnapshot(preparationId);
            if (!PreparationInstruction.STATUS_PUBLISHED.equals(snapshot.status())) {
                return Optional.empty();
            }
            return Optional.of(toSnapshot(snapshot));
        } catch (CatalogEntityNotFoundException ex) {
            return Optional.empty();
        }
    }

    private static PublicDiagnosticServiceSnapshot toSnapshot(DiagnosticService entity) {
        return new PublicDiagnosticServiceSnapshot(entity.serviceId(), entity.code(), entity.name().en(),
                entity.name().es(), entity.serviceType(), entity.version());
    }

    private static PublicTestSnapshot toSnapshot(TestDefinition entity) {
        return new PublicTestSnapshot(entity.testDefinitionId(), entity.code(), entity.name().en(),
                entity.name().es(), entity.methodology(), entity.measurementUnit(), entity.resultType(),
                entity.turnaroundTimeHours(), entity.version());
    }

    private static PublicPanelSnapshot toSnapshot(PanelDefinition entity) {
        return new PublicPanelSnapshot(entity.panelId(), entity.code(), entity.name().en(),
                entity.name().es(), entity.version());
    }

    private static PublicPreparationSnapshot toSnapshot(PreparationInstruction entity) {
        return new PublicPreparationSnapshot(entity.preparationId(), entity.code(), entity.title().en(),
                entity.title().es(), entity.instructionText().en(), entity.instructionText().es(),
                entity.category(), entity.durationHours(), entity.version());
    }
}
