package com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.application;

import java.util.List;

public record CreatePanelDefinitionCommand(
        String tenantId,
        String laboratoryId,
        String code,
        String nameEn,
        String nameEs,
        List<PanelMemberInput> members) {
}
