package com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.application;

import java.util.List;

public record MarkAdmissionReadyCommand(String clinicalNotesDraft, List<CatalogSelectionInput> catalogSelection) {

    public record CatalogSelectionInput(String testDefinitionId, String catalogItemKind, Integer quantity) {
    }
}
