package com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain;

/** A member test reference within a panel (ENT-PNL-002). */
public record PanelMember(String memberId, String panelId, String testRefId, Integer displayOrder, boolean mandatory) {
}
