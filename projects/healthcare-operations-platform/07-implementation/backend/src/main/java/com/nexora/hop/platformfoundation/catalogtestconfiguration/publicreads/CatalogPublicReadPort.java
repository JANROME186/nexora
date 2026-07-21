package com.nexora.hop.platformfoundation.catalogtestconfiguration.publicreads;

import java.util.List;
import java.util.Optional;

/**
 * Anonymous, published-only projection over BCM-SVC-001/002/003/005 aggregates for the
 * COM-MOD-011 public website surface. Never exposes drafts, deprecated or retired records; the
 * DTOs deliberately omit tenantId, audit metadata and internal-only fields.
 */
public interface CatalogPublicReadPort {

    List<PublicDiagnosticServiceSnapshot> listPublishedDiagnosticServices(String laboratoryId);

    Optional<PublicDiagnosticServiceSnapshot> findPublishedDiagnosticServiceSnapshot(String serviceId);

    List<PublicTestSnapshot> listPublishedTests(String laboratoryId);

    Optional<PublicTestSnapshot> findPublishedTestSnapshot(String testDefinitionId);

    List<PublicPanelSnapshot> listPublishedPanels(String laboratoryId);

    Optional<PublicPanelSnapshot> findPublishedPanelSnapshot(String panelId);

    List<PublicPreparationSnapshot> listPublishedPreparations(String laboratoryId);

    Optional<PublicPreparationSnapshot> findPublishedPreparationSnapshot(String preparationId);

    record PublicDiagnosticServiceSnapshot(
            String serviceId, String code, String nameEn, String nameEs, String serviceType, int version) {
    }

    record PublicTestSnapshot(
            String testDefinitionId,
            String code,
            String nameEn,
            String nameEs,
            String methodology,
            String measurementUnit,
            String resultType,
            Integer turnaroundTimeHours,
            int version) {
    }

    record PublicPanelSnapshot(String panelId, String code, String nameEn, String nameEs, int version) {
    }

    record PublicPreparationSnapshot(
            String preparationId,
            String code,
            String titleEn,
            String titleEs,
            String instructionTextEn,
            String instructionTextEs,
            String category,
            Integer durationHours,
            int version) {
    }
}
