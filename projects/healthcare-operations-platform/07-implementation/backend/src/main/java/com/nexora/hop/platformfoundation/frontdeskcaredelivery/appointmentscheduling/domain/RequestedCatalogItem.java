package com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain;

/**
 * A published test or panel selected at scheduling time (VO-APT-001), referenced by id until
 * order creation captures its snapshot.
 */
public record RequestedCatalogItem(
        String itemId,
        String appointmentId,
        String testDefinitionId,
        String catalogItemKind) {

    public static final String KIND_TEST = "test";
    public static final String KIND_PANEL = "panel";
}
