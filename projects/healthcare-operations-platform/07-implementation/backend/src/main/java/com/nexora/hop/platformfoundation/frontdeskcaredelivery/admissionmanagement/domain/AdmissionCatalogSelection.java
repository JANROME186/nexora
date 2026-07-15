package com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.domain;

/**
 * A published test or panel selected for the admission (VO-ADM-001), prior to becoming an order
 * line catalog snapshot when {@code CommitAdmissionRequest} runs.
 */
public record AdmissionCatalogSelection(
        String selectionId,
        String admissionId,
        String testDefinitionId,
        String catalogItemKind,
        int quantity) {

    public static final String KIND_TEST = "test";
    public static final String KIND_PANEL = "panel";
}
