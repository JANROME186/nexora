package com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.application;

import java.util.List;

public record UpdatePanelDefinitionCommand(String code, String nameEn, String nameEs, List<PanelMemberInput> members) {
}
